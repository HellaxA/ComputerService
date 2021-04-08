package com.computerservice.entity.pc.motherboard;

import lombok.Data;

import javax.persistence.*;
import java.math.BigInteger;

@Data
@Entity
@Table(name = "supported_cpu")
public class SupportedCpu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @Column(name = "cpu_generation")
    private String cpuGeneration;

    @ManyToOne
    @JoinColumn(name = "motherboard_id")
    private Motherboard motherboard;
}
