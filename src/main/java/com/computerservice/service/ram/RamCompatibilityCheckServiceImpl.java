package com.computerservice.service.ram;

import com.computerservice.entity.pc.motherboard.Motherboard;
import com.computerservice.entity.pc.ram.Ram;
import org.springframework.stereotype.Service;

@Service
public class RamCompatibilityCheckServiceImpl implements RamCompatibilityCheckService {

    @Override
    public boolean checkRamTypeCompatibilityWithMotherboard(Ram ram, Motherboard motherboard) {
        return ram.getType().equals(motherboard.getRamType());

    }

    @Override
    public boolean checkRamAmountCompatibilityWithMotherboard(Ram ram, Motherboard motherboard) {
        return ram.getAmount() <= motherboard.getNumRam();
    }

    @Override
    public boolean checkRamGbAmountCompatibilityWithMotherboard(Ram ram, Motherboard motherboard) {
        return ram.getAmount() * ram.getCapacity() <= motherboard.getMaxRam();
    }
}
