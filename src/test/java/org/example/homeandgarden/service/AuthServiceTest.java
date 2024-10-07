package org.example.homeandgarden.service;

import org.example.homeandgarden.dto.responsedto.UserResponseDto;
import org.example.homeandgarden.entity.User;
import org.example.homeandgarden.entity.enums.Role;
import org.example.homeandgarden.mapper.Mappers;
import org.example.homeandgarden.repository.UserRepository;
import org.example.homeandgarden.security.jwt.JwtProvider;
import org.example.homeandgarden.security.jwt.JwtRequest;
import org.example.homeandgarden.security.jwt.JwtResponse;
import org.example.homeandgarden.security.service.AuthService;
import jakarta.security.auth.message.AuthException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private Mappers mappers;

    @Mock
    private PasswordEncoder passwordEncoderMock;

    @Mock
    private JwtProvider jwtProviderMock;

    @InjectMocks
    private AuthService authServiceMock;


    private AuthException authException;

    private String accessToken, refreshToken, expiredRefreshToken;

    private User user;
    private UserResponseDto userResponseDto;

    @BeforeEach
    void setUp() {

        userResponseDto = UserResponseDto.builder()
                .userId(1L)
                .name("Torsten Bormann")
                .email("torstenbormann@example.com")
                .phone("+496880152")
                .passwordHash("$2a$10$yovX4MDz2oZKpqq6DiWfrOkpJ3.xzCmj8cko5vNWN8kfZamm3AdTa")
                .role(Role.CLIENT)
                .build();

        user = new User(1L,
                "Torsten Bormann",
                "torstenbormann@example.com",
                "+496880152",
                "$2a$10$yovX4MDz2oZKpqq6DiWfrOkpJ3",
                Role.CLIENT,
                refreshToken,
                null,
                null,
                null);


        accessToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0b3JzdGVuYm9ybWFubkBleGFtcGxlLmNvbSIsImV4cCI6MTcyODE1Mjg3NCwicm9sZXMiOlsiQ0xJRU5UIl0sIm5hbWUiOiJUb3JzdGVuIEJvcm1hbm4ifQ.O2i3LRBAHH9Eqkp_dS6d_OVkzpOeJsZhQSRY1acac6TMCgQho4NUskJ8v9Qw1YzA3QeY8FXK6PBOfeWcDxJo4Q";

        refreshToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0b3JzdGVuYm9ybWFubkBleGFtcGxlLmNvbSIsImV4cCI6MTczMDc0NzU3NH0.03zWFhAd01SZGztIivLGe4dVYwEiDsXCAEVocMsPO36RBjjPCWzB-b1LXY-qBw3ouihkmheJ9Soa5YtaeMr6VQ";

        expiredRefreshToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0b3JzdGVuYm9ybWFubkBleGFtcGxlLmNvbSIsImV4cCI6MTcyMzk4NzA0Mn0.-n33W1Qxu4r7EOc6aTakXRw4aFRsaJkDd7CNzHv9N2X1xsA27v-H4Ert92fHdwnu36iw6o7kh_h9kJYW5dA5MQ";


    }

    @Test
    void login() throws AuthException {

        JwtRequest authRequest = JwtRequest.builder()
                .email("torstenbormann@example.com")
                .password("ClientPass1$trong")
                .build();

        JwtRequest wrongMailAuthRequest = JwtRequest.builder()
                .email("wrongemail@example.com")
                .password("ClientPass1$trong")
                .build();

        JwtRequest wrongPasswordAuthRequest = JwtRequest.builder()
                .email("torstenbormann@example.com")
                .password("WrongPass1$trong")
                .build();

        when(userRepositoryMock.findByEmail(authRequest.getEmail())).thenReturn(Optional.of(user));
        when(mappers.convertToUserResponseDto(user)).thenReturn(userResponseDto);
        when(passwordEncoderMock.matches(authRequest.getPassword(), userResponseDto.getPasswordHash())).thenReturn(true);
        when(jwtProviderMock.generateAccessToken(userResponseDto)).thenReturn(accessToken);
        when(jwtProviderMock.generateRefreshToken(userResponseDto)).thenReturn(refreshToken);

        JwtResponse jwtResponse = authServiceMock.login(authRequest);

        verify(userRepositoryMock, times(1)).findByEmail(authRequest.getEmail());
        verify(mappers, times(1)).convertToUserResponseDto(user);
        verify(passwordEncoderMock, times(1)).matches(authRequest.getPassword(), userResponseDto.getPasswordHash());
        verify(jwtProviderMock, times(1)).generateAccessToken(userResponseDto);
        verify(jwtProviderMock, times(1)).generateRefreshToken(userResponseDto);


        assertEquals(accessToken, jwtResponse.getAccessToken());
        assertEquals(refreshToken, jwtResponse.getRefreshToken());


        when(userRepositoryMock.findByEmail(wrongMailAuthRequest.getEmail())).thenReturn(Optional.empty());
        authException = assertThrows(AuthException.class,
                () -> authServiceMock.login(wrongMailAuthRequest));
        assertEquals("User not found in database.", authException.getMessage());


        when(passwordEncoderMock.matches(wrongPasswordAuthRequest.getPassword(), userResponseDto.getPasswordHash())).thenReturn(false);
        authException = assertThrows(AuthException.class,
                () -> authServiceMock.login(wrongPasswordAuthRequest));
        assertEquals("Wrong password.", authException.getMessage());
    }

}