package com.computerservice.repository.user;

import com.computerservice.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.math.BigInteger;

public interface UserEntityRepository extends JpaRepository<UserEntity, BigInteger> {
    UserEntity findByLogin(String login);

    @Query(value =
            "select * from user_table " +
                    "where id in " +
                    "   (select user_id " +
                    "   from reset_token " +
                    "   where token = ?1)",
            nativeQuery = true)
    UserEntity findUserByToken(String token);

    @Modifying
    @Query("UPDATE UserEntity " +
            "SET password = :encodedPasswordWithSalt " +
            "WHERE id = :id")
    void changeUserPassword(String encodedPasswordWithSalt, BigInteger id);

    @Modifying
    @Query("UPDATE ResetToken " +
            "SET expiryDate = current_timestamp " +
            "WHERE token = :token")
    void makeTokenExpired(String token);
}
