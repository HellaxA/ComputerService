package com.computerservice.entity.pc.ram;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@Entity
@Table(name = "ram")
public class Ram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @Column(name = "capacity")
    private int capacity;

    @Column(name = "type")
    private String type;

    @Column(name = "amount")
    private int amount;

    @Column(name = "freq")
    private double freq;

    @Column(name = "avg_bench")
    private double avgBench;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "name")
    private String name;
}
