package org.example.homeandgarden.security.service;

import org.example.homeandgarden.dto.responsedto.UserResponseDto;
import org.example.homeandgarden.entity.User;
import org.example.homeandgarden.mapper.Mappers;
import org.example.homeandgarden.repository.UserRepository;
import org.example.homeandgarden.security.jwt.JwtProvider;
import org.example.homeandgarden.security.jwt.JwtRequest;
import org.example.homeandgarden.security.jwt.JwtResponse;
import io.jsonwebtoken.Claims;
import jakarta.security.auth.message.AuthException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {


    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final Mappers mappers;
    private final PasswordEncoder passwordEncoder;

    public JwtResponse login(JwtRequest authRequest) throws AuthException {
        User user = userRepository.findByEmail(authRequest.getEmail()).orElse(null);
        if (user != null) {
            final UserResponseDto userResponseDto = mappers.convertToUserResponseDto(user);

            if (passwordEncoder.matches(authRequest.getPassword(), userResponseDto.getPasswordHash())) {
                final String accessToken = jwtProvider.generateAccessToken(userResponseDto);
                final String refreshToken = jwtProvider.generateRefreshToken(userResponseDto);

                user.setRefreshToken(refreshToken);
                userRepository.save(user);
                return new JwtResponse(accessToken, refreshToken);

            } else {
                throw new AuthException("Wrong password.");
            }
        } else {
            throw new AuthException("User not found in database.");
        }
    }

            public JwtResponse getAccessToken (@NonNull String refreshToken) throws AuthException {
                if (jwtProvider.validateRefreshToken(refreshToken)) {
                    final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
                    final String email = claims.getSubject();

                    User user = userRepository.findByEmail(email).orElse(null);
                    if (user != null) {
                        final String savedRefreshToken = user.getRefreshToken();
                        if (savedRefreshToken != null && savedRefreshToken.equals(refreshToken)) {
                            final UserResponseDto userResponseDto = mappers.convertToUserResponseDto(user);

                            final String accessToken = jwtProvider.generateAccessToken(userResponseDto);
                            return new JwtResponse(accessToken, null);
                        }
                    } else {
                        throw new AuthException("User not found in database.");
                    }
                } throw new AuthException("Invalid JWT token. Please, login.");
            }


            public JwtResponse refresh (@NonNull String refreshToken) throws AuthException {
                JwtResponse jwtResponse = null;
                if (jwtProvider.validateRefreshToken(refreshToken)) {
                    final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
                    final String email = claims.getSubject();

                    User user = userRepository.findByEmail(email).orElse(null);
                    if (user != null) {
                        final String savedRefreshToken = user.getRefreshToken();

                        if (savedRefreshToken != null && savedRefreshToken.equals(refreshToken)) {
                            final UserResponseDto userResponseDto = mappers.convertToUserResponseDto(user);

                            final String newAccessToken = jwtProvider.generateAccessToken(userResponseDto);
                            final String newRefreshToken = jwtProvider.generateRefreshToken(userResponseDto);

                            user.setRefreshToken(refreshToken);
                            userRepository.save(user);
                            jwtResponse = new JwtResponse(newAccessToken, newRefreshToken);
                        }
                    } else {
                        throw new AuthException("User not found in database");
                    }
                } else {
                    throw new AuthException("Invalid JWT token. Please, login.");
                }
                return jwtResponse;
            }
        }
