package com.ScienceStation.app.repository;

import com.ScienceStation.app.model.Notification;
import com.ScienceStation.app.model.enumeration.NotificationStatus;
import com.ScienceStation.app.model.enumeration.NotificationType;
import org.aspectj.weaver.ast.Not;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,Long> {

    List<Notification> findAllByUser_IdAndStatus(Long id,NotificationStatus status);

    List<Notification> findAllByDeadlineBeforeAndStatus (LocalDateTime date,NotificationStatus status);
    List<Notification> findAllByDeadlineAfter (LocalDateTime date);
    Optional<Notification> findByJournal_IdAndStatusAndNotificationType(Long journalId, NotificationStatus status, NotificationType type);
}
