package com.computerservice.entity.authentication;

import lombok.Data;
import lombok.NonNull;

@Data
public class RegistrationRequest {
    @NonNull
    private String login;
    @NonNull
    private String password;
}
