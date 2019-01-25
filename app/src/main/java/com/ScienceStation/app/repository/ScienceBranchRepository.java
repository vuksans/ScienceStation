package com.ScienceStation.app.repository;

import com.ScienceStation.app.model.ScienceBranch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScienceBranchRepository extends JpaRepository<ScienceBranch,Long>{

    ScienceBranch findOneByName (String name);
}
