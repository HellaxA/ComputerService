package com.computerservice.repository.user;

import com.computerservice.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface UserEntityRepository extends JpaRepository<UserEntity, BigInteger> {
    UserEntity findByLogin(String login);
}
