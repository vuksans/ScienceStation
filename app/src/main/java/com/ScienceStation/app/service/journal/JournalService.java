package com.ScienceStation.app.service.journal;

import com.ScienceStation.app.controller.journal.dto.request.CreateJournalRequestDTO;
import com.ScienceStation.app.model.Journal;
import com.ScienceStation.app.model.Magazine;
import com.ScienceStation.app.model.ScienceBranch;
import com.ScienceStation.app.model.User;
import com.ScienceStation.app.model.enumeration.JournalStatus;
import com.ScienceStation.app.repository.JournalRepository;
import com.ScienceStation.app.repository.MagazineRepository;
import com.ScienceStation.app.repository.ScienceBranchRepository;
import com.ScienceStation.app.repository.UserRepository;
import com.ScienceStation.app.service.exception.InvalidValueException;
import com.ScienceStation.app.service.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JournalService {


    private JournalRepository journalRepository;

    private MagazineRepository magazineRepository;

    private ScienceBranchRepository scienceBranchRepository;

    private NotificationService notificationService;

    @Autowired
    public JournalService(JournalRepository journalRepository, MagazineRepository magazineRepository,
                          ScienceBranchRepository scienceBranchRepository, NotificationService notificationService) {
        this.journalRepository = journalRepository;
        this.magazineRepository = magazineRepository;
        this.scienceBranchRepository = scienceBranchRepository;
        this.notificationService = notificationService;
    }

    public Journal createJournal(CreateJournalRequestDTO createJournalRequestDTO,User user) {
        /* Siguran sam da mi ovo vraca dobro jer cu preko fronta namestiti! Ako bas treba mogu napraviti custom exeptione za ovo */
        Magazine m = magazineRepository.findById(createJournalRequestDTO.getMagazine_id()).get();
        ScienceBranch branch = scienceBranchRepository.findById(createJournalRequestDTO.getBranch_id()).get();

        Journal journal = new Journal();

        journal.setKeyPoints(createJournalRequestDTO.getKeyPoints());
        journal.setHeadline(createJournalRequestDTO.getHeadLine());
        journal.setJournalAbstract(createJournalRequestDTO.getJournalAbstract());
        journal.setStatus(JournalStatus.CREATED);
        journal.setAuthor(user);
        journal.setMagazine(m);
        journal.setBranch(branch);
        journal.setEditor(m.getMainEditor());
        journalRepository.save(journal);

        notificationService.notificationForCreatedJournal(journal);
        return journal;

    }

    public Journal getJournalById(Long id) throws InvalidValueException{
        Optional<Journal> journalOptional = journalRepository.findById(id);
        if(!journalOptional.isPresent())
            throw new InvalidValueException();
        return journalOptional.get();
    }
}
