package com.itacademy.petAcademy.models;

import com.itacademy.petAcademy.entities.User;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserDto {

    private String userName;

    private String email;

    private String fullName;

    private String city;

    private String country;

    private String role;

    private LocalDateTime createdAt;

    private LocalDateTime accountLockedAt;

    private LocalDateTime accountBlockedAt;

    public UserDto(User user) {
        this.userName = user.getUsername();

        this.email = user.getEmail();

        this.fullName = user.getFullName();

        this.city = user.getCity();

        this.country = user.getCountry();

        this.createdAt = user.getCreatedAt();

        this.accountLockedAt = user.getAccountLockedAt();

        this.accountBlockedAt = user.getAccountBlockedAt();

        this.role = user.getRol();

    }
}
