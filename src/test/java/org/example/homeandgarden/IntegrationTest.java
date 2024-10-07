package org.example.homeandgarden;

import org.example.homeandgarden.dto.requestdto.UserRequestDto;
import org.example.homeandgarden.entity.User;
import org.example.homeandgarden.repository.UserRepository;
import org.example.homeandgarden.security.jwt.JwtProvider;
import org.example.homeandgarden.security.jwt.JwtRequest;
import org.example.homeandgarden.security.jwt.JwtRequestRefresh;
import org.example.homeandgarden.security.service.AuthService;
import org.example.homeandgarden.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest()
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Order(value = 1)
    @Test
    void registerUser() throws Exception{

        UserRequestDto userRequestDto = UserRequestDto.builder()
                .name("Yulia Vladimirov")
                .email("yuliavladimirov@example.com")
                .phone("+49963293617")
                .password("ClientPass1$trong")
                .build();

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isCreated());


    }

    @Order(value = 2)
    @Test
    void login() throws Exception {

        JwtRequest authRequest = JwtRequest.builder()
                .email("yuliavladimirov@example.com")
                .password("ClientPass1$trong")
                .build();

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty());
    }

    @Order(value = 3)
    @Test
    void getNewAccessToken() throws Exception {

        User user = userRepository.findByEmail("yuliavladimirov@example.com").orElse(null);
        assert user != null;
        String refreshToken = user.getRefreshToken();

        JwtRequestRefresh requestRefresh = JwtRequestRefresh.builder()
                .refreshToken(refreshToken)
                .build();

        mockMvc.perform(post("/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestRefresh)))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isEmpty());

        userRepository.delete(user);
    }
}
