package com.computerservice.entity.pc.pc;

import com.computerservice.entity.pc.PcComponent;
import com.computerservice.entity.pc.gpu.Gpu;
import com.computerservice.entity.pc.motherboard.Motherboard;
import com.computerservice.entity.pc.powersupply.PowerSupply;
import com.computerservice.entity.pc.processor.Processor;
import com.computerservice.entity.pc.ram.Ram;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "pc")
@NoArgsConstructor
@AllArgsConstructor
public class Pc extends PcComponent {

    @ManyToOne
    @JoinColumn(name = "power_supply_id")
    private PowerSupply powerSupply;

    @ManyToOne
    @JoinColumn(name = "motherboard_id")
    private Motherboard motherboard;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "pc_gpus",
                joinColumns = {
                    @JoinColumn(name = "pc_id", referencedColumnName = "id",
                    nullable = false, updatable = false)
                },
                inverseJoinColumns = {
                    @JoinColumn(name = "gpu_id", referencedColumnName = "id",
                    nullable = false, updatable = false)
                })
    private List<Gpu> gpus;

    @ManyToOne
    @JoinColumn(name = "processor_id")
    private Processor processor;

    @ManyToOne
    @JoinColumn(name = "ram_id")
    private Ram ram;

}
