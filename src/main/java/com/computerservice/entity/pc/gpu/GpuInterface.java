package com.computerservice.entity.pc.gpu;

import lombok.Data;

import javax.persistence.*;
import java.math.BigInteger;

@Data
@Entity
@Table(name = "gpu_interface")
public class GpuInterface {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @Column(name = "power_supply_pin")
    private String powerSupplyPin;

    @Column(name = "gpu_interface_value")
    private String gpuInterfaceValue;
}
