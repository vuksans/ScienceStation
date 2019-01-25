package com.ScienceStation.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="journal_review_task")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class JournalReviewTask {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Journal journal;

    private int numberOfTasks=0;

    private boolean active;

    private LocalDateTime createdAt;

    @ManyToOne
    private User creator;

    @OneToMany(targetEntity = Comment.class,mappedBy = "journalReviewTask")
    private List<Comment> comments;
}
