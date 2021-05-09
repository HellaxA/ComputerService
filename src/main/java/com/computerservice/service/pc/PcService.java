package com.computerservice.service.pc;

import com.computerservice.entity.pc.pc.PcCompListDto;
import com.computerservice.entity.pc.pc.PcReqAndResDto;
import com.computerservice.entity.pc.pc.PcRequestDto;
import com.computerservice.entity.pc.pc.PcCompatibilityCheckResponseDto;

public interface PcService {
    PcCompatibilityCheckResponseDto checkPcCompatibility(PcRequestDto pcRequestDto);

    PcCompListDto fixComputerAssembly(PcReqAndResDto pcReqAndResDto);

    PcCompListDto proposeComponents(PcRequestDto pcRequestDto);
}
