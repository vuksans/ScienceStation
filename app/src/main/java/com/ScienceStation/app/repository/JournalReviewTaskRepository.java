package com.ScienceStation.app.repository;

import com.ScienceStation.app.model.JournalReviewTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JournalReviewTaskRepository extends JpaRepository<JournalReviewTask,Long>{

    Optional<JournalReviewTask> findByActiveAndJournal_Id (boolean active,Long journalId);
}
