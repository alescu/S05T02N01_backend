package com.itacademy.petAcademy.controllers;

import com.itacademy.petAcademy.entities.User;
import com.itacademy.petAcademy.models.LoginCredentials;
import com.itacademy.petAcademy.models.RegisterCredentials;
import com.itacademy.petAcademy.models.RenewPasswordCredentials;
import com.itacademy.petAcademy.security.Rol;
import com.itacademy.petAcademy.services.PetService;
import com.itacademy.petAcademy.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;

@Tag(name = "Entry Controller")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private PetService petService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "Registers a new user with the provided credentials and returns a JWT token.")
    @Parameter(
            name =  "RegisterCredentials",
            description  = "Register credentials for user",
            example = "{\n" +
                    "  \"username\": \"john_doe\",\n" +
                    "  \"email\": \"john@example.com\",\n" +
                    "  \"password\": \"password123456\"\n" +
                    "}",
            required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully.",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Map.class),
                    examples = @ExampleObject(name = "Success Response",
                            value = "{\"jwt-token\": \"eyJhbGciOiJ.....signature\"}"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication or registration error.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = {
                                    @ExampleObject(name = "Bad Credentials",
                                            summary = "Invalid username or password format (e.g. if the backend validates format, not actual credentials at this stage).",
                                            value = "{\"error\": \"Credencials incorrectes.\"}",
                                            description = "Returned if the registration credentials (e.g., username, password, email) fail basic validation or format checks by the backend during the registration attempt, or if a user with that user already exists, leading to a BadCredentialsException or similar internal validation error."
                                    )}))})
    public ResponseEntity<Map<String,Object>> registerUser(
            @RequestBody RegisterCredentials registerCredentials
    ){
        try{

            String token = userService.registerService(registerCredentials, Rol.ROLE_USER);
            Map<String, Object> response = new HashMap<>();
            response.put("authUsername", registerCredentials.getUsername());
            response.put("jwt-token", token);
            return ResponseEntity.ok(response);

        } catch(BadCredentialsException authExc){
            log.error("Intent de login fallit (credencials incorrectes) per a l'usuari: {}", registerCredentials.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "Credencials incorrectes."));

        } catch(DisabledException authExc){
            log.error("Intent de login fallit (compte deshabilitat) per a l'usuari: {}", registerCredentials.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "El teu compte està deshabilitat."));

        } catch(LockedException authExc){
            log.error("Intent de login fallit (compte bloquejat) per a l'usuari: {}", registerCredentials.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "El teu compte està bloquejat. Contacta amb l'administrador."));

        } catch(AuthenticationException authExc){
            log.error("Error d'autenticació inesperat per a l'usuari: {} - {}", registerCredentials.getUsername(), authExc.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "Error d'autenticació."));

        } catch(Exception e){
            log.error("Error intern del servidor durant el login per a l'usuari: {} - {}", registerCredentials.getUsername(), e.getMessage()); // Per a logging intern
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "S'ha produït un error inesperat al servidor."));
        }
    }

    @PostMapping("/registerStaff")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(
            summary = "Register a new staff user",
            description = "Registers a new staff user with the provided credentials and returns a JWT token.")
    @Parameter(
            name =  "RegisterCredentials",
            description  = "Register credentials for user",
            example = "{\n" +
                    "  \"userName\": \"john_doe\",\n" +
                    "  \"email\": \"john@example.com\",\n" +
                    "  \"password\": \"password123456\"\n" +
                    "}",
            required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Staff user registered successfully.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(name = "Success Response",
                            value = "{\"jwt-token\": \"eyJhbGciOiJ.....signature\"}" + "{\"authUsername\": \"username\"}"  + "{\"pets\": \"[{}]]\"}"
                            ))),

            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication or registration error.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = {
                                    @ExampleObject(name = "Bad Credentials",
                                            summary = "Invalid username or password format (e.g. if the backend validates format, not actual credentials at this stage).",
                                            value = "{\"error\": \"Credencials incorrectes.\"}",
                                            description = "Returned if the registration credentials (e.g., username, password, email) fail basic validation or format checks by the backend during the registration attempt, or if a user with that user already exists, leading to a BadCredentialsException or similar internal validation error."
                                    )}))})
    public ResponseEntity<Map<String,Object>> registerStaffHandler(
            @RequestBody RegisterCredentials registerCredentials
    ){
        try{

            String token = userService.registerService(registerCredentials, Rol.ROLE_SUB_ADMIN);
            Map<String, Object> response = new HashMap<>();
            response.put("authUsername", registerCredentials.getUsername());
            response.put("jwt-token", token);
            return ResponseEntity.ok(response);

        } catch(BadCredentialsException authExc){
            log.error("Intent de login fallit (credencials incorrectes) per a l'usuari: {}", registerCredentials.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "Credencials incorrectes."));

        } catch(DisabledException authExc){
            log.error("Intent de login fallit (compte deshabilitat) per a l'usuari: {}", registerCredentials.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "El teu compte està deshabilitat."));

        } catch(LockedException authExc){
            log.error("Intent de login fallit (compte bloquejat) per a l'usuari: {}", registerCredentials.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "El teu compte està bloquejat. Contacti amb l'administrador."));

        } catch(AuthenticationException authExc){
            log.error("Error d'autenticació inesperat per a l'usuari: {} - {}", registerCredentials.getUsername(), authExc.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "Error d'autenticació."));

        } catch(Exception e){
            log.error("Error intern del servidor durant el login per a l'usuari: {} - {}", registerCredentials.getUsername(), e.getMessage()); // Per a logging intern
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "S'ha produït un error inesperat al servidor."));
        }
    }

    @PostMapping("/login")
    @Operation(
            summary = "Login user",
            description = "login user with the provided credentials and returns a JWT token.")
    @Parameter(
            name =  "RegisterCredentials",
            description  = "Register credentials for user",
            example = "{\n" +
                    "  \"userName\": \"john_doe\",\n" +
                    "  \"email\": \"john@example.com\",\n" +
                    "  \"password\": \"password123456\"\n" +
                    "}",
            required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Staff user registered successfully.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(name = "Success Response",
                                    value = "{\"jwt-token\": \"eyJhbGciOiJ.....signature\"}" + "{\"authUsername\": \"username\"}"  + "{\"pets\": \"[{}]]\"}"
                            ))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication or registration error.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = {
                                    @ExampleObject(name = "Bad Credentials",
                                            summary = "Invalid username or password format (e.g. if the backend validates format, not actual credentials at this stage).",
                                            value = "{\"error\": \"Credencials incorrectes.\"}",
                                            description = "Returned if the registration credentials (e.g., username, password, email) fail basic validation or format checks by the backend during the registration attempt, or if a user with that user already exists, leading to a BadCredentialsException or similar internal validation error."
                                    )}))})
    public ResponseEntity<Map<String,Object>> loginEntry(
            @RequestBody LoginCredentials loginCredentials
    ){
        String username = loginCredentials.getUsername();
        String password = loginCredentials.getPassword();

        UsernamePasswordAuthenticationToken authInputToken =
                new UsernamePasswordAuthenticationToken(username, password);

        try {
            if(userService.isBlockedUser(username)) throw new LockedException("");
            authenticationManager.authenticate(authInputToken);
            userService.resetFailedLoginAttempts(username);

            UserDetails userDetails = userService.loadUserByUsername(username);

            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
            List<String> roles = authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            String token = userService.generateTokenForAuthenticatedUser(username, roles);

            Map<String, Object> response = new HashMap<>();
            response.put("authUsername", username);
            response.put("jwt-token", token);
            response.put("pets", petService.getPetsByUserName(username));
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException authExc) {
            log.info("Intent de login fallit (credencials incorrectes) per a l'usuari: " + username);
            User user = userService.incrementFailedLoginAttempts(username);
            if(user.getAccountLockedAt()!=null){
                log.info("Intent de login fallit (compte bloquejat) per a l'usuari: " + username);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Collections.singletonMap("error", "El teu compte està bloquejat. Contacta amb l'administrador."));

            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "Credencials incorrectes."));

        } catch (LockedException authExc) {
            log.info("Intent de login fallit (compte bloquejat) per a l'usuari: " + username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "El teu compte està bloquejat. Contacta amb l'administrador."));

        } catch (AuthenticationException authExc) {
            log.info("Error d'autenticació inesperat per a l'usuari: " + username + " - " + authExc.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", authExc.getMessage()));

        } catch (Exception e) {
            log.error("Error intern del servidor durant el login per a l'usuari: " + username, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "S'ha produït un error inesperat al servidor."));
        }
    }

    @PostMapping("/userRenewPassword")
    @Operation(
            summary = "User Renew Passwordr",
            description = "User change their password the provided credentials and returns a JWT token.")
    @Parameter(
            name =  "RenewPasswordCredentials",
            description  = "Register credentials for user",
            example = "{\n" +
                    "  \"username\": \"john_doe\",\n" +
                    "  \"email\": \"john@example.com\",\n" +
                    "}",
            required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(name = "Success Response",
                                    value = "{\"jwt-token\": \"eyJhbGciOiJ.....signature\"}"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication or registration error.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = {
                                    @ExampleObject(name = "Bad Credentials",
                                            summary = "Invalid username or password format (e.g. if the backend validates format, not actual credentials at this stage).",
                                            value = "{\"error\": \"Credencials incorrectes.\"}",
                                            description = "Returned if the registration credentials (e.g., username, password, email) fail basic validation or format checks by the backend during the registration attempt, or if a user with that user already exists, leading to a BadCredentialsException or similar internal validation error."
                                    )}))})
    public ResponseEntity<Map<String,Object>> userRenewPassword(
            @RequestBody RenewPasswordCredentials renewPasswordCredentials
    ){
        try{
            userService.changeUserPassword( renewPasswordCredentials.getUsername(),  renewPasswordCredentials.getPassword());
            String token = userService.generateTokenForAuthenticatedUser(renewPasswordCredentials.getUsername());
            Map<String, Object> response = new HashMap<>();
            response.put("authUsername", renewPasswordCredentials.getUsername());
            response.put("jwt-token", token);
            return ResponseEntity.ok(response);

        } catch(BadCredentialsException authExc){
            log.error("Intent de login fallit (credencials incorrectes) per a l'usuari: {}", renewPasswordCredentials.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "Credencials incorrectes."));

        } catch(DisabledException authExc){
            log.error("Intent de login fallit (compte deshabilitat) per a l'usuari: {}", renewPasswordCredentials.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "El teu compte està deshabilitat."));

        } catch(LockedException authExc){
            log.error("Intent de login fallit (compte bloquejat) per a l'usuari: {}", renewPasswordCredentials.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "El teu compte està bloquejat. Contacta amb l'administrador."));

        } catch(AuthenticationException authExc){
            log.error("Error d'autenticació inesperat per a l'usuari: {} - {}", renewPasswordCredentials.getUsername(), authExc.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "Error d'autenticació."));

        } catch(Exception e){
            log.error("Error intern del servidor durant el login per a l'usuari: {} - {}", renewPasswordCredentials.getUsername(), e.getMessage()); // Per a logging intern
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "S'ha produït un error inesperat al servidor."));
        }
    }

}
