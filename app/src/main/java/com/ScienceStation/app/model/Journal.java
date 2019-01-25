package com.ScienceStation.app.model;

import com.ScienceStation.app.model.enumeration.JournalStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="journal")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Journal {

    @GeneratedValue
    @Id
    private Long id;

    @NotBlank
    private String headline;

    @NotBlank
    private String keyPoints;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String journalAbstract;
    //Journal ima samo jedan branch a branch moze imati vise journala
    @ManyToOne
    private ScienceBranch branch;

    @ManyToOne
    @JoinColumn(name = "magazine_id")
    private Magazine magazine;

    @NotNull
    private JournalStatus status;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name="editor_id")
    private User editor;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User author;

}
