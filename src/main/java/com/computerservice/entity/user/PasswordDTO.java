package com.computerservice.entity.user;

import lombok.Data;

@Data
public class PasswordDTO {
    private  String token;
    private String password;

    public PasswordDTO(String token, String password) {
        this.token = token;
        this.password = password;
    }
}