package com.computerservice.repository.processor;

import com.computerservice.entity.pc.processor.Processor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.math.BigInteger;

@RepositoryRestResource
public interface ProcessorEntityRepository extends PagingAndSortingRepository<Processor, BigInteger> {
    Page<Processor> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);
}

