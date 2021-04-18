package com.computerservice.service.powersupply;

import com.computerservice.entity.pc.gpu.Gpu;
import com.computerservice.entity.pc.motherboard.Motherboard;
import com.computerservice.entity.pc.powersupply.PowerSupply;
import com.computerservice.entity.pc.processor.Processor;

import java.util.List;
import java.util.Map;

public interface PowerSupplyCompatibilityCheckService {
    boolean checkCompatibilityWithMotherboardPower(PowerSupply powerSupply, Motherboard motherboard);

    boolean checkCompatibilityWithMotherboardCpuPower(PowerSupply powerSupply, Motherboard motherboard);

    Map<String, String> checkCompatibilityWithGpuPower(PowerSupply powerSupply, List<Gpu> gpus);

    boolean checkTdp(PowerSupply powerSupply, Processor processor, List<Gpu> gpus);
}
