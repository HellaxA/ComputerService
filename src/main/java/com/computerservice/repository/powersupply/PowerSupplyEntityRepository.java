package com.computerservice.repository.powersupply;


import com.computerservice.entity.pc.powersupply.PowerSupply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.math.BigInteger;

@RepositoryRestResource(collectionResourceRel = "power-supplies", path = "power-supplies")
public interface PowerSupplyEntityRepository extends PagingAndSortingRepository<PowerSupply, BigInteger> {
    Page<PowerSupply> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);
}
