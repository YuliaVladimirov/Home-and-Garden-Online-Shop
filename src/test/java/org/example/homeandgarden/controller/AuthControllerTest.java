package org.example.homeandgarden.controller;

import org.example.homeandgarden.security.config.SecurityConfig;
import org.example.homeandgarden.security.controller.AuthController;
import org.example.homeandgarden.security.jwt.*;
import org.example.homeandgarden.security.service.AuthService;
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
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authServiceMock;

    @MockBean
    private JwtProvider jwtProviderMock;

    JwtResponse jwtResponse;

    JwtRequest jwtRequest;

    JwtRequestRefresh requestRefresh;


    @BeforeEach
    void setUp() {

        String accessToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0b3JzdGVuYm9ybWFubkBleGFtcGxlLmNvbSIsImV4cCI6MTcyODE1Mjg3NCwicm9sZXMiOlsiQ0xJRU5UIl0sIm5hbWUiOiJUb3JzdGVuIEJvcm1hbm4ifQ.O2i3LRBAHH9Eqkp_dS6d_OVkzpOeJsZhQSRY1acac6TMCgQho4NUskJ8v9Qw1YzA3QeY8FXK6PBOfeWcDxJo4Q";
        String refreshToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0b3JzdGVuYm9ybWFubkBleGFtcGxlLmNvbSIsImV4cCI6MTczMDc0NzU3NH0.03zWFhAd01SZGztIivLGe4dVYwEiDsXCAEVocMsPO36RBjjPCWzB-b1LXY-qBw3ouihkmheJ9Soa5YtaeMr6VQ";


        jwtResponse = JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();



        jwtRequest = JwtRequest.builder()
                .email("torstenbormann@example.com")
                .password("ClientPass1$trong")
                .build();

        requestRefresh = JwtRequestRefresh.builder()
                .refreshToken(refreshToken)
                .build();
    }


    @Test
    void login() throws Exception {
        when(authServiceMock.login(jwtRequest)).thenReturn(jwtResponse);
        this.mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jwtRequest)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(authServiceMock, times(1)).login(jwtRequest);
    }

    @Test
    void getNewAccessToken() throws Exception {
        when(authServiceMock.getAccessToken(requestRefresh.refreshToken)).thenReturn(jwtResponse);
        this.mockMvc.perform(post("/auth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestRefresh)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(authServiceMock, times(1)).getAccessToken(requestRefresh.refreshToken);
    }

    @Test
    @WithMockUser(username = "Test User", roles = {"CLIENT","ADMINISTRATOR"})
    void getNewRefreshToken() throws Exception {
        when(authServiceMock.refresh(requestRefresh.refreshToken)).thenReturn(jwtResponse);
        this.mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestRefresh)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(authServiceMock, times(1)).refresh(requestRefresh.refreshToken);
    }

    @Test
    void shouldNotGetNewRefreshToken() throws Exception {
        when(authServiceMock.refresh(requestRefresh.refreshToken)).thenReturn(jwtResponse);
        this.mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestRefresh)))
                .andDo(print())
                .andExpect(status().isForbidden());

        verify(authServiceMock, never()).refresh(requestRefresh.refreshToken);
    }
}