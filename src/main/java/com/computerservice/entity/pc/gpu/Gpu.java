package com.computerservice.entity.pc.gpu;


import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@Entity
@Table(name = "gpu")
public class Gpu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @Column(name = "add_power_pin")
    private String addPower;

    @Column(name = "tdp")
    private int tdp;

    @Column(name = "avg_bench")
    private double avgBench;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "name")
    private String name;
}


