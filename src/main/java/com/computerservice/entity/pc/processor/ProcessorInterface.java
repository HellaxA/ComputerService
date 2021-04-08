package com.computerservice.entity.pc.processor;

import lombok.Data;

import javax.persistence.*;
import java.math.BigInteger;

@Data
@Entity
@Table(name = "processor_interface")
public class ProcessorInterface {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @Column(name = "power_supply_pin")
    private String powerSupplyPin;

    @Column(name = "processor_interface_value")
    private String processorInterfaceValue;
}
