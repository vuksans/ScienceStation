package com.ScienceStation.app.repository;

import com.ScienceStation.app.model.Magazine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MagazineRepository extends JpaRepository<Magazine,Long>{

    Optional<Magazine> findOneByName (String name);
}
