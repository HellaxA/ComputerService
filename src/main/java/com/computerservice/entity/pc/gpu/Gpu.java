package com.computerservice.entity.pc.gpu;


import com.computerservice.entity.pc.PcComponent;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "gpu")
public class Gpu extends PcComponent {

    @Column(name = "add_power_pin")
    private String addPower;

    @Column(name = "tdp")
    private int tdp;

    @Column(name = "avg_bench")
    private double avgBench;

}


