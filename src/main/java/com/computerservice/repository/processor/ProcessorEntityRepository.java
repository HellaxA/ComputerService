package com.computerservice.repository.processor;

import com.computerservice.entity.pc.processor.Processor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@RepositoryRestResource
public interface ProcessorEntityRepository extends PagingAndSortingRepository<Processor, BigInteger> {
    Page<Processor> findByNameContainingIgnoreCase(
            @Param("name") String name, Pageable pageable);

    List<Processor> findTop5ByTdpLessThanEqualAndPriceLessThanEqualAndSocketOrderByCore8ptsDesc(
            @Param("tdp") int tdp,
            @Param("price") BigDecimal price,
            @Param("motherboardSocket") String motherboardSocket
    );

    Optional<Processor> findFirstBySocketAndPriceLessThanEqualOrderByCore8ptsDesc(
            @Param("socket") String socket,
            @Param("price") BigDecimal price
    );
}

