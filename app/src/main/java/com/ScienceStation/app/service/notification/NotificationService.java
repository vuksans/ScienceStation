package com.ScienceStation.app.service.notification;

import com.ScienceStation.app.component.EmailSender;
import com.ScienceStation.app.controller.notification.dto.request.*;
import com.ScienceStation.app.controller.notification.dto.response.MainEditorAcceptJournalResponseDTO;
import com.ScienceStation.app.model.*;
import com.ScienceStation.app.model.enumeration.JournalStatus;
import com.ScienceStation.app.model.enumeration.NotificationStatus;
import com.ScienceStation.app.model.enumeration.NotificationType;
import com.ScienceStation.app.repository.JournalRepository;
import com.ScienceStation.app.repository.NotificationRepository;
import com.ScienceStation.app.repository.UserRepository;
import com.ScienceStation.app.service.exception.InvalidUserException;
import com.ScienceStation.app.service.exception.InvalidValueException;
import com.ScienceStation.app.service.exception.NoReviewException;
import com.ScienceStation.app.service.journalReviewTask.JournalReviewTaskService;
import com.ScienceStation.app.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    private final JournalRepository journalRepository;

    private final EmailSender emailSender;

    private final UserRepository userRepository;

    private final UserService userService;

    private final JournalReviewTaskService journalReviewTaskService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public NotificationService(NotificationRepository notificationRepository,EmailSender emailSender,JournalRepository journalRepository,
                               UserRepository userRepository,UserService userService,JournalReviewTaskService journalReviewTaskService) {
        this.notificationRepository = notificationRepository;
        this.emailSender = emailSender;
        this.journalRepository = journalRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.journalReviewTaskService = journalReviewTaskService;
    }
    ///WORKS!!!
    public List<Notification> getAllActive(User user){
        return notificationRepository.findAllByUser_IdAndStatus(user.getId(),NotificationStatus.PENDING);
    }
    //WORKS!!!
    public Notification notificationForCreatedJournal(Journal j){
        Notification notification = new Notification();

        notification.setJournal(j);
        notification.setStatus(NotificationStatus.PENDING);
        notification.setUser(j.getMagazine().getMainEditor());
        notification.setNotificationType(NotificationType.CREATED_JOURNAL);
        notification.setReason("Author "+j.getAuthor().getFirstName() +" "+j.getAuthor().getLastName()+" created a journal for "+j.getMagazine().getName()+" you should review it");

        //notificaion emails
        emailSender.sendNotificationToUser(j.getAuthor(),"Your Journal has been accepted and is pending review");
        emailSender.sendNotificationToUser(j.getMagazine().getMainEditor(),"You have a new journal request to review!");

        notificationRepository.save(notification);
        return notification;
    }

    //WORKS!!!
    public Journal acceptJournal(Long notificationId,User user) throws NoReviewException,InvalidUserException,InvalidValueException{
        Optional<Notification> notification = notificationRepository.findById(notificationId);

        if(!notification.isPresent())
            throw new InvalidValueException();
        if(notification.get().getStatus().equals(NotificationStatus.CLOSED))
            throw new InvalidValueException();
        if(!notification.get().getUser().getId().equals(user.getId()) || !notification.get().getJournal().getEditor().getId().equals(user.getId())){
            throw new InvalidUserException();
        }

        /*Let the journal editor accept the Journal if there are no reviewers*/
        if (userService.getReviewers(notification.get().getJournal().getMagazine().getId(), notification.get().getJournal().getBranch().getId()).isEmpty()) {
            closeNotification(notification.get());
            notification.get().getJournal().setStatus(JournalStatus.ACCEPTED);
            journalRepository.save(notification.get().getJournal());
            emailSender.sendNotificationToUser(notification.get().getJournal().getAuthor(), "Your Journal " + notification.get().getJournal().getHeadline() + " has been accepted. Congrats!");
            return notification.get().getJournal();
        }
        else {
            try {
                journalReviewTaskService.closeJournalReviewTask(journalReviewTaskService.findTaskFromJournal(notification.get().getJournal()));
                closeNotification(notification.get());
                notification.get().getJournal().setStatus(JournalStatus.ACCEPTED);
                journalRepository.save(notification.get().getJournal());
                emailSender.sendNotificationToUser(notification.get().getJournal().getAuthor(), "Your Journal " + notification.get().getJournal().getHeadline() + " has been accepted. Congrats!");
                return notification.get().getJournal();
            } catch (InvalidValueException e) {
                throw new NoReviewException();
            }
        }
    }

    //WORKS!
    public void declineJournal(DeclineJournalRequestDTO requestDTO, User user) throws InvalidUserException,InvalidValueException{
        Notification notification = notificationRepository.findById(requestDTO.getNotificationId()).get();
        //provera da li korisnik koji pristupa endpointu je onaj ciji je zadatak
        if(!notification.getUser().getId().equals(user.getId()) || !notification.getJournal().getEditor().getId().equals(user.getId()))
            throw new InvalidUserException();

        if(notification.getStatus() == NotificationStatus.CLOSED || notification.getJournal().getStatus()==JournalStatus.DECLINED){
            throw new InvalidValueException();
        }


        emailSender.sendNotificationToUser(notification.getJournal().getAuthor(),"Your journal "+notification.getJournal().getHeadline()+
                " does not meet our criteria. Reason: "+requestDTO.getReason()+" The Journal has been declined and all corresponding processes have been terminated");

        closeNotification(notification);

        notification.getJournal().setStatus(JournalStatus.DECLINED);
        journalRepository.save(notification.getJournal());

        try {
            journalReviewTaskService.closeJournalReviewTask(journalReviewTaskService.findTaskFromJournal(notification.getJournal()));
        }catch (InvalidValueException e){
            logger.info("Closed the journal "+notification.getJournal().getHeadline()+" without sending it to be reviewed!");
        }
    }


    public Notification sendBackJournal(SendJournalBackDTO requestDTO, User user) throws InvalidUserException{
        Notification notification = notificationRepository.findById(requestDTO.getNotificationId()).get();
        //provera da li korisnik koji pristupa endpointu je onaj ciji je zadatak
        if(!notification.getUser().equals(user) || !notification.getJournal().getEditor().equals(user))
            throw new InvalidUserException();

        closeNotification(notification);
        notification.getJournal().setStatus(JournalStatus.RETURNED_BACK);

        emailSender.sendNotificationToUser(notification.getJournal().getAuthor(),"Your journal "+notification.getJournal().getHeadline()+
                " needs some more work. Reason: "+requestDTO.getReason()+" Please change your journal up until the date of "+requestDTO.getDateTime().toString());

        journalRepository.save(notification.getJournal());

        Notification newNotification = new Notification();
        newNotification.setDeadline(requestDTO.getDateTime());
        newNotification.setStatus(NotificationStatus.PENDING);
        newNotification.setJournal(notification.getJournal());
        newNotification.setReason(requestDTO.getReason());
        newNotification.setUser(notification.getJournal().getAuthor());

        newNotification.setNotificationType(NotificationType.JOURNAL_RETURNED_WITH_NO_REVIEW);


        notificationRepository.save(newNotification);
        return newNotification;
    }

    //WORKS!!!
    public Notification authorChanged(AuthorChangesRequestDTO requestDTO,User user) throws InvalidValueException,InvalidUserException{
        Optional<Notification> notification = notificationRepository.findById(requestDTO.getNotificationId());

        if(!notification.isPresent())
            throw new InvalidValueException();
        if(notification.get().getStatus().equals(NotificationStatus.CLOSED))
            throw new InvalidValueException();
        if(!notification.get().getUser().getId().equals(user.getId()))
            throw new InvalidUserException();

        closeNotification(notification.get());

        Notification newNotification = new Notification();
        newNotification.setNotificationType(notification.get().getNotificationType());
        newNotification.setReason(requestDTO.getChanges());
        newNotification.setJournal(notification.get().getJournal());
        newNotification.setUser(notification.get().getJournal().getEditor());
        newNotification.setStatus(NotificationStatus.PENDING);

        notificationRepository.save(newNotification);

        return newNotification;
    }

    ///WORKS!!!
    public void sendBackForReviews(SendAgainToReviewRequestDTO requestDTO,User user) throws InvalidValueException,InvalidUserException {
        Optional<Notification> notification = notificationRepository.findById(requestDTO.getNotificationId());
        if (!notification.isPresent())
            throw new InvalidValueException();
        if(notification.get().getStatus().equals(NotificationStatus.CLOSED))
            throw new InvalidValueException();
        if (!notification.get().getUser().getId().equals(user.getId()))
            throw new InvalidUserException();

        closeNotification(notification.get());

        JournalReviewTask journalReviewTask = journalReviewTaskService.findTaskFromJournal(notification.get().getJournal());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        //First the notification for all reviewers
        for(Comment comment : journalReviewTask.getComments()){

            Notification newNotification = new Notification();

            newNotification.setUser(comment.getCreator());
            newNotification.setReason(notification.get().getReason());
            newNotification.setJournal(notification.get().getJournal());
            newNotification.setStatus(NotificationStatus.PENDING);
            newNotification.setNotificationType(NotificationType.REVIEW_JOURNAL);
            newNotification.setDeadline(LocalDateTime.parse(requestDTO.getDeadLine(),formatter));

            notificationRepository.save(newNotification);
        }

        //Then, close the JournalTaskReview and make a new one
        journalReviewTaskService.createNewFromOld(notification.get().getJournal());
    }

    //WORKS!!!
    public Notification sendBackAfterReview(SendBackAfterReviewRequestDTO requestDTO,User user) throws InvalidUserException,InvalidValueException{
        Optional<Notification> notification = notificationRepository.findById(requestDTO.getNotificationId());

        if(!notification.isPresent())
            throw new InvalidValueException();
        if(notification.get().getStatus().equals(NotificationStatus.CLOSED))
            throw new InvalidValueException();
        if(!notification.get().getUser().getId().equals(user.getId()))
            throw new InvalidUserException();

        closeNotification(notification.get());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        Notification newNotification = new Notification();
        newNotification.setUser(notification.get().getJournal().getAuthor());
        newNotification.setReason("Hey, we have reviewed your Journal and you need to make some changes we have sent you the details!");
        newNotification.setStatus(NotificationStatus.PENDING);
        newNotification.setJournal(notification.get().getJournal());
        newNotification.setDeadline(LocalDateTime.parse(requestDTO.getDeadLine(),formatter));
        if(requestDTO.isChangeType())
            newNotification.setNotificationType(NotificationType.JOURNAL_RETURNED_WITH_REVIEW_BIG);
        else
            newNotification.setNotificationType(NotificationType.JOURNAL_RETURNED__WITH_REVIEW_SMALL);

        notificationRepository.save(newNotification);

        return newNotification;

    }

    public MainEditorAcceptJournalResponseDTO confirmMainEditorJournal(Long notificationId, User user) throws InvalidUserException,InvalidValueException{
        Optional<Notification> notificationOptional = notificationRepository.findById(notificationId);


        if(!notificationOptional.isPresent())
            throw new InvalidValueException();

        Notification notification = notificationOptional.get();

        //why editors review?
        if(notification.getStatus().equals(NotificationStatus.CLOSED))
            throw new InvalidValueException();

        if(!notification.getUser().getId().equals(user.getId()) || !notification.getJournal().getEditor().getId().equals(user.getId()))
            throw new InvalidUserException();

        closeNotification(notification);

        /*Before anything I'll check if there are any reviewers for that magazines branch if not i'll create a new notification, hence the only reviewer is the main editor*/
        try {
            if (userService.getReviewers(notification.getJournal().getMagazine().getId(), notification.getJournal().getBranch().getId()).isEmpty()){
                Notification mainEditorNotification = new Notification();
                mainEditorNotification.setUser(user);
                mainEditorNotification.setStatus(NotificationStatus.PENDING);
                mainEditorNotification.setJournal(notification.getJournal());
                mainEditorNotification.setNotificationType(NotificationType.REVIEW_JOURNAL);
                mainEditorNotification.setReason("There are no reviewers for that magazine and branch you will have to review it yourself");
                notificationRepository.save(mainEditorNotification);
                return new MainEditorAcceptJournalResponseDTO(false,"No reviewers found!");
            }
        }catch (InvalidValueException e){
            throw new InvalidUserException();
        }

        boolean flag = false;
        String reason = "";
        for(int i=0;i<notification.getJournal().getMagazine().getEditors().size();i++){
            if(notification.getJournal().getMagazine().getEditors().get(i).getBranch().equals(notification.getJournal().getBranch())){
                //If this passes that means that there is a editor in that magazine with the branch as same as the journal so he has to pick the reviewers
                //thus he will receive the new notification
                Notification newNotification = new Notification();
                newNotification.setStatus(NotificationStatus.PENDING);
                newNotification.setJournal(notification.getJournal());
                newNotification.setNotificationType(NotificationType.PICK_REVIEWERS);
                reason="Please choose reviewers to review this journal!";
                newNotification.setReason(reason);
                newNotification.setUser(notification.getJournal().getMagazine().getEditors().get(i));
                notificationRepository.save(newNotification);
                flag = true;

                newNotification.getJournal().setEditor(notification.getJournal().getMagazine().getEditors().get(i));
                journalRepository.save(newNotification.getJournal());
                break;
            }
        }
        if(!flag){
            //if this code runs that means there are no editors for that branch in the magazine so the main editor has to choose the reviewers
            Notification newNotification = new Notification();
            newNotification.setStatus(NotificationStatus.PENDING);
            newNotification.setJournal(notification.getJournal());
            newNotification.setNotificationType(NotificationType.PICK_REVIEWERS);
            reason="There are no editors that that journals branch. Please choose the editors yourself";
            newNotification.setReason("There are no editors that that journals branch. Please choose the editors yourself");
            newNotification.setUser(user);
            notificationRepository.save(newNotification);
        }
        return new MainEditorAcceptJournalResponseDTO(flag,reason);
    }

    //WORKS!!!
    public void chooseReviewer(ChooseReviewerRequestDTO requestDTO,User u) throws InvalidValueException,InvalidUserException{
        Optional<Notification> notification = notificationRepository.findById(requestDTO.getNotificationId());
        Optional<User> reviewer = userRepository.findUserById(requestDTO.getReviewerId());


        if(!notification.isPresent())
            throw new InvalidValueException();
        if(notification.get().getStatus().equals(NotificationStatus.CLOSED))
            throw new InvalidValueException();

        if(!reviewer.isPresent() || !notification.get().getUser().getId().equals(u.getId()))
            throw new InvalidUserException();

        List<User> availableUsers = userService.getAvailableReviews(notification.get().getJournal().getId());
        boolean flag = false;
        for(User tempUser:availableUsers){
            if(tempUser.getId().equals(reviewer.get().getId()))
                flag = true;
        }
        if(!flag)
            throw new InvalidUserException();


        /*Need to check if there is a active JournalReviewTask for this Journal if there is I just need to update the taskCount on it*/
        JournalReviewTask task = journalReviewTaskService.addReviewer(notification.get().getJournal(),u);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        Notification newNotification = new Notification();

        newNotification.setUser(reviewer.get());
        newNotification.setReason(requestDTO.getReason());
        newNotification.setJournal(notification.get().getJournal());
        newNotification.setStatus(NotificationStatus.PENDING);
        newNotification.setDeadline(LocalDateTime.parse(requestDTO.getDeadLine(),formatter));
        newNotification.setNotificationType(NotificationType.REVIEW_JOURNAL);
        notificationRepository.save(newNotification);

        //nema vise reviewera samo taj jedan tako da zatvori ovo
        if(availableUsers.size() == 1)
            closeNotification(notification.get());
    }
    //WORKS!!!
    public void cancelPickingReviewers(Long notificationId,User u) throws InvalidUserException,InvalidValueException{
        Optional<Notification> notificationOptional = notificationRepository.findById(notificationId);

        if(!notificationOptional.isPresent())
            throw new InvalidValueException();

        if(notificationOptional.get().getStatus().equals(NotificationStatus.CLOSED))
            throw new InvalidValueException();

        if(!notificationOptional.get().getUser().getId().equals(u.getId()))
            throw new InvalidUserException();

        closeNotification(notificationOptional.get());
    }
    //WORKS!!!
    public Notification closeNotification(Notification notification){
        notification.setStatus(NotificationStatus.CLOSED);
        notificationRepository.save(notification);
        return notification;
    }
    ///WORKS!!!
    public Notification reviewComplete(JournalReviewTask journalReviewTask){
        Notification notification = new Notification();
        notification.setStatus(NotificationStatus.PENDING);
        notification.setNotificationType(NotificationType.REVIEW_DONE);
        notification.setUser(journalReviewTask.getCreator());
        notification.setJournal(journalReviewTask.getJournal());
        notification.setReason("Reviewers have finished their work. Time to see what they have to say!");

        notificationRepository.save(notification);
        return notification;
    }
    //TODO:: Ako nema vise reviewera samo ugasi ovaj review
    public Notification notifyToRePickReviewer(Journal journal) throws InvalidValueException{
        try {
            JournalReviewTask journalReviewTask = journalReviewTaskService.findTaskFromJournal(journal);
            Notification notification = new Notification();
            notification.setUser(journalReviewTask.getCreator());
            notification.setReason("You need to pick a new reviewer!");
            notification.setNotificationType(NotificationType.REPICK_REVIEWER);
            notification.setStatus(NotificationStatus.PENDING);
            notification.setJournal(journal);
            notificationRepository.save(notification);
            return notification;
        }catch (InvalidValueException e) {
            throw new InvalidValueException();
        }
    }
    /*The difference from this function and the pickReviewer is here im not updating the count on the JournalReviewTask im just sending a new Notification to a reviewer that he
    * needs to review a journal*/
    public Notification rePickReviewer(ChooseReviewerRequestDTO requestDTO,User u)throws InvalidValueException,InvalidUserException{
        Optional<Notification> notification = notificationRepository.findById(requestDTO.getNotificationId());
        Optional<User> reviewer = userRepository.findUserById(requestDTO.getReviewerId());

        closeNotification(notification.get());

        if(!notification.isPresent())
            throw new InvalidValueException();
        if(!reviewer.isPresent() || !notification.get().getUser().equals(u))
            throw new InvalidUserException();

        Notification newNotification = new Notification();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        newNotification.setUser(reviewer.get());
        newNotification.setReason(requestDTO.getReason());
        newNotification.setJournal(notification.get().getJournal());
        newNotification.setStatus(NotificationStatus.PENDING);
        newNotification.setDeadline(LocalDateTime.parse(requestDTO.getDeadLine(),formatter));
        newNotification.setNotificationType(NotificationType.REVIEW_JOURNAL);
        notificationRepository.save(newNotification);

        return newNotification;
    }

}
