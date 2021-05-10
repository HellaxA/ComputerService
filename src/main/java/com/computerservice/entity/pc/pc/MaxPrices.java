package com.computerservice.entity.pc.pc;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MaxPrices {
    private BigDecimal gpuMaxPrice;
    private BigDecimal motherboardMaxPrice;
    private BigDecimal ramMaxPrice;
    private BigDecimal powerSupplyMaxPrice;
    private BigDecimal processorMaxPrice;
}
