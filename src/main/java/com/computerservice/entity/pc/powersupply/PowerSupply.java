package com.computerservice.entity.pc.powersupply;

import com.computerservice.entity.pc.PcComponent;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "power_supply")
public class PowerSupply extends PcComponent {

    @Column(name = "motherboard_power_pin")
    private String motherboardPowerPin;

    @Column(name = "gpu_add_power_pin")
    private String gpuAddPowerPin;

    @Column(name = "processor_power_pin")
    private String processorPowerPin;

    @Column(name = "power")
    private int power;

}
