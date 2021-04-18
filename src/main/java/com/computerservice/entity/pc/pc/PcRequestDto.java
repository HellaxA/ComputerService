package com.computerservice.entity.pc.pc;

import lombok.Data;

import java.math.BigInteger;
import java.util.List;

@Data
public class PcRequestDto {
    private BigInteger powerSupplyId;
    private BigInteger motherboardId;
    private List<BigInteger> gpuIds;
    private BigInteger processorId;
    private BigInteger ramId;
}
