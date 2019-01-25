package com.ScienceStation.app.model;

import com.ScienceStation.app.model.enumeration.Role;
        import com.fasterxml.jackson.annotation.JsonIgnore;
        import lombok.AllArgsConstructor;
        import lombok.Data;
        import lombok.NoArgsConstructor;
        import org.springframework.security.core.GrantedAuthority;
        import org.springframework.security.core.authority.SimpleGrantedAuthority;
        import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
        import javax.validation.constraints.NotNull;
        import java.util.Collection;
        import java.util.Collections;
import java.util.List;

@Entity
@Table(name="user")
@NoArgsConstructor
@AllArgsConstructor
@Data
//fali mi baseEntity za timestampove
public class User implements UserDetails{

    @Id
    @GeneratedValue
    private Long id;


    //NotBlank = To put it simply, a String field constrained with @NotBlank must be not null and the trimmed length must be greater than zero.
    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Column(unique = true)
    @NotBlank
    private String email;

    @NotBlank
    private String country;

    @NotBlank
    private String city;

    @NotNull
    private boolean emailVerified;

    @JsonIgnore
    //@Column(name = "password")
    @NotBlank
    private String hashedPassword;

    @NotNull
    private Role role;

    @JsonIgnore
    @NotNull
    private boolean deleted;

    @ManyToOne
    @JsonIgnore
    private ScienceBranch branch;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }

    //ovo cemo videti jos jel treba
    @JsonIgnore
    @Override
    public String getPassword() {
        return hashedPassword;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return emailVerified;
    }
}
