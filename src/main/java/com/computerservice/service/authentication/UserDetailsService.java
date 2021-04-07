package com.computerservice.service.authentication;

import com.computerservice.entity.authentication.CustomUserDetails;

public interface UserDetailsService {
    CustomUserDetails loadUserByUsername(String username);
}
