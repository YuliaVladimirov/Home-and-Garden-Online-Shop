package org.example.homeandgarden.security.jwt;

import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    public static JwtAuthentication generate(Claims claims) {

        String email = claims.getSubject();

        List<?> rolesObjectList = claims.get("roles", List.class);

        List<String> roles = rolesObjectList.stream()
                .map(Object::toString)
                .collect(Collectors.toList());

        return new JwtAuthentication(email, roles);
    }
}