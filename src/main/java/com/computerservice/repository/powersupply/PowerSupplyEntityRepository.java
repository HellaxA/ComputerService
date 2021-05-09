package com.computerservice.repository.powersupply;


import com.computerservice.entity.pc.powersupply.PowerSupply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.math.BigInteger;
import java.util.List;

@RepositoryRestResource(collectionResourceRel = "power-supplies", path = "power-supplies")
public interface PowerSupplyEntityRepository extends PagingAndSortingRepository<PowerSupply, BigInteger> {
    Page<PowerSupply> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    @Query(
            value = "select * " +
                    "from power_supply ps " +
                    "where gpu_add_power_pin in ( " +
                    "    select power_supply_pin " +
                    "    from gpu_interface " +
                    "    where gpu_interface_value LIKE ?1) " +
                    "and gpu_add_power_pin in (\n" +
                    "    select power_supply_pin\n" +
                    "    from gpu_interface\n" +
                    "    where gpu_interface_value LIKE ?2)" +
                    "  and motherboard_power_pin in ( " +
                    "    select power_supply_pin " +
                    "    from motherboard_interface " +
                    "    where motherboard_interface_value = ?3) " +
                    "  and processor_power_pin in ( " +
                    "    select power_supply_pin " +
                    "    from processor_interface " +
                    "    where processor_interface_value = ?4) " +
                    "  and power >= ?5 order by price limit 3 ",
            nativeQuery = true)
    List<PowerSupply> findCompatiblePowerSupply(String gpuAddPowerPin1,
                                                String gpuAddPowerPin2,
                                                String motherboardPowerPin,
                                                String processorPowerPin,
                                                int tdp
    );
}
