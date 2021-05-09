package com.computerservice.repository.ram;

import com.computerservice.entity.pc.ram.Ram;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@RepositoryRestResource
public interface RamEntityRepository extends PagingAndSortingRepository<Ram, BigInteger> {
    Page<Ram> findByNameContainingIgnoreCase(
            @Param("name") String name,
            Pageable pageable);

    List<Ram> findTop3ByCapacityLessThanEqualAndAmountLessThanEqualAndTypeAndPriceLessThanEqualOrderByAvgBenchDesc(
            @Param("maxRam") int maxRam,
            @Param("numRam") int numRam,
            @Param("ramType") String ramType,
            @Param("price") BigDecimal price
    );
}
