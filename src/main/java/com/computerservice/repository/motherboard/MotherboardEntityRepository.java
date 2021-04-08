package com.computerservice.repository.motherboard;

import com.computerservice.entity.pc.motherboard.Motherboard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.math.BigInteger;

@RepositoryRestResource
public interface MotherboardEntityRepository extends PagingAndSortingRepository<Motherboard, BigInteger> {
    Page<Motherboard> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);
}
