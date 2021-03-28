package com.computerservice.repository.user;

import com.computerservice.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserEntityRepository extends JpaRepository<UserEntity, Integer> {
    UserEntity findByLogin(String login);
}
