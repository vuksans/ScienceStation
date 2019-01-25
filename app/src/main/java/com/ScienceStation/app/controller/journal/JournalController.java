package com.ScienceStation.app.controller.journal;

import com.ScienceStation.app.controller.journal.dto.request.CreateJournalRequestDTO;
import com.ScienceStation.app.model.Journal;
import com.ScienceStation.app.model.User;
import com.ScienceStation.app.service.journal.JournalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/journals")
public class JournalController {

    JournalService journalService;

    @Autowired
    public JournalController(JournalService journalService) {
        this.journalService = journalService;
    }

    @RequestMapping(value="/create",method = RequestMethod.POST)
    @PreAuthorize("hasAnyRole('ADMIN','AUTHOR')")
    public ResponseEntity<?> createJournal(@RequestBody CreateJournalRequestDTO createJournalRequestDTO){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Journal j =journalService.createJournal(createJournalRequestDTO,user);
        return ResponseEntity.ok(j);
    }
}
