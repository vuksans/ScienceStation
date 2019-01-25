package com.ScienceStation.app.controller.notification;

import com.ScienceStation.app.controller.notification.dto.request.*;
import com.ScienceStation.app.controller.notification.dto.response.MainEditorAcceptJournalResponseDTO;
import com.ScienceStation.app.model.User;
import com.ScienceStation.app.service.exception.InvalidUserException;
import com.ScienceStation.app.service.exception.InvalidValueException;
import com.ScienceStation.app.service.exception.NoReviewException;
import com.ScienceStation.app.service.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RequestMapping(value="/all",method = RequestMethod.GET)
    public ResponseEntity<?> getAllNotificationsForUser(){
        User u = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok().body(notificationService.getAllActive(u));
    }

    @RequestMapping(value="/declineJournal",method = RequestMethod.POST)
    @PreAuthorize("hasAnyRole('ADMIN','MAIN_EDITOR')")
    public ResponseEntity<?> declineJournal(@RequestBody DeclineJournalRequestDTO requestDTO){
        try {
            notificationService.declineJournal(requestDTO, (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            return ResponseEntity.ok().build();
        }catch (InvalidUserException e){
            return ResponseEntity.status(403).body(e.getMessage());
        }catch (InvalidValueException e){
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @RequestMapping(value="/sendBackJournal",method = RequestMethod.POST)
    @PreAuthorize("hasAnyRole('ADMIN','MAIN_EDITOR')")
    public ResponseEntity<?> sendBackJournal(@RequestBody SendJournalBackDTO requestDTO){
        try {
            notificationService.sendBackJournal(requestDTO, (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            return ResponseEntity.ok().build();
        }catch (InvalidUserException e){
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @RequestMapping(value="/mainEditorAccept/{id}",method = RequestMethod.GET)
    @PreAuthorize("hasAnyRole('ADMIN','MAIN_EDITOR')")
    public ResponseEntity<?> mainEditorAcceptJournal(@PathVariable("id") Long notificationId){
        try{
            MainEditorAcceptJournalResponseDTO responseDTO =notificationService.confirmMainEditorJournal(notificationId,(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            return ResponseEntity.ok().body(responseDTO);
        }catch (InvalidUserException e){
            return ResponseEntity.status(403).body(e.getMessage());
        }catch (InvalidValueException e){
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @RequestMapping(value="/chooseReviewer",method = RequestMethod.POST)
    @PreAuthorize("hasAnyRole('ADMIN','MAIN_EDITOR','EDITOR')")
    public ResponseEntity<?> chooseReviewer(@RequestBody ChooseReviewerRequestDTO requestDTO){
        try{
            notificationService.chooseReviewer(requestDTO,(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            return ResponseEntity.ok().build();
        }catch (InvalidValueException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch (InvalidUserException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @RequestMapping(value="/cancelReviewerPick/{id}",method = RequestMethod.GET)
    @PreAuthorize("hasAnyRole('ADMIN','MAIN_EDITOR','EDITOR')")
    public ResponseEntity<?> cancelTaskToPickReviewers(@PathVariable("id") Long notificationId){
        try{
            notificationService.cancelPickingReviewers(notificationId,(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            return ResponseEntity.ok().build();
        }catch (InvalidUserException e){
            return ResponseEntity.status(403).body(e.getMessage());
        }catch (InvalidValueException e){
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
    @RequestMapping(value="/sendBackAfterReview",method = RequestMethod.POST)
    @PreAuthorize("hasAnyRole('ADMIN','MAIN_EDITOR','EDITOR')")
    public ResponseEntity<?> sendBackAfterReview(@RequestBody SendBackAfterReviewRequestDTO requestDTO){
        try{
            return ResponseEntity.ok().body(notificationService.sendBackAfterReview(requestDTO,(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()));
        }catch (InvalidUserException e){
            return ResponseEntity.status(403).body(e.getMessage());
        }catch (InvalidValueException e){
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @RequestMapping(value="/authorChanged",method = RequestMethod.POST)
    @PreAuthorize("hasAnyRole('ADMIN','AUTHOR')")
    public ResponseEntity<?> authorChanged(@RequestBody AuthorChangesRequestDTO requestDTO){
        try {
            return ResponseEntity.ok().body(notificationService.authorChanged(requestDTO, (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()));
        }catch (InvalidValueException e){
            return ResponseEntity.status(400).body(e.getMessage());
        }catch (InvalidUserException e){
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @RequestMapping(value="/sendBackForReviews",method = RequestMethod.POST)
    @PreAuthorize("hasAnyRole('ADMIN','MAIN_EDITOR','EDITOR')")
    public ResponseEntity<?> sendBackForReviews(@RequestBody SendAgainToReviewRequestDTO requestDTO){
        try {
            notificationService.sendBackForReviews(requestDTO, (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            return ResponseEntity.ok().build();
        }catch (InvalidValueException e){
            return ResponseEntity.status(400).body(e.getMessage());
        }catch (InvalidUserException e){
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }
    @RequestMapping(value="/acceptJournal/{notificationId}",method = RequestMethod.GET)
    @PreAuthorize("hasAnyRole('ADMIN','MAIN_EDITOR','EDITOR')")
    public ResponseEntity<?> acceptJournal(@PathVariable("notificationId") Long notificationId){
        try {
            return ResponseEntity.ok().body(notificationService.acceptJournal(notificationId, (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()));
        }catch (InvalidValueException e){
            return ResponseEntity.status(400).body(e.getMessage());
        }catch (InvalidUserException e){
            return ResponseEntity.status(403).body(e.getMessage());
        }catch (NoReviewException e){
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}
