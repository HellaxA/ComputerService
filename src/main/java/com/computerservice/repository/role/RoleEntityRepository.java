package com.computerservice.repository.role;

import com.computerservice.entity.role.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface RoleEntityRepository extends JpaRepository<RoleEntity, BigInteger> {
    RoleEntity findByName(String name);
}
