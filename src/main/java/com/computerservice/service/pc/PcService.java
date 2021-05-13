package com.computerservice.service.pc;

import com.computerservice.entity.pc.pc.*;

import java.math.BigInteger;
import java.util.List;

public interface PcService {
    PcCompatibilityCheckResponseDto checkPcCompatibility(PcRequestDto pcRequestDto);

    PcCompatibilityCheckResponseDto checkPcCompatibility(Pc pc);

    PcCompListDto fixComputerAssembly(PcReqAndResDto pcReqAndResDto);

    PcCompListDto proposeComponents(PcIdsWithMaxPriceDto pcIdsWithMaxPriceDto);

    Pc savePc(PcRequestDto pcRequestDto);

    List<Pc> findAllPaginated(int page, int size);

    int countAllPcsOfUser();

    void removePc(BigInteger id);
}
