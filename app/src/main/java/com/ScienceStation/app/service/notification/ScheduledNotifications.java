package com.ScienceStation.app.service.notification;

import com.ScienceStation.app.component.EmailSender;
import com.ScienceStation.app.model.Notification;
import com.ScienceStation.app.model.enumeration.JournalStatus;
import com.ScienceStation.app.model.enumeration.NotificationStatus;
import com.ScienceStation.app.model.enumeration.NotificationType;
import com.ScienceStation.app.repository.JournalRepository;
import com.ScienceStation.app.repository.NotificationRepository;
import com.ScienceStation.app.service.exception.InvalidValueException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScheduledNotifications {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private JournalRepository journalRepository;

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private NotificationService notificationService;

    /*This function will use scheduled times to figure out of a notification has ended*/
    @Scheduled(cron = "0 * * * * *")//every hour
    public void doScheduledWork(){
        logger.info("Scheduled work!!!");
        List<Notification> notificationList = notificationRepository.findAllByDeadlineBeforeAndStatus(LocalDateTime.now(),NotificationStatus.PENDING);
        findAuthorExpiredNotifications(notificationList);
        findReviewerExpiredNotifications(notificationList);

    }
    public void findAuthorExpiredNotifications(List<Notification> notificationList){
        List<Notification> notifications = new ArrayList<>();
        for(Notification n:notificationList){
                if(n.getNotificationType() == NotificationType.JOURNAL_RETURNED_WITH_NO_REVIEW ||
                        n.getNotificationType() == NotificationType.JOURNAL_RETURNED__WITH_REVIEW_SMALL || n.getNotificationType() == NotificationType.JOURNAL_RETURNED_WITH_REVIEW_BIG )
                notifications.add(n);
        }
        for(Notification n:notifications){
            n.setStatus(NotificationStatus.CLOSED);
            n.getJournal().setStatus(JournalStatus.DECLINED);
            emailSender.sendNotificationToUser(n.getJournal().getAuthor(),"Your time to correct the suggestions on the journal "+n.getJournal().getHeadline()+
                    " has expired. The Journal has been declined and all corresponding processes have been terminated");
            journalRepository.save(n.getJournal());
        }
        notificationRepository.saveAll(notifications);
    }

    public void findReviewerExpiredNotifications(List<Notification> notificationList){
        List<Notification> notifications = new ArrayList<>();
        for(Notification n:notificationList){
            if(n.getNotificationType()== NotificationType.REVIEW_JOURNAL)
                notifications.add(n);
        }
        for(Notification n:notifications){
            n.setStatus(NotificationStatus.CLOSED);
            /*emailSender.sendNotificationToUser(n.getJournal().getMagazine().getMainEditor(),"The reviewer "+n.getUser().getLastName() + " "+n.getUser().getLastName()+
            " didn't manage to finish this work on the journal "+n.getJournal().getHeadline()+" of the magazine "+n.getJournal().getMagazine().getName()+" in time, please pick a new editor" +
                    "and assign him the task");*/
            try{
                notificationService.notifyToRePickReviewer(n.getJournal());
            }catch (InvalidValueException e){
                return ;
            }

        }
        notificationRepository.saveAll(notifications);

    }
}
