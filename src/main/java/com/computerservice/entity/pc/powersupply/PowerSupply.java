package com.computerservice.entity.pc.powersupply;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@Entity
@Table(name = "power_supply")
public class PowerSupply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @Column(name = "motherboard_power_pin")
    private String motherboardPowerPin;

    @Column(name = "gpu_add_power_pin")
    private String gpuAddPowerPin;

    @Column(name = "processor_power_pin")
    private String processorPowerPin;

    @Column(name = "power")
    private int power;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "name")
    private String name;
}
