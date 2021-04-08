package com.computerservice.entity.pc.motherboard;

import lombok.Data;

import javax.persistence.*;
import java.math.BigInteger;

@Data
@Entity
@Table(name = "motherboard_interface")
public class MotherboardInterface {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @Column(name = "power_supply_pin")
    private String powerSupplyPin;

    @Column(name = "motherboard_interface_value")
    private String motherboardInterfaceValue;
}
