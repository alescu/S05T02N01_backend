package com.itacademy.petAcademy.controllers;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "Administration Controller")
@RestController
@RequestMapping("/admin")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private PetService petService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/users")
    @PreAuthorize("!hasRole('ROLE_USER')")
    @Operation(
            summary = "Show all users",
            description = "Show all users information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Show all users information.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(name = "Success Response",
                            value = "{\n" +
                                    "    \"result\": [\n" +
                                    "        {\n" +
                                    "            \"userName\": \"john_doe\",\n" +
                                    "            \"email\": \"john@example.com\",\n" +
                                    "            \"fullName\": null,\n" +
                                    "            \"city\": null,\n" +
                                    "            \"country\": null,\n" +
                                    "            \"createdAt\": \"2025-06-13T15:59:51.443304\"\n" +
                                    "        },\n" +
                                    "        {\n" +
                                    "            \"userName\": \"koko2\",\n" +
                                    "            \"email\": \"fff@gggg.vom\",\n" +
                                    "            \"fullName\": null,\n" +
                                    "            \"city\": null,\n" +
                                    "            \"country\": null,\n" +
                                    "            \"createdAt\": \"2025-06-13T17:13:29.486957\"\n" +
                                    "        },\n" +
                                    "        {\n" +
                                    "            \"userName\": \"koko3\",\n" +
                                    "            \"email\": \"sss@fff.com\",\n" +
                                    "            \"fullName\": null,\n" +
                                    "            \"city\": null,\n" +
                                    "            \"country\": null,\n" +
                                    "            \"createdAt\": \"2025-06-13T17:25:44.985201\"\n" +
                                    "        },\n" +
                                    "        {\n" +
                                    "            \"userName\": \"koko\",\n" +
                                    "            \"email\": \"koko@example.com\",\n" +
                                    "            \"fullName\": null,\n" +
                                    "            \"city\": null,\n" +
                                    "            \"country\": null,\n" +
                                    "            \"createdAt\": \"2025-06-13T16:00:24.601632\"\n" +
                                    "        },\n" +
                                    "        {\n" +
                                    "            \"userName\": \"koko4\",\n" +
                                    "            \"email\": \"4eee@bbb.bom\",\n" +
                                    "            \"fullName\": null,\n" +
                                    "            \"city\": null,\n" +
                                    "            \"country\": null,\n" +
                                    "            \"createdAt\": \"2025-06-14T18:16:27.328816\"\n" +
                                    "        }\n" +
                                    "    ],\n" +
                                    "    \"authenticatedUsername\": \"koko\"\n" +
                                    "}"
                            ))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication or registration error.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = {
                                    @ExampleObject(name = "Bad Credentials",
                                            summary = "Invalid username or password format (e.g. if the backend validates format, not actual credentials at this stage).",
                                            value = "{\"error\": \"Credencials incorrectes.\"}",
                                            description = "Returned if the registration credentials (e.g., username, password, email) fail basic validation or format checks by the backend during the registration attempt, or if a user with that username/email already exists, leading to a BadCredentialsException or similar internal validation error."
                                    )}))})
    public ResponseEntity<Map<String,Object>> getAllUsers(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUsername = authentication.getName();

        Map<String, Object> response = new HashMap<>();
        response.put("authenticatedUsername", authenticatedUsername);
        response.put("users", userService.convertUsersToUserDtos(userService.getUsers()));

        return ResponseEntity.ok(response);
    }


    @GetMapping("/pets")
    @PreAuthorize("!hasRole('ROLE_USER')")
    @Operation(
            summary = "Show all users Pets",
            description = "Show all users Pets information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Staff user registered successfully.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(name = "Success Response",
                            value = "{\n" +
                                    "    \"pets\": [\n" +
                                    "        {\n" +
                                    "            \"petName\": \"Whisky2\",\n" +
                                    "            \"petType\": \"cat\",\n" +
                                    "            \"userName\": \"koko2\",\n" +
                                    "            \"id\": \"684daabebcd0b6f35b93d4f1\",\n" +
                                    "            \"hunger\": 5,\n" +
                                    "            \"happiness\": 5,\n" +
                                    "            \"energy\": 5,\n" +
                                    "            \"health\": 5,\n" +
                                    "            \"objects\": [],\n" +
                                    "            \"background\": null\n" +
                                    "        }\n" +
                                    "    ],\n" +
                                    "    \"authenticatedUsername\": \"koko\"\n" +
                                    "}"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication or registration error.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = {
                                    @ExampleObject(name = "Bad Credentials",
                                            summary = "Invalid username or password format (e.g. if the backend validates format, not actual credentials at this stage).",
                                            value = "{\"error\": \"Credencials incorrectes.\"}",
                                            description = "Returned if the registration credentials (e.g., username, password, email) fail basic validation or format checks by the backend during the registration attempt, or if a user with that username/email already exists, leading to a BadCredentialsException or similar internal validation error."
                                    )}))})
    public ResponseEntity<Map<String,Object>> getAllPets(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUsername = authentication.getName();

        Map<String, Object> response = new HashMap<>();
        response.put("authenticatedUsername", authenticatedUsername);
        response.put("pets", petService.getGetAllPets());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{adminUser}/blockUser")
    @PreAuthorize("!hasRole('ROLE_USER')")
    @Operation(
            summary = "block User",
            description = "Mark user as blocked with the provided credentials and returns a JWT token.")
    @Parameter(
            name =  "userName",
            description  = "user name",
            example = "john_doe",
            required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Staff user registered successfully.",
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
                                            description = "Returned if the registration credentials (e.g., username, password, email) fail basic validation or format checks by the backend during the registration attempt, or if a user with that username/email already exists, leading to a BadCredentialsException or similar internal validation error."
                                    )}))})
    public ResponseEntity<Map<String,Object>> blockUser(
            @RequestParam String userName,
            @PathVariable String adminUser
    ){
        userService.blockUser(userName, LocalDateTime.now());
        Map<String, Object> response = new HashMap<>();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{adminUser}/unblockUser")
    @PreAuthorize("!hasRole('ROLE_USER')")
    @Operation(
            summary = "unblockUser",
            description = "Mark user as unblock user with the provided credentials and returns a JWT token.")
    @Parameter(
            name =  "userName",
            description  = "user name",
            example = "john_doe",
            required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Staff user registered successfully.",
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
                                            description = "Returned if the registration credentials (e.g., username, password, email) fail basic validation or format checks by the backend during the registration attempt, or if a user with that username/email already exists, leading to a BadCredentialsException or similar internal validation error."
                                    )}))})
    public ResponseEntity<Map<String,Object>> unblockUser(
            @RequestParam String userName,
            @PathVariable String adminUser
    ){
        userService.blockUser(userName, null);
        Map<String, Object> response = new HashMap<>();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{adminUser}/changeRole")
    @PreAuthorize("!hasRole('ROLE_USER')")
    @Operation(
            summary = "Change user Role",
            description = "Change the user Role with the provided credentials and returns a JWT token.")
    @Parameter(
            name =  "userName",
            description  = "user name",
            example = "john_doe",
            required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Staff user registered successfully.",
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
                                            description = "Returned if the registration credentials (e.g., username, password, email) fail basic validation or format checks by the backend during the registration attempt, or if a user with that username/email already exists, leading to a BadCredentialsException or similar internal validation error."
                                    )}))})
    public ResponseEntity<Map<String,Object>> changeUserRole(
            @RequestParam String userName,
            @PathVariable String adminUser
    ){
        userService.changeUserRole(userName);
        Map<String, Object> response = new HashMap<>();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{adminUser}/markUser")
    @PreAuthorize("!hasRole('ROLE_USER')")
    @Operation(
            summary = "markUserAsPendingRenewal",
            description = "Mark user as pending password renewal with the provided credentials and returns a JWT token.")
    @Parameter(
            name =  "userName",
            description  = "user name",
            example = "john_doe",
            required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Staff user registered successfully.",
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
                                            description = "Returned if the registration credentials (e.g., username, password, email) fail basic validation or format checks by the backend during the registration attempt, or if a user with that username/email already exists, leading to a BadCredentialsException or similar internal validation error."
                                    )}))})
    public ResponseEntity<Map<String,Object>> markUserAsPendingRenewal(
            @RequestParam String userName,
            @PathVariable String adminUser
    ){
        userService.markAsPendingRenewal(userName, LocalDateTime.now());
        Map<String, Object> response = new HashMap<>();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{adminUser}/unmarkUser")
    @PreAuthorize("!hasRole('ROLE_USER')")
    @Operation(
            summary = "unmarkUserAsPendingRenewal",
            description = "Unmmark user as pending password renewal with the provided credentials and returns a JWT token.")
    @Parameter(
            name =  "userName",
            description  = "user name",
            example = "john_doe",
            required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Staff user registered successfully.",
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
                                            description = "Returned if the registration credentials (e.g., username, password, email) fail basic validation or format checks by the backend during the registration attempt, or if a user with that username/email already exists, leading to a BadCredentialsException or similar internal validation error."
                                    )}))})
    public ResponseEntity<Map<String,Object>> unmarkUserAsPendingRenewal(
            @RequestParam String userName,
            @PathVariable String adminUser
    ){
        userService.markAsPendingRenewal(userName, null);
        Map<String, Object> response = new HashMap<>();
        return ResponseEntity.ok(response);
    }

}
