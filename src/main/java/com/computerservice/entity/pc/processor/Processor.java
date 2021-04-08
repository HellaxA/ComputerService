package com.computerservice.entity.pc.processor;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@Entity
@Table(name = "processor")
public class Processor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @Column(name = "socket")
    private String socket;

    @Column(name = "tdp")
    private int tdp;

    @Column(name = "core8pts")
    private double core8pts;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "name")
    private String name;

}


