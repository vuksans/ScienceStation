package com.ScienceStation.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Table(name="science_branch")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ScienceBranch {

    @GeneratedValue
    @Id
    private Long id;

    @NotBlank
    private String name;

    @JsonIgnore
    @OneToMany(targetEntity = User.class,mappedBy = "branch")
    private List<User> users;
}
