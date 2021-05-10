package com.computerservice.repository.ram;

import com.computerservice.entity.pc.ram.Ram;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
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

    @Query(value = "select * from ram " +
            "where price <= ?1 " +
            "and type = ?2 " +
            "and amount <= ?3 " +
            "and capacity * amount <= ?4 " +
            "ORDER BY avg_bench desc",
             nativeQuery = true)
    List<Ram> findTop3RamsProposals(
            @Param("price") BigDecimal price,
            @Param("type") String type,
            @Param("numRam") int numRam,
            @Param("capacity") int capacity
    );


}
