package com.computerservice.repository.motherboard;

import com.computerservice.entity.pc.motherboard.Motherboard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

@RepositoryRestResource
public interface MotherboardEntityRepository extends PagingAndSortingRepository<Motherboard, BigInteger> {
    Page<Motherboard> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    Optional<Motherboard> findFirstByPriceLessThanEqualOrderByM2Desc(@Param("price") BigDecimal price);

    @Query(value = "select * " +
            "            from motherboard " +
            "            where socket LIKE ?1 " +
            "              and num_ram >= ?2 " +
            "              and max_ram >= ?3 " +
            "              and power_pin in ( " +
            "                select motherboard_interface_value " +
            "                from motherboard_interface " +
            "                where power_supply_pin LIKE ?4) " +
            "              and processor_power_pin in ( " +
            "                select processor_interface_value " +
            "                from processor_interface " +
            "                where power_supply_pin LIKE ?5) " +
            "              and price < ?6 ORDER BY m2 desc LIMIT 1",
            nativeQuery = true)
    Optional<Motherboard> findCompatibleMotherboardWithCpuRamPS(
            String socket,
            int amount,
            int maxRam,
            String motherboardPowerPin,
            String motherboardCpuPowerPin,
            BigDecimal mbMaxPrice
    );
}
