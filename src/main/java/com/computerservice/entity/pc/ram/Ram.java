package com.computerservice.entity.pc.ram;

import com.computerservice.entity.pc.PcComponent;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "ram")
public class Ram extends PcComponent {

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

}
