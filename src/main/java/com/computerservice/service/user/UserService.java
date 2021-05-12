package com.computerservice.service.user;

import com.computerservice.entity.resettoken.ResetToken;
import com.computerservice.entity.user.PasswordDTO;
import com.computerservice.entity.user.UserEntity;

public interface UserService {
    UserEntity saveUser(UserEntity userEntity);
    UserEntity findByLogin(String login);
    UserEntity findByLoginAndPassword(String login, String password);

    void createPasswordResetTokenForUser(UserEntity user, String token);

    boolean validatePassword(PasswordDTO passwordDTO);

    ResetToken findByToken(String token);

    void changeUserPassword(ResetToken resetToken, String password);

    void makeTokenExpired(ResetToken resetToken);
}
