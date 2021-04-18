package com.computerservice.config.security.jwt;

public interface JwtProvider {
    String generateToken(String login, String role);

    boolean validateToken(String token);

    String getLoginFromToken(String token);
}
