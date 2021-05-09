package com.computerservice.entity.pc.pc;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class PcCompatibilityCheckResponseDto {
    private boolean isPowerSupplyCompatibleWithMotherboardPower;
    private boolean isPowerSupplyCompatibleWithMotherboardCpuPower;
    private boolean isProcessorCompatibleWithMotherboardSocket;
    private boolean isRamTypeCompatibleWithMotherboard;
    private boolean isRamAmountCompatibleWithMotherboard;
    private boolean isRamGbAmountCompatibleWithMotherboard;
    private boolean isTdpValid;
    private Map<String, String> powerSupplyCompatibilityWithGpuPower;
}
