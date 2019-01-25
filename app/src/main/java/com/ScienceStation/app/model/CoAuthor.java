package com.ScienceStation.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name="co_author")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoAuthor {

    @GeneratedValue
    @Id
    private Long id;

    @Email
    private String email;

    @NotBlank
    private String country;

    @NotBlank
    private String city;

    @ManyToOne
    @JoinColumn(name="magazine_id")
    private Magazine magazine;

}
