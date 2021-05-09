package com.computerservice.entity.pc.pc;

import lombok.Data;

@Data
public class PcReqAndResDto {
    private PcCompatibilityCheckResponseDto pcCompatibilityCheckResponseDto;
    private Pc pc;
}
