package com.computerservice.repository.gpu;

import com.computerservice.entity.pc.gpu.Gpu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@RepositoryRestResource
public interface GpuEntityRepository extends PagingAndSortingRepository<Gpu, BigInteger> {
    Page<Gpu> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    List<Gpu> findByIdIn(List<BigInteger> ids);

    Optional<Gpu> findFirstByPriceLessThanEqualOrderByAvgBenchDesc(
            @Param("price") BigDecimal price
    );

    @Query(value = "select * from gpu " +
            "where price <= ?1 " +
            "and tdp <= ?2 order by avg_bench desc LIMIT 1"
            , nativeQuery = true)
    Optional<Gpu> findCompatibleGpuWithPS(BigDecimal gpuMaxPrice, int tdp);
}
