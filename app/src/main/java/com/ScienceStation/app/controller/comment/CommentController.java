package com.ScienceStation.app.controller.comment;

import com.ScienceStation.app.controller.comment.dto.request.CreateCommentDTO;
import com.ScienceStation.app.model.User;
import com.ScienceStation.app.service.comment.CommentService;
import com.ScienceStation.app.service.exception.InvalidUserException;
import com.ScienceStation.app.service.exception.InvalidValueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @RequestMapping(value="/create",method = RequestMethod.POST)
    @PreAuthorize("hasAnyRole('ADMIN','REVIEWER')")
    public ResponseEntity<?> createComment(@RequestBody CreateCommentDTO createCommentDTO){
        try{
            return ResponseEntity.status(201).body(commentService.createComment(createCommentDTO,(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()));
        }catch (InvalidUserException e){
            return ResponseEntity.status(403).body(e.getMessage());
        }catch (InvalidValueException e){
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
    @RequestMapping(value="/getComments/{journalId}",method=RequestMethod.GET)
    public ResponseEntity<?> getCommentsForJournal(@PathVariable("journalId")Long journalId){
        try{
            return ResponseEntity.ok().body(commentService.getCommentsForJournal(journalId,(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()));
        }catch (InvalidValueException e){
            return ResponseEntity.status(400).body(e.getMessage());
        }catch (InvalidUserException e){
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }
}
