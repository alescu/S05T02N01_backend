package com.itacademy.petAcademy.entities;

import com.itacademy.petAcademy.models.RegisterCredentials;
import com.itacademy.petAcademy.security.Rol;
import com.mongodb.lang.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.*;
import jakarta.persistence.Id;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String userName;

    private String email;

    private String rol;

    @Nullable
    private String fullName;

    @Nullable
    private String city;

    @Nullable
    private String country;

    @Nullable
    private String password;

    @Nullable
    @Setter(AccessLevel.NONE)
    private LocalDateTime createdAt;

    @Nullable
    @Setter(AccessLevel.NONE)
    private LocalDateTime updatedAt;

    @Nullable
    @Setter(AccessLevel.NONE)
    private LocalDateTime passwordUpdatedAt;

    @Nullable
    private LocalDateTime accountLockedAt;

    @Nullable
    private LocalDateTime accountBlockedAt;

    private Integer failedLoginAttempts = 0;

    public User(String userName, String email, String password) {
        super();
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.rol = Rol.ROLE_USER.name();
    }


    public User(RegisterCredentials registerCredentials, String rol){
        super();
        this.userName = registerCredentials.getUsername();
        this.email = registerCredentials.getEmail();
        this.password = registerCredentials.getPassword();
        this.rol = rol;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.rol));
    }

    @Override
    public String getUsername() {
        return this.userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountLockedAt == null;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountLockedAt == null;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return accountLockedAt == null;
    }

    @Override
    public boolean isEnabled() {
        return accountLockedAt == null;
    }
}
