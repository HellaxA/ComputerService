package com.computerservice.service.pc;

import com.computerservice.entity.pc.pc.*;

public interface PcService {
    PcCompatibilityCheckResponseDto checkPcCompatibility(PcRequestDto pcRequestDto);

    PcCompListDto fixComputerAssembly(PcReqAndResDto pcReqAndResDto);

    PcCompListDto proposeComponents(PcIdsWithMaxPriceDto pcIdsWithMaxPriceDto);
}
