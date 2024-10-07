package org.example.homeandgarden.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class JwtResponse {

    private final String type = "Bearer";

    private String accessToken;

    private String refreshToken;
}