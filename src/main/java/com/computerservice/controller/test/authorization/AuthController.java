package com.computerservice.controller.test.authorization;

import com.computerservice.config.security.jwt.JwtProvider;
import com.computerservice.entity.authentication.AuthRequest;
import com.computerservice.entity.authentication.AuthResponse;
import com.computerservice.entity.authentication.RegistrationRequest;
import com.computerservice.entity.user.UserEntity;
import com.computerservice.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtProvider jwtProvider;

    @PostMapping("/register")
    public String registerUser(@RequestBody RegistrationRequest registrationRequest) {
        UserEntity userEntity = new UserEntity();
        userEntity.setPassword(registrationRequest.getPassword());
        userEntity.setLogin(registrationRequest.getLogin());
        userService.saveUser(userEntity);
        return "OK";
    }

    @PostMapping("/auth")
    public AuthResponse auth(@RequestBody AuthRequest request) {
        UserEntity userEntity = userService.findByLoginAndPassword(request.getLogin(), request.getPassword());
        String token = jwtProvider.generateToken(userEntity.getLogin());
        return new AuthResponse(token);
    }
}
