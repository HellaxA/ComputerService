package com.computerservice.service.processor;

import com.computerservice.entity.pc.motherboard.Motherboard;
import com.computerservice.entity.pc.processor.Processor;

public interface ProcessorCompatibilityCheckService {
    boolean checkCompatibilityWithMotherboardSocket(Processor processor, Motherboard motherboard);
}
