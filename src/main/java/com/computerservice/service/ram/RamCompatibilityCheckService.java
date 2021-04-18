package com.computerservice.service.ram;

import com.computerservice.entity.pc.motherboard.Motherboard;
import com.computerservice.entity.pc.ram.Ram;

public interface RamCompatibilityCheckService {
    boolean checkRamTypeCompatibilityWithMotherboard(Ram ram, Motherboard motherboard);

    boolean checkRamAmountCompatibilityWithMotherboard(Ram ram, Motherboard motherboard);

    boolean checkRamGbAmountCompatibilityWithMotherboard(Ram ram, Motherboard motherboard);
}
