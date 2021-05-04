package com.computerservice.controller.authorization;

import com.computerservice.config.security.jwt.JwtProviderImpl;
import com.computerservice.entity.authentication.AuthRequest;
import com.computerservice.entity.authentication.AuthResponse;
import com.computerservice.entity.authentication.RegistrationRequest;
import com.computerservice.entity.authentication.StatusResponse;
import com.computerservice.entity.user.UserEntity;
import com.computerservice.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtProviderImpl jwtProviderImpl;

    @PostMapping("/register")
    public ResponseEntity<StatusResponse> registerUser(@RequestBody RegistrationRequest registrationRequest) {
        UserEntity userEntity = new UserEntity();
        userEntity.setPassword(registrationRequest.getPassword());
        userEntity.setLogin(registrationRequest.getLogin());
        userService.saveUser(userEntity);
        return ResponseEntity.ok(new StatusResponse("success"));
    }

    @PostMapping("/auth")
    public ResponseEntity<AuthResponse> auth(@RequestBody AuthRequest request) {
        UserEntity userEntity = userService.findByLoginAndPassword(request.getLogin(), request.getPassword());
        String token = jwtProviderImpl.generateToken(userEntity.getLogin(), userEntity.getRoleEntity().getName());
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
