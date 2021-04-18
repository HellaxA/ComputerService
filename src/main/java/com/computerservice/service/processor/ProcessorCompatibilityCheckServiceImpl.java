package com.computerservice.service.processor;

import com.computerservice.entity.pc.motherboard.Motherboard;
import com.computerservice.entity.pc.processor.Processor;
import org.springframework.stereotype.Service;

@Service
public class ProcessorCompatibilityCheckServiceImpl implements ProcessorCompatibilityCheckService {

    @Override
    public boolean checkCompatibilityWithMotherboardSocket(Processor processor, Motherboard motherboard) {
        return processor.getSocket().equals(motherboard.getSocket());
    }
}
