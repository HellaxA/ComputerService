package com.computerservice.repository.resettoken;

import com.computerservice.entity.resettoken.ResetToken;
import com.computerservice.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.Optional;

public interface ResetTokenEntityRepository extends JpaRepository<ResetToken, BigInteger> {

    Optional<ResetToken> findFirstByUserEntity(UserEntity userEntity);

    ResetToken findFirstByToken(String token);
}
