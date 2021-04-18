package com.computerservice.repository.motherboard;

import com.computerservice.entity.pc.motherboard.MotherboardInterface;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.math.BigInteger;

@RepositoryRestResource
public interface MotherboardInterfaceEntityRepository extends JpaRepository<MotherboardInterface, BigInteger> {

    @Query("select case when count(mbi) > 0 then true else false end " +
            "from MotherboardInterface mbi " +
            "where mbi.powerSupplyPin = :powerSupplyInterface " +
            "and mbi.motherboardInterfaceValue = :motherboardInterface")
    boolean existsPowerSupplyWithSuchMotherboardInterface(
            @Param("motherboardInterface") String motherboardInterface,
            @Param("powerSupplyInterface") String powerSupplyInterface
    );

}
