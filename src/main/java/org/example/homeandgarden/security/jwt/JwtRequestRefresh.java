package org.example.homeandgarden.security.jwt;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JwtRequestRefresh {

    public String refreshToken;

}