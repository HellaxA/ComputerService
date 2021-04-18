package com.computerservice.service.pc;

import com.computerservice.entity.pc.pc.PcRequestDto;
import com.computerservice.entity.pc.pc.PcResponseDto;

public interface PcCompatibilityCheckService {
    PcResponseDto checkForCompatibility(PcRequestDto pcRequestDto);
}
