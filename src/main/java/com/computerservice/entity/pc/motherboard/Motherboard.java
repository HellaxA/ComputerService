package com.computerservice.entity.pc.motherboard;

import com.computerservice.entity.pc.PcComponent;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "motherboard")
public class Motherboard extends PcComponent {

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
}
