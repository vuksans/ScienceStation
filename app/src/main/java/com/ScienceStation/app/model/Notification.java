package com.ScienceStation.app.model;

import com.ScienceStation.app.model.enumeration.NotificationStatus;
import com.ScienceStation.app.model.enumeration.NotificationType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name="notification")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Notification {

    @GeneratedValue
    @Id
    private Long id;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String reason;

    @NotNull
    private NotificationStatus status;

    private LocalDateTime deadline;

    @NotNull
    private NotificationType notificationType;

    @ManyToOne
    @JoinColumn(name="journal_id")
    private Journal journal;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;


}
