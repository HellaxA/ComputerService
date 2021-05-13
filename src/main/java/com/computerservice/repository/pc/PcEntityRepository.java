package com.computerservice.repository.pc;

import com.computerservice.entity.pc.pc.Pc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@RepositoryRestResource
public interface PcEntityRepository extends PagingAndSortingRepository<Pc, BigInteger> {
    Page<Pc> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);



    @Modifying
    @Query(value =
            "INSERT INTO pc(power_supply_id, motherboard_id, processor_id, ram_id, name, price, user_id) " +
            "VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7)",
            nativeQuery = true)
    void savePc(BigInteger powerSupplyId, BigInteger motherboardId,
              BigInteger processorId, BigInteger ramId,
              String name, BigDecimal price, BigInteger userId);

    @Modifying
    @Query(value =
            "INSERT INTO pc_gpus(pc_id, gpu_id) " +
                    "VALUES (?1,?2)",
    nativeQuery = true)
    void saveGpuToPc(BigInteger pcId, BigInteger gpuId);

    @Query(value =
            "select * from pc p " +
                    "inner join user_table ut on p.user_id = ut.id " +
                    "where p.name = ?1 and ut.login = ?2",
            nativeQuery = true)
    Pc findPcIdByName(String name, String username);

    @Query(value =
            "select * from pc p " +
            "inner join user_table ut " +
            "on p.user_id = ut.id where login = ?3 offset ?1 limit ?2", nativeQuery = true)
    List<Pc> findAllPaginated(int offset, int size, String login);

    @Query(value = "select count(*) from pc where user_id = ?1", nativeQuery = true)
    int countAllPcsOfUser(BigInteger id);

    @Modifying
    @Query(value = "DELETE FROM pc WHERE id = ?1", nativeQuery = true)
    void removePc(BigInteger id);

    @Modifying
    @Query(value = "DELETE FROM pc_gpus where pc_id = ?1", nativeQuery = true)
    void removeGpusFromPc(BigInteger id);
}

