package com.computerservice.entity.pc.motherboard;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@Entity
@Table(name = "motherboard")
public class Motherboard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @Column(name = "socket")
    private String socket;

    @Column(name = "max_ram")
    private int maxRam;

    @Column(name = "ram_type")
    private String ramType;

    @Column(name = "num_ram")
    private int numRam;

    @Column(name = "power_pin")
    private String powerPin;

    @Column(name = "processor_power_pin")
    private String processorPowerPin;

    @Column(name = "chipset")
    private String chipset;

    @Column(name = "form_factor")
    private String formFactor;

    @Column(name="m2")
    private boolean m2;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "name")
    private String name;
}
