package com.computerservice.config.security;

public interface UserDetailsService {
    CustomUserDetails loadUserByUsername(String username);
}
