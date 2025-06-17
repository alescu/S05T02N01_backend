package com.itacademy.petAcademy.services;

import com.itacademy.petAcademy.entities.User;
import com.itacademy.petAcademy.models.RegisterCredentials;
import com.itacademy.petAcademy.models.UserDto;
import com.itacademy.petAcademy.repositories.UserRepository;
import com.itacademy.petAcademy.security.JwtService;
import com.itacademy.petAcademy.security.Rol;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Role;
import org.springframework.security.authentication.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UserService implements UserDetailsService {


    private final UserRepository userRepo;

    private final PasswordEncoder passwordEncoder;

    @Lazy
    private final JwtService jwtService;

    public UserService(UserRepository userRepo, PasswordEncoder passwordEncoder, JwtService jwtUtil){
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtUtil;
    }

    public String registerService(RegisterCredentials registerCredentials, Rol userRol) throws UsernameNotFoundException, BadCredentialsException {
        String encodedPass = passwordEncoder.encode(registerCredentials.getPassword());
        registerCredentials.setPassword(encodedPass);

        User user = userRepo.save(new User(registerCredentials, userRol.name()));

        return jwtService.generateToken(user.getUsername());
    }

    public String generateTokenForAuthenticatedUser(String username) {
        userRepo.findByUserName(username).ifPresent(user -> {

        });
        return jwtService.generateToken(username);
    }

    public String generateTokenForAuthenticatedUser(String userName, List<String> roles) {
        userRepo.findByUserName(userName).ifPresent(user -> {

        });
        return jwtService.generateToken( userName, roles);

    }

    @Transactional
    public User incrementFailedLoginAttempts(String username) throws LockedException {
        User user = null;
        Optional<User> userOpt = userRepo.findByUserName(username);
        if(userOpt.isPresent()){
            user = userOpt.get();
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            int MAX_FAILED_ATTEMPTS = 3;
            if ((user.getAccountLockedAt()==null) && (user.getFailedLoginAttempts() > MAX_FAILED_ATTEMPTS)) {
                user.setAccountLockedAt(LocalDateTime.now());
            }
            userRepo.save(user);
        }
        return user;
    }

    @Transactional
    public void changeUserRole(String username) {
        Optional<User> userOpt = userRepo.findByUserName(username);
        if(userOpt.isPresent()){
            User user = userOpt.get();
            user.setRol((Rol.ROLE_USER.name().equals(user.getRol())?Rol.ROLE_SUB_ADMIN.name():Rol.ROLE_USER.name())  );
            userRepo.save(user);
        }
    }

    @Transactional
    public void markAsPendingRenewal(String username, LocalDateTime localDateTime) {
        Optional<User> userOpt = userRepo.findByUserName(username);
        if(userOpt.isPresent()){
            User user = userOpt.get();
            user.setAccountLockedAt(localDateTime);
            userRepo.save(user);
        }
    }

    @Transactional
    public void blockUser(String username, LocalDateTime localDateTime) {
        Optional<User> userOpt = userRepo.findByUserName(username);
        if(userOpt.isPresent()){
            User user = userOpt.get();
            user.setAccountBlockedAt(localDateTime);
            userRepo.save(user);
        }
    }

    public Boolean isBlockedUser(String username) {
        Optional<User> userOpt = userRepo.findByUserName(username);
        if(userOpt.isPresent()){
            return userOpt.get().getAccountBlockedAt()!=null;
        }
        return null;
    }

    @Transactional
    public void resetPasswordFailedLoginAttempts(String username, String newPassword) {
        userRepo.findByUserName(username).ifPresent(user -> {
            if (user.getFailedLoginAttempts() > 0 || user.getAccountLockedAt() != null) {
                user.setFailedLoginAttempts(0);
                user.setAccountLockedAt(null);
                userRepo.save(user);
            }
        });
    }

    @Transactional
    public void resetFailedLoginAttempts(String username) {
        resetPasswordFailedLoginAttempts(username, null);
    }

    @Transactional
    public void changeUserPassword(String username, String newPassword) {
        userRepo.findByUserName(username).ifPresent(user -> {
            if (user.getFailedLoginAttempts() > 0 || user.getAccountLockedAt() != null) {
                user.setFailedLoginAttempts(0);
                user.setAccountLockedAt(null);
                String encodedPass = passwordEncoder.encode(newPassword);
                user.setPassword(encodedPass);
                userRepo.save(user);
            }
        });
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOpt = userRepo.findByUserName(username);
        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("No user found with username: " + username);
        }
        User user = userOpt.get();
        if (user.getAccountLockedAt() != null) {
            throw new LockedException("El compte està bloquejat.");
        }
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRol()))
        );
    }

    public List<User> getUsers() {
        return userRepo.findAll();
    }

    @Transactional
    public void deleteUser(String username) {
        Optional<User> userOpt = userRepo.findByUserName(username);
        if(userOpt.isPresent()){
            User user = userOpt.get();
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            int MAX_FAILED_ATTEMPTS = 3;
            if ((user.getAccountLockedAt()!=null) && (user.getFailedLoginAttempts() >= MAX_FAILED_ATTEMPTS)) {
                user.setAccountLockedAt(LocalDateTime.now());
            }
            userRepo.save(user);
        }
    }

    public List<UserDto> convertUsersToUserDtos(List<User> users) {
        return users.stream() // 1. Crea un stream a partir de la llista d'usuaris
                .map(user -> new UserDto(user)) // 2. Transforma cada objecte User en un UserDto
                .collect(Collectors.toList()); // 3. Recull els UserDto resultants en una nova llista
    }

}