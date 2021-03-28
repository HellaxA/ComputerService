package com.computerservice.entity.authentication;

import lombok.Data;

@Data
public class AuthRequest {
    private String login;
    private String password;
}
