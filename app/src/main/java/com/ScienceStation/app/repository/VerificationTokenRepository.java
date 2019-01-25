package com.ScienceStation.app.repository;

import com.ScienceStation.app.model.User;
import com.ScienceStation.app.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken,Long>{

    VerificationToken findOneByUser(User user);

    Optional<VerificationToken> findOneByToken(String token);

}
