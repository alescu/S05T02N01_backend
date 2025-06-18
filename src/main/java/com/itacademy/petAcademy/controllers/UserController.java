package com.itacademy.petAcademy.controllers;

import com.itacademy.petAcademy.entities.Pet;
import com.itacademy.petAcademy.entities.User;
import com.itacademy.petAcademy.models.NewPetCredentials;
import com.itacademy.petAcademy.models.NewPetValues;
import com.itacademy.petAcademy.models.UserDto;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "User Controller")
@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private PetService petService;

    @Autowired
    private UserService userService;

   @PostMapping("/{username}/pet")
    @Operation(
            summary = "new Pet",
            description = "Insert an user new Pet.")
    @Parameter(
            name =  "new Pet information",
            description  = "Per information",
            example = "{ petName : \"Whisky\", petType : \"cat\" }",
            required = true)
    @Parameter(
            name =  "username",
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
    public ResponseEntity<Map<String,Object>> newPet( @PathVariable String username, @RequestBody NewPetCredentials newPetCredentials){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String authenticatedUsername = authentication.getName();

            if(!userValidation(username, authentication)){
                log.info("L'usuari logat no coincideix amb el del path: " + username + " : " + authenticatedUsername);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Collections.singletonMap("error", "S'ha produït un error inesperat al servidor."));
            }

            Pet pet = petService.createNewPet( username, newPetCredentials.getPetName(), newPetCredentials.getPetType());

            Map<String, Object> response = new HashMap<>();
            response.put("authenticatedUsername", authenticatedUsername);
            response.put("pet", pet);

            return ResponseEntity.ok(response);

        } catch (
            BadCredentialsException authExc) {
            log.info("Intent de login fallit (credencials incorrectes) per a l'usuari: " + username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "Credencials incorrectes."));

        } catch (Exception e) {
        log.error("Error intern del servidor durant el login per a l'usuari: " + username, e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("error", "S'ha produït un error inesperat al servidor."));
        }
    }

    @PutMapping("/{userName}/pet/{petName}")
    @Operation(
            summary = "Set New Pet Values",
            description = "Set new Pet information.")
    @Parameter(
            name =  "Pet new information",
            description  = "Pet new information modified",
            example = "{\n" +
                    "    \"result\": {\n" +
                    "        \"petName\": \"Whisky2\",\n" +
                    "        \"petType\": \"cat\",\n" +
                    "        \"userName\": \"koko2\",\n" +
                    "        \"id\": \"684d9bf9aa2169743596a4a2\",\n" +
                    "        \"hunger\": 5,\n" +
                    "        \"happiness\": 5,\n" +
                    "        \"energy\": 5,\n" +
                    "        \"health\": 5,\n" +
                    "        \"objects\": [],\n" +
                    "        \"background\": null\n" +
                    "    },\n" +
                    "    \"authenticatedUsername\": \"koko2\"\n" +
                    "}",
            required = true)
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
    public ResponseEntity<Map<String,Object>> setNewPetValues(@RequestBody NewPetValues newPetCredentials, @PathVariable String userName, @PathVariable String petName){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUsername = authentication.getName();

        Pet pet = petService.getPetByName(petName);
        pet.setHappiness(newPetCredentials.getHappiness());
        pet.setEnergy(newPetCredentials.getEnergy());
        pet.setHunger(newPetCredentials.getHunger());
        pet.setBackground(newPetCredentials.getBackground());

        pet.setObjects(newPetCredentials.getObjects());

        if(!userValidation(userName, authentication)){
            log.info("L'usuari logat no coincideix amb el del path: " + userName + " : " + authenticatedUsername);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "S'ha produït un error inesperat al servidor."));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("authenticatedUsername", authenticatedUsername);
        response.put("pet", petService.savePet(pet));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userName}/pet")
    @Operation(
            summary = "Show user Pets",
            description = "Show all user Pets.")
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
    public ResponseEntity<Map<String,Object>> getUserPets(@PathVariable String userName){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUsername = authentication.getName();

        if(!userValidation(userName, authentication)){
            log.info("L'usuari logat no coincideix amb el del path: " + userName + " : " + authenticatedUsername);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "S'ha produït un error inesperat al servidor."));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("authenticatedUsername", authenticatedUsername);
        response.put("pets", petService.getPetsByUserName(userName));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userName}/pet/{petName}")
    @Operation(
            summary = "Show user Pet",
            description = "Show user Pet information.")
    @Parameter(
            name =  "userName",
            description  = "user name",
            example = "john_doe",
            required = true)
    @Parameter(
            name =  "petName",
            description  = "pet name",
            example = "Kaki",
            required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Staff user registered successfully.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(name = "Success Response",
                                    value = "{\n" +
                                            "    \"authenticatedUsername\": \"koko2\",\n" +
                                            "    \"pet\": {\n" +
                                            "        \"petName\": \"Whisky2\",\n" +
                                            "        \"petType\": \"cat\",\n" +
                                            "        \"userName\": \"koko2\",\n" +
                                            "        \"id\": \"684daabebcd0b6f35b93d4f1\",\n" +
                                            "        \"hunger\": 5,\n" +
                                            "        \"happiness\": 5,\n" +
                                            "        \"energy\": 5,\n" +
                                            "        \"health\": 5,\n" +
                                            "        \"objects\": [],\n" +
                                            "        \"background\": null\n" +
                                            "    }\n" +
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
    public ResponseEntity<Map<String,Object>> getUserPet(@PathVariable String userName, @PathVariable String petName){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUsername = authentication.getName();

        if(!userValidation(userName, authentication)){
            log.info("L'usuari logat no coincideix amb el del path: " + userName + " : " + authenticatedUsername);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "S'ha produït un error inesperat al servidor."));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("authenticatedUsername", authenticatedUsername);
        response.put("pet", petService.getUserPetByPetName(userName, petName));

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userName}/pet/{petName}")
    @Operation(
            summary = "Delete user Pet",
            description = "Delete user.")
    @Parameter(
            name =  "userName",
            description  = "user name",
            example = "john_doe",
            required = true)
    @Parameter(
            name =  "petName",
            description  = "pet name",
            example = "Kaki",
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
    public ResponseEntity<Map<String,Object>> deleteUserPet(@PathVariable String userName, @PathVariable String petName){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUsername = authentication.getName();

        if(!userValidation(userName, authentication)){
            log.info("L'usuari logat no coincideix amb el del path: " + userName + " : " + authenticatedUsername);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "S'ha produït un error inesperat al servidor."));
        }

        petService.deletePet( userName,  petName);

        Map<String, Object> response = new HashMap<>();
        response.put("authenticatedUsername", authenticatedUsername);
        response.put("result", petService.getGetAllPets());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userName}")
    @Operation(
            summary = "Show user info",
            description = "Show user information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Show user information.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(name = "Success Response",
                                    value = "{[{},{}]}"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication or registration error.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = {
                                    @ExampleObject(name = "Bad Credentials",
                                            summary = "Invalid username or password format (e.g. if the backend validates format, not actual credentials at this stage).",
                                            value = "{\"error\": \"Credencials incorrectes.\"}",
                                            description = "Returned if the registration credentials (e.g., username, password, email) fail basic validation or format checks by the backend during the registration attempt, or if a user with that username/email already exists, leading to a BadCredentialsException or similar internal validation error."
                                    )}))})
    public ResponseEntity<Map<String,Object>> getUserDetails(@PathVariable String userName){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUsername = authentication.getName();

        UserDetails userDetails = userService.loadUserByUsername(userName);
        List<Pet> petList = petService.getGetAllPets();

        Map<String, Object> response = new HashMap<>();
        response.put("authenticatedUsername", authenticatedUsername);
        response.put("pets", petService.getGetAllPets());
        response.put("userinfo", new UserDto((User) userService.loadUserByUsername(userName)));

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userName}")
    @Operation(
            summary = "Delete user",
            description = "Delete user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete user.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(name = "Success Response",
                                    value = "{[{},{}]}"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication or registration error.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = {
                                    @ExampleObject(name = "Bad Credentials",
                                            summary = "Invalid username or password format (e.g. if the backend validates format, not actual credentials at this stage).",
                                            value = "{\"error\": \"Credencials incorrectes.\"}",
                                            description = "Returned if the registration credentials (e.g., username, password, email) fail basic validation or format checks by the backend during the registration attempt, or if a user with that username/email already exists, leading to a BadCredentialsException or similar internal validation error."
                                    )}))})
    public ResponseEntity<Map<String,Object>> deleteUser(@PathVariable String username){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUsername = authentication.getName();

        UserDetails userDetails = userService.loadUserByUsername(username);
        userService.deleteUser(username);

        Map<String, Object> response = new HashMap<>();
        response.put("authenticatedUsername", authenticatedUsername);
        response.put("pets", petService.getGetAllPets());
        response.put("userinfo", userService.loadUserByUsername(username));

        return ResponseEntity.ok(response);
    }

    private boolean userValidation(String userName, Authentication authentication){
        return authentication.getName().equals(userName);
    }

}
