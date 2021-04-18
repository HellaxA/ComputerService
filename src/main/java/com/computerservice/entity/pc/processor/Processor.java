package com.computerservice.entity.pc.processor;

import com.computerservice.entity.pc.PcComponent;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "processor")
public class Processor extends PcComponent {

    @Column(name = "socket")
    private String socket;

    @Column(name = "tdp")
    private int tdp;

    @Column(name = "core8pts")
    private double core8pts;

}


