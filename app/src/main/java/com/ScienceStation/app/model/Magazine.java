package com.ScienceStation.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name="magazine")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Magazine {

    @GeneratedValue
    @Id
    private Long id;

    @NotNull
    private Long issn;

    @NotBlank
    private String name;

    @NotNull
    private boolean openAccess;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name ="main_editor_id")
    private User mainEditor;

    @ManyToMany
    @JsonIgnore
    @JoinTable(name="magazine_editor")
    private List<User> editors;

    @ManyToMany
    @JsonIgnore
    @JoinTable(name="magazine_reviewer")
    private List<User> reviewers;

    @OneToMany(mappedBy = "magazine")
    private List<CoAuthor> coAuthors;

    @ManyToMany
    @JsonIgnore
    @JoinTable(
            name="magazine_members",
            joinColumns = {@JoinColumn(name="magazine_id")},
            inverseJoinColumns = {@JoinColumn(name="user_id")}
    )
    private List<User> members;
}
