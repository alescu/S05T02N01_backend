package com.itacademy.petAcademy.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itacademy.petAcademy.entities.Pet;
import com.itacademy.petAcademy.models.LoginCredentials;
import com.itacademy.petAcademy.models.RegisterCredentials;
import com.itacademy.petAcademy.security.Rol;
import com.itacademy.petAcademy.services.UserService;
import com.itacademy.petAcademy.services.PetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

        @ExtendWith(MockitoExtension.class)
        class AuthControllerTest {

            @Mock
            private Logger logger;

            @Mock
            private UserService userService;

            @Mock
            private PetService petService; // Mock per PetService

            @Mock
            private AuthenticationManager authenticationManager;

            @InjectMocks
            private AuthController authController;

            private MockMvc mockMvc;
            private ObjectMapper objectMapper;

            @BeforeEach
            void setUp() {
                objectMapper = new ObjectMapper();
                mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
            }

            @Test
            @DisplayName("Hauría de registrar un nou usuari y tornar un token JWT")
            void registerUser_success() throws Exception {
                RegisterCredentials credentials = new RegisterCredentials("testuser", "test@example.com", "password123");
                String jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.testToken";

                when(userService.registerService(any(RegisterCredentials.class), eq(Rol.ROLE_USER)))
                        .thenReturn(jwtToken);

                mockMvc.perform(post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(credentials)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.authUsername").value("testuser"))
                        .andExpect(jsonPath("$.jwt-token").value(jwtToken));
            }


            @Test
            @DisplayName("Hauria de tornar un 401 si les credencials son incorrectas")
            void registerUser_badCredentials() throws Exception {
                RegisterCredentials credentials = new RegisterCredentials("baduser", "bad@example.com", "badpass");

                // Configura el mock del userService para que lance una BadCredentialsException
                when(userService.registerService(any(RegisterCredentials.class), eq(Rol.ROLE_USER)))
                        .thenThrow(new BadCredentialsException("Credenciales incorrectas."));

                mockMvc.perform(post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(credentials)))
                        .andExpect(status().isUnauthorized())
                        .andExpect(jsonPath("$.error").exists());
            }

            @Test
            @DisplayName("Hauria de tornar un 401 si l'usuari está deshabilitat")
            void registerUser_disabledUser() throws Exception {
                RegisterCredentials credentials = new RegisterCredentials("disableduser", "disabled@example.com", "password123");

                when(userService.registerService(any(RegisterCredentials.class), eq(Rol.ROLE_USER)))
                        .thenThrow(new DisabledException("El teu compte està deshabilitat."));

                mockMvc.perform(post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(credentials)))
                        .andExpect(status().isUnauthorized())
                        .andExpect(jsonPath("$.error").exists());
            }

            @Test
            @DisplayName("Hauria de tornar un 401 si l'usuari está bloquejat")
            void registerUser_lockedUser() throws Exception {
                RegisterCredentials credentials = new RegisterCredentials("lockeduser", "locked@example.com", "password123");

                when(userService.registerService(any(RegisterCredentials.class), eq(Rol.ROLE_USER)))
                        .thenThrow(new LockedException("El teu compte està bloquejat. Contacta amb l'administrador."));

                mockMvc.perform(post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(credentials)))
                        .andExpect(status().isUnauthorized())
                        .andExpect(jsonPath("$.error").value("El teu compte està bloquejat. Contacta amb l'administrador."));
            }

            @Test
            @DisplayName("Hauria de tornar un 401 pels altres errors d'autentificació")
            void registerUser_authenticationException() throws Exception {
                RegisterCredentials credentials = new RegisterCredentials("authexceptuser", "auth@example.com", "password123");

                when(userService.registerService(any(RegisterCredentials.class), eq(Rol.ROLE_USER)))
                        .thenThrow(new AuthenticationException("Error genérico de autenticación") {}); // Anonymous class for abstract AuthenticationException

                mockMvc.perform(post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(credentials)))
                        .andExpect(status().isUnauthorized())
                        .andExpect(jsonPath("$.error").value("Error d'autenticació."));
            }

            @Test
            @DisplayName("Hauria de tornar un  500 per errors interns del servidor")
            void registerUser_internalServerError() throws Exception {
                RegisterCredentials credentials = new RegisterCredentials("erroruser", "error@example.com", "password123");

                when(userService.registerService(any(RegisterCredentials.class), eq(Rol.ROLE_USER)))
                        .thenThrow(new RuntimeException("Error inesperado en el servicio."));

                mockMvc.perform(post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(credentials)))
                        .andExpect(status().isInternalServerError())
                        .andExpect(jsonPath("$.error").exists());
            }

                @Test
                @DisplayName("Debería logear al usuario y devolver token JWT y datos de mascotas")
                void loginEntry_success() throws Exception {
                    LoginCredentials credentials = new LoginCredentials("testuser", "password123");
                    String jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.loginToken";
                    List<Pet> pets = new ArrayList<>();

                    Pet pet1 = new Pet("Buddy", "Dog", "testuser");
                    pet1.setId("1L");
                    pets.add(pet1);

                    Pet pet2 = new Pet("Whiskers", "Cat", "testuser");
                    pet2.setId("2L");
                    pets.add(pet2);

                    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                            .thenReturn(mock(Authentication.class));
                    doNothing().when(userService).resetFailedLoginAttempts(eq(credentials.getUsername()));

                    when(userService.generateTokenForAuthenticatedUser(eq(credentials.getUsername())))
                            .thenReturn(jwtToken);

                    when(petService.getPetsByUserName(eq(credentials.getUsername())))
                            .thenReturn(pets);

                    mockMvc.perform(post("/auth/login")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(credentials)))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.authUsername").value(credentials.getUsername()))
                            .andExpect(jsonPath("$.jwt-token").value(jwtToken))
                            .andExpect(jsonPath("$.pets").isArray())
                            .andExpect(jsonPath("$.pets[0].petName").value("Buddy"))
                            .andExpect(jsonPath("$.pets[1].petName").value("Whiskers"));

                    verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
                    verify(userService).resetFailedLoginAttempts(eq(credentials.getUsername()));
                    verify(userService).generateTokenForAuthenticatedUser(eq(credentials.getUsername()));
                    verify(petService).getPetsByUserName(eq(credentials.getUsername()));
                }

                @Test
                @DisplayName("Debería devolver 401 si las credenciales de login son incorrectas")
                void loginEntry_badCredentials() throws Exception {
                    LoginCredentials credentials = new LoginCredentials("wronguser", "wrongpass");

                    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                            .thenThrow(new BadCredentialsException("Credenciales incorrectas."));

                    doNothing().when(userService).incrementFailedLoginAttempts(eq(credentials.getUsername()));

                    mockMvc.perform(post("/auth/login")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(credentials)))
                            .andExpect(status().isUnauthorized())
                            .andExpect(jsonPath("$.error").exists());

                    verify(userService).incrementFailedLoginAttempts(eq(credentials.getUsername()));
                    verify(userService, never()).resetFailedLoginAttempts(anyString());
                    verify(userService, never()).generateTokenForAuthenticatedUser(anyString());
                    verify(petService, never()).getPetsByUserName(anyString());
                }

                @Test
                @DisplayName("Debería devolver 401 si el cuenta está bloqueada")
                void loginEntry_lockedAccount() throws Exception {
                    LoginCredentials credentials = new LoginCredentials("lockeduser", "password123");

                    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                            .thenThrow(new LockedException("El teu compte està bloquejat. Contacta amb l'administrador."));

                    mockMvc.perform(post("/auth/login")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(credentials)))
                            .andExpect(status().isUnauthorized())
                            .andExpect(jsonPath("$.error").value("El teu compte està bloquejat. Contacta amb l'administrador."));

                    verify(userService, never()).resetFailedLoginAttempts(anyString());
                    verify(userService, never()).incrementFailedLoginAttempts(anyString());
                }

                @Test
                @DisplayName("Debería devolver 401 para otros errores de autenticación")
                void loginEntry_authenticationException() throws Exception {
                    LoginCredentials credentials = new LoginCredentials("authexceptuser", "password123");

                    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                            .thenThrow(new AuthenticationException("Error genérico de autenticación") {});

                    mockMvc.perform(post("/auth/login")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(credentials)))
                            .andExpect(status().isUnauthorized())
                            .andExpect(jsonPath("$.error").value("Credencials incorrectes."));

                    verify(userService, never()).resetFailedLoginAttempts(anyString());
                    verify(userService, never()).incrementFailedLoginAttempts(anyString());
                }

                @Test
                @DisplayName("Debería devolver 500 para errores internos del servidor durante el login")
                void loginEntry_internalServerError() throws Exception {
                    LoginCredentials credentials = new LoginCredentials("erroruser", "password123");

                    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                            .thenThrow(new RuntimeException("Error inesperado en el sistema de autenticación."));

                    mockMvc.perform(post("/auth/login")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(credentials)))
                            .andExpect(status().isInternalServerError())
                            .andExpect(jsonPath("$.error").value("S'ha produït un error inesperat al servidor."));

                    verify(userService, never()).resetFailedLoginAttempts(anyString());
                    verify(userService, never()).incrementFailedLoginAttempts(anyString());
                }

        }