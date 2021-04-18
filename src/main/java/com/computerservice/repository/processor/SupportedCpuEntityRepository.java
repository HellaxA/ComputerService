package com.computerservice.repository.processor;

import com.computerservice.entity.pc.motherboard.SupportedCpu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface SupportedCpuEntityRepository extends JpaRepository<SupportedCpu, BigInteger> {
}
