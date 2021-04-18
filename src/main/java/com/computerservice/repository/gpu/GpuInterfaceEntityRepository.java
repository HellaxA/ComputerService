package com.computerservice.repository.gpu;

import com.computerservice.entity.pc.gpu.GpuInterface;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.math.BigInteger;

@RepositoryRestResource
public interface GpuInterfaceEntityRepository extends JpaRepository<GpuInterface, BigInteger> {

    @Query("select case when count(gpui) > 0 then true else false end " +
            "from GpuInterface gpui " +
            "where gpui.gpuInterfaceValue = :gpuInterface " +
            "and gpui.powerSupplyPin = :powerSupplyInterface")
    boolean existsPowerSupplyWithSuchGpuInterface(
            @Param("gpuInterface") String gpuInterface,
            @Param("powerSupplyInterface") String powerSupplyInterface
    );
}
