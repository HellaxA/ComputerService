package com.computerservice.entity.pc.pc;

import com.computerservice.entity.pc.gpu.Gpu;
import com.computerservice.entity.pc.motherboard.Motherboard;
import com.computerservice.entity.pc.powersupply.PowerSupply;
import com.computerservice.entity.pc.processor.Processor;
import com.computerservice.entity.pc.ram.Ram;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PcCompListDto {
    private List<PowerSupply> powerSupplies;
    private List<Motherboard> motherboards;
    private List<Gpu> gpus;
    private List<Processor> processors;
    private List<Ram> rams;
    private PcCompatibilityCheckResponseDto pcCompatibilityCheckResponseDto;
}
