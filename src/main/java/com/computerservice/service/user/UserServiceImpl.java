package com.computerservice.service.user;

import com.computerservice.entity.resettoken.ResetToken;
import com.computerservice.entity.role.RoleEntity;
import com.computerservice.entity.user.PasswordDTO;
import com.computerservice.entity.user.UserEntity;
import com.computerservice.exception.user.UserNotFoundException;
import com.computerservice.repository.resettoken.ResetTokenEntityRepository;
import com.computerservice.repository.role.RoleEntityRepository;
import com.computerservice.repository.user.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log
public class UserServiceImpl implements UserService {
    private final UserEntityRepository userEntityRepository;
    private final RoleEntityRepository roleEntityRepository;
    private final PasswordEncoder passwordEncoder;
    private final ResetTokenEntityRepository resetTokenEntityRepository;

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
        throw new UserNotFoundException();
    }

    @Override
    public void createPasswordResetTokenForUser(UserEntity user, String token) {

        Optional<ResetToken> resetTokenOptional = resetTokenEntityRepository.findFirstByUserEntity(user);
        Date nowPlus3Hrs = new Date(new Date().getTime() + ResetToken.EXPIRATION);

        if (resetTokenOptional.isPresent()) {
            ResetToken resetToken = resetTokenOptional.get();
            resetToken.setExpiryDate(nowPlus3Hrs);
            resetToken.setToken(token);
            resetTokenEntityRepository.save(resetToken);
            log.info("Token has been successfully updated: " + resetToken);
        } else {
            ResetToken newToken = new ResetToken(token, user, nowPlus3Hrs);
            resetTokenEntityRepository.save(newToken);
            log.info("Token has been successfully created: " + newToken);
        }

    }

    @Override
    public boolean validatePassword(PasswordDTO passwordDTO) {
        UserEntity theUser = userEntityRepository.findUserByToken(passwordDTO.getToken());
        String encodedPasswordWithSalt = passwordDTO.getPassword() + passwordSalt;

        return passwordEncoder.matches(encodedPasswordWithSalt, theUser.getPassword());
    }

    @Override
    public ResetToken findByToken(String token) {
        return resetTokenEntityRepository.findFirstByToken(token);
    }

    @Override
    @Transactional
    public void changeUserPassword(ResetToken resetToken, String password) {
        String encodedPasswordWithSalt = passwordEncoder.encode(password + passwordSalt);

        System.out.println(resetToken);
        userEntityRepository.changeUserPassword(encodedPasswordWithSalt, resetToken.getUserEntity().getId());
    }

    @Override
    @Transactional
    public void makeTokenExpired(ResetToken resetToken) {
        userEntityRepository.makeTokenExpired(resetToken.getToken());
    }
}
