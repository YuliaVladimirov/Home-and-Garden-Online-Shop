package org.example.homeandgarden.controller;

import org.example.homeandgarden.dto.requestdto.*;
import org.example.homeandgarden.dto.responsedto.UserResponseDto;
import org.example.homeandgarden.entity.enums.Role;
import org.example.homeandgarden.security.config.SecurityConfig;
import org.example.homeandgarden.security.jwt.JwtProvider;
import org.example.homeandgarden.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userServiceMock;

    @MockBean
    private JwtProvider jwtProvider;


    private UserRequestDto userRequestDtoClient, userRequestDtoAdmin;


    @BeforeEach
    void setUp() {

        userRequestDtoClient = UserRequestDto.builder()
                .name("Arne Oswald")
                .email("arneoswald@example.com")
                .phone("+496151226")
                .password("ClientPass1$trong")
                .build();

        userRequestDtoAdmin = UserRequestDto.builder()
                .name("Michael Nguyen")
                .email("michaelnguyen@example.com")
                .phone("+496823485")
                .password("AdminPass1$trong")
                .build();
    }


    @Test
    void registerUser() throws Exception {
        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDtoClient)))
                .andDo(print())
                .andExpect(status().isCreated());

        verify(userServiceMock, times(1)).registerUser(userRequestDtoClient);
    }

    @Test
    @WithMockUser(username = "Test User", roles = {"ADMINISTRATOR"})
    void registerAdmin() throws Exception {

        mockMvc.perform(post("/users/registerAdmin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDtoAdmin)))
                .andDo(print())
                .andExpect(status().isCreated());

        verify(userServiceMock, times(1)).registerAdmin(userRequestDtoAdmin);
    }

    @Test
    @WithMockUser(username = "Test User", roles = {"CLIENT"})
    void shouldNotRegisterAdmin() throws Exception {
        mockMvc.perform(post("/users/registerAdmin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDtoAdmin)))
                .andDo(print())
                .andExpect(status().isForbidden());

        verify(userServiceMock, never()).registerAdmin(userRequestDtoAdmin);
    }

    @Test
    @WithMockUser(username = "Test User", roles = {"ADMINISTRATOR"})
    void updateUser() throws Exception {
        Long userId = 1L;
        mockMvc.perform(put("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDtoClient)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userServiceMock, times(1)).updateUser(userId, userRequestDtoClient);
    }

    @Test
    @WithMockUser(username = "Test User", roles = {"CLIENT"})
    void shouldNotUpdateUser() throws Exception {
        Long userId = 1L;
        mockMvc.perform(put("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDtoAdmin)))
                .andDo(print())
                .andExpect(status().isForbidden());

        verify(userServiceMock, never()).updateUser(userId, userRequestDtoClient);
    }


    @Test
    @WithMockUser(username = "Test User", roles = {"ADMINISTRATOR"})
    void deleteUser() throws Exception {
        Long userId = 1L;

        mockMvc.perform(delete("/users/{id}", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userServiceMock, times(1)).deleteUser(userId);
    }

    @Test
    @WithMockUser(username = "Test User", roles = {"CLIENT"})
    void shouldNotDeleteUser() throws Exception {
        Long userId = 1L;

        mockMvc.perform(delete("/users/{id}", userId))
                .andDo(print())
                .andExpect(status().isForbidden());

        verify(userServiceMock, never()).deleteUser(userId);
    }

    @Test
    @WithMockUser(username = "Test User", roles = {"ADMINISTRATOR"})
    void getUserByEmail() throws Exception {

        UserResponseDto userResponseDto = UserResponseDto.builder()
                .userId(1L)
                .name("Arne Oswald")
                .email("arneoswald@example.com")
                .phone("+496151226")
                .role(Role.CLIENT)
                .build();

        when(userServiceMock.getUserByEmail("arneoswald@example.com")).thenReturn(userResponseDto);
        mockMvc.perform(get("/users?email=arneoswald@example.com"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.name").value("Arne Oswald"))
                .andExpect(jsonPath("$.email").value("arneoswald@example.com"));

        verify(userServiceMock, times(1)).getUserByEmail("arneoswald@example.com");
    }

    @Test
    @WithMockUser(username = "Test User", roles = {"CLIENT"})
    void shouldNotGetUserByEmail() throws Exception {

        UserResponseDto userResponseDto = UserResponseDto.builder()
                .userId(1L)
                .name("Arne Oswald")
                .email("arneoswald@example.com")
                .phone("+496151226")
                .role(Role.CLIENT)
                .build();

        when(userServiceMock.getUserByEmail("arneoswald@example.com")).thenReturn(userResponseDto);
        mockMvc.perform(get("/users?email=arneoswald@example.com"))
                .andDo(print())
                .andExpect(status().isForbidden());

        verify(userServiceMock, never()).getUserByEmail("arneoswald@example.com");
    }
}
