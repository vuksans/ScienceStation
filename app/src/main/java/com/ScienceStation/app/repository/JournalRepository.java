package com.ScienceStation.app.repository;

import com.ScienceStation.app.model.Journal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JournalRepository extends JpaRepository<Journal,Long> {

    Optional<Journal> findOneByHeadline (String headline);

    List<Journal> findAllByBranchId (Long id);
}
