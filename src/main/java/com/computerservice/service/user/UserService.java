package com.computerservice.service.user;

import com.computerservice.entity.role.RoleEntity;
import com.computerservice.entity.user.UserEntity;
import com.computerservice.exception.user.UserDoesntExistInSystem;
import com.computerservice.repository.role.RoleEntityRepository;
import com.computerservice.repository.user.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log
public class UserService {
    private final UserEntityRepository userEntityRepository;
    private final RoleEntityRepository roleEntityRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("$(password.salt)")
    private String passwordSalt;

    public UserEntity saveUser(UserEntity userEntity) {
        RoleEntity userRole = roleEntityRepository.findByName("ROLE_USER");
        userEntity.setRoleEntity(userRole);
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword() + passwordSalt));
        log.info("User was saved in the system.");
        return userEntityRepository.save(userEntity);
    }

    public UserEntity findByLogin(String login) {
        return userEntityRepository.findByLogin(login);
    }

    public UserEntity findByLoginAndPassword(String login, String password) {
        password += passwordSalt;
        UserEntity userEntity = findByLogin(login);
        if (userEntity != null && passwordEncoder.matches(password, userEntity.getPassword())) {
            log.info("User exists in the system.");
            return userEntity;
        }
        log.info("User doesn't exist in the system.");
        throw new UserDoesntExistInSystem();
    }
}
