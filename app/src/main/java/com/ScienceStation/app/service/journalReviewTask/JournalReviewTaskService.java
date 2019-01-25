package com.ScienceStation.app.service.journalReviewTask;

import com.ScienceStation.app.model.Comment;
import com.ScienceStation.app.model.Journal;
import com.ScienceStation.app.model.JournalReviewTask;
import com.ScienceStation.app.model.User;
import com.ScienceStation.app.repository.JournalReviewTaskRepository;
import com.ScienceStation.app.service.exception.InvalidValueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class JournalReviewTaskService {

    private final JournalReviewTaskRepository journalReviewTaskRepository;

    @Autowired
    public JournalReviewTaskService(JournalReviewTaskRepository journalReviewTaskRepository) {
        this.journalReviewTaskRepository = journalReviewTaskRepository;
    }

    public JournalReviewTask addReviewer(Journal journal, User user){
        Optional<JournalReviewTask> journalReviewTaskOptional = journalReviewTaskRepository.findByActiveAndJournal_Id(true,journal.getId());

        if(journalReviewTaskOptional.isPresent()){
            journalReviewTaskOptional.get().setNumberOfTasks(journalReviewTaskOptional.get().getNumberOfTasks()+1);
            journalReviewTaskRepository.save(journalReviewTaskOptional.get());
            return journalReviewTaskOptional.get();
        }

        //if it isn't preset I need to create a new JournalReviewTask first and then save it
        JournalReviewTask journalReviewTask = new JournalReviewTask();
        journalReviewTask.setCreator(user);
        journalReviewTask.setJournal(journal);
        journalReviewTask.setNumberOfTasks(journalReviewTask.getNumberOfTasks()+1);
        journalReviewTask.setActive(true);
        journalReviewTask.setCreatedAt(LocalDateTime.now());
        journalReviewTaskRepository.save(journalReviewTask);
        return journalReviewTask;
    }

    public JournalReviewTask findTaskFromJournal(Journal journal) throws InvalidValueException{
        Optional<JournalReviewTask> journalReviewTaskOptional = journalReviewTaskRepository.findByActiveAndJournal_Id(true,journal.getId());

        if(!journalReviewTaskOptional.isPresent())
            throw new InvalidValueException();

        return journalReviewTaskOptional.get();
    }

    public JournalReviewTask closeJournalReviewTask(JournalReviewTask journalReviewTask){
        journalReviewTask.setActive(false);
        journalReviewTaskRepository.save(journalReviewTask);
        return journalReviewTask;
    }

    public JournalReviewTask createNewFromOld(Journal j) throws InvalidValueException {
        try {
            JournalReviewTask oldJournalReviewTask = findTaskFromJournal(j);
            closeJournalReviewTask(oldJournalReviewTask);

            JournalReviewTask journalReviewTask = new JournalReviewTask();

            journalReviewTask.setActive(true);
            journalReviewTask.setNumberOfTasks(oldJournalReviewTask.getNumberOfTasks());
            journalReviewTask.setCreatedAt(LocalDateTime.now());
            journalReviewTask.setJournal(j);
            journalReviewTask.setCreator(oldJournalReviewTask.getCreator());
            journalReviewTaskRepository.save(journalReviewTask);
            return  journalReviewTask;

        }catch (InvalidValueException e){
            throw new InvalidValueException();
        }
    }

}
