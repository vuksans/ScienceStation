package com.ScienceStation.app.service.comment;

import com.ScienceStation.app.controller.comment.dto.request.CreateCommentDTO;
import com.ScienceStation.app.controller.comment.dto.response.CommentToAuthorResponseDTO;
import com.ScienceStation.app.model.*;
import com.ScienceStation.app.model.enumeration.NotificationStatus;
import com.ScienceStation.app.model.enumeration.NotificationType;
import com.ScienceStation.app.model.enumeration.Role;
import com.ScienceStation.app.repository.CommentRepository;
import com.ScienceStation.app.repository.JournalRepository;
import com.ScienceStation.app.repository.NotificationRepository;
import com.ScienceStation.app.service.exception.InvalidUserException;
import com.ScienceStation.app.service.exception.InvalidValueException;
import com.ScienceStation.app.service.journalReviewTask.JournalReviewTaskService;
import com.ScienceStation.app.service.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    private final NotificationRepository notificationRepository;

    private final JournalReviewTaskService journalReviewTaskService;

    private final JournalRepository journalRepository;

    private final NotificationService notificationService;
    @Autowired
    public CommentService(CommentRepository commentRepository, NotificationRepository notificationRepository, JournalReviewTaskService journalReviewTaskService,
                          NotificationService notificationService,JournalRepository journalRepository) {
        this.commentRepository = commentRepository;
        this.notificationRepository = notificationRepository;
        this.journalReviewTaskService = journalReviewTaskService;
        this.notificationService = notificationService;
        this.journalRepository = journalRepository;
    }
    //WORKS!!!
    public Comment createComment(CreateCommentDTO createCommentDTO, User user)throws InvalidValueException,InvalidUserException{
        Optional<Notification> notificationOptional = notificationRepository.findById(createCommentDTO.getNotificationId());

        if(!notificationOptional.isPresent())
            throw new InvalidValueException();
        if(notificationOptional.get().getStatus().equals(NotificationStatus.CLOSED))
            throw new InvalidValueException();
        if(!notificationOptional.get().getUser().getId().equals(user.getId()))
            throw new InvalidUserException();


        notificationService.closeNotification(notificationOptional.get());

        Comment comment = new Comment();
        comment.setCreator(user);
        comment.setComment(createCommentDTO.getComment());
        comment.setHiddenComment(createCommentDTO.getHiddenComment());

        JournalReviewTask journalReviewTask = journalReviewTaskService.findTaskFromJournal(notificationOptional.get().getJournal());
        journalReviewTask.getComments().add(comment);

        comment.setJournalReviewTask(journalReviewTask);

        commentRepository.save(comment);

        /*Need to check now if that comment was the last comment needed because if it was, I need to create a new notification saying that the review is done*/
        Optional<Notification> notificationOptional2 = notificationRepository.findByJournal_IdAndStatusAndNotificationType(notificationOptional.get().getJournal().getId(),
                NotificationStatus.PENDING, NotificationType.PICK_REVIEWERS);
        if(journalReviewTask.getComments().size() == journalReviewTask.getNumberOfTasks() && !notificationOptional2.isPresent()) {
            notificationService.reviewComplete(journalReviewTask);
            //journalReviewTaskService.closeJournalReviewTask(journalReviewTask);
        }


        return comment;
    }
    //TODO:Maybe put that the user can't see the comments while he didnt receive the notification
    public List<?> getCommentsForJournal(Long journalId,User user)throws InvalidValueException,InvalidUserException{
        Optional<Journal> journalOptional = journalRepository.findById(journalId);
        if(!journalOptional.isPresent())
            throw new InvalidValueException();
        if(!journalOptional.get().getEditor().getId().equals(user.getId()) && !journalOptional.get().getAuthor().getId().equals(user.getId()))
            throw new InvalidUserException();
        try{
            List<Comment> comments =  journalReviewTaskService.findTaskFromJournal(journalOptional.get()).getComments();
            if(user.getRole().equals(Role.ROLE_AUTHOR)){
                List<CommentToAuthorResponseDTO> userComments = new ArrayList<>();
                for(Comment c : comments){
                    CommentToAuthorResponseDTO authorComment = new CommentToAuthorResponseDTO();
                    authorComment.setComment(c.getComment());
                    authorComment.setId(c.getId());
                    userComments.add(authorComment);
                }
                return userComments;
            }
            return comments;
        }catch (InvalidValueException e){
            throw e;
        }

    }
}
