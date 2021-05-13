package com.computerservice.entity.pc.pc;

import com.computerservice.entity.pc.PcComponent;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigInteger;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class PcRequestDto extends PcComponent {
    private BigInteger powerSupplyId;
    private BigInteger motherboardId;
    private List<BigInteger> gpuIds;
    private BigInteger processorId;
    private BigInteger ramId;
    private BigInteger userId;
}
