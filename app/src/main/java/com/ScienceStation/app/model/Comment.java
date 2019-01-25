package com.ScienceStation.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Comment {

    @GeneratedValue
    @Id
    private Long id;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String comment;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String hiddenComment;

    @JsonIgnore
    @ManyToOne
    private JournalReviewTask journalReviewTask;

    @JsonIgnore
    @ManyToOne
    private User creator;
}
