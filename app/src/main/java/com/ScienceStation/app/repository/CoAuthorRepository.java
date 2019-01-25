package com.ScienceStation.app.repository;

import com.ScienceStation.app.model.CoAuthor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoAuthorRepository extends JpaRepository<CoAuthor,Long> {

    Optional<CoAuthor> findOneByEmail(String email);

    List<CoAuthor> findAllByMagazine_Id(Long id);
}
