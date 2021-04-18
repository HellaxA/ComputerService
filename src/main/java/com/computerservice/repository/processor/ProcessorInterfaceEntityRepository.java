package com.computerservice.repository.processor;

import com.computerservice.entity.pc.processor.ProcessorInterface;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.math.BigInteger;

@RepositoryRestResource
public interface ProcessorInterfaceEntityRepository extends JpaRepository<ProcessorInterface, BigInteger> {

    @Query("select case when count(pi) > 0 then true else false end" +
            " from ProcessorInterface pi " +
            "where pi.processorInterfaceValue = :motherboardCpuInterface " +
            "and pi.powerSupplyPin = :powerSupplyCpuInterface")
    boolean existsPowerSupplyWithSuchCpuInterface(
            @Param("motherboardCpuInterface") String motherboardCpuInterface,
            @Param("powerSupplyCpuInterface") String powerSupplyCpuInterface
    );
}
