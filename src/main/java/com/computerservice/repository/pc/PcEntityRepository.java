package com.computerservice.repository.pc;

import com.computerservice.entity.pc.pc.Pc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.math.BigInteger;

@RepositoryRestResource
public interface PcEntityRepository extends PagingAndSortingRepository<Pc, BigInteger> {
    Page<Pc> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);
}

