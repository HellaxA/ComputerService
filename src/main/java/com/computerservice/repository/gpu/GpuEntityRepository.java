package com.computerservice.repository.gpu;

import com.computerservice.entity.pc.gpu.Gpu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.math.BigInteger;
import java.util.List;

@RepositoryRestResource
public interface GpuEntityRepository extends PagingAndSortingRepository<Gpu, BigInteger> {
    Page<Gpu> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    List<Gpu> findByIdIn(List<BigInteger> ids);
}
