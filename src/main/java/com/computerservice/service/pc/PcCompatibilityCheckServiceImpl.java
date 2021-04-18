package com.computerservice.service.pc;

import com.computerservice.entity.pc.gpu.Gpu;
import com.computerservice.entity.pc.motherboard.Motherboard;
import com.computerservice.entity.pc.pc.PcRequestDto;
import com.computerservice.entity.pc.pc.PcResponseDto;
import com.computerservice.entity.pc.powersupply.PowerSupply;
import com.computerservice.entity.pc.processor.Processor;
import com.computerservice.entity.pc.ram.Ram;
import com.computerservice.exception.gpu.GpuNotFoundException;
import com.computerservice.exception.motherboard.MotherboardNotFoundException;
import com.computerservice.exception.powersupply.PowerSupplyNotFoundException;
import com.computerservice.exception.processor.ProcessorNotFoundException;
import com.computerservice.exception.ram.RamNotFoundException;
import com.computerservice.repository.gpu.GpuEntityRepository;
import com.computerservice.repository.motherboard.MotherboardEntityRepository;
import com.computerservice.repository.powersupply.PowerSupplyEntityRepository;
import com.computerservice.repository.processor.ProcessorEntityRepository;
import com.computerservice.repository.ram.RamEntityRepository;
import com.computerservice.service.processor.ProcessorCompatibilityCheckService;
import com.computerservice.service.powersupply.PowerSupplyCompatibilityCheckService;
import com.computerservice.service.ram.RamCompatibilityCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PcCompatibilityCheckServiceImpl implements PcCompatibilityCheckService {

    private final PowerSupplyEntityRepository powerSupplyEntityRepository;
    private final MotherboardEntityRepository motherboardEntityRepository;
    private final GpuEntityRepository gpuEntityRepository;
    private final RamEntityRepository ramEntityRepository;
    private final ProcessorEntityRepository processorEntityRepository;
    private final PowerSupplyCompatibilityCheckService powerSupplyCompatibilityCheckService;
    private final ProcessorCompatibilityCheckService processorCompatibilityCheckService;
    private final RamCompatibilityCheckService ramCompatibilityCheckService;

    public PcResponseDto checkForCompatibility(PcRequestDto pcRequestDto) {
        BigInteger powerSupplyId = pcRequestDto.getPowerSupplyId();
        BigInteger motherboardId = pcRequestDto.getMotherboardId();
        BigInteger ramId = pcRequestDto.getRamId();
        BigInteger processorId = pcRequestDto.getProcessorId();
        List<BigInteger> gpuIds = pcRequestDto.getGpuIds();

        PowerSupply powerSupply = powerSupplyEntityRepository
                .findById(powerSupplyId)
                .orElseThrow(PowerSupplyNotFoundException::new);
        Motherboard motherboard = motherboardEntityRepository
                .findById(motherboardId)
                .orElseThrow(MotherboardNotFoundException::new);
        Ram ram = ramEntityRepository
                .findById(ramId)
                .orElseThrow(RamNotFoundException::new);
        Processor processor = processorEntityRepository
                .findById(processorId)
                .orElseThrow(ProcessorNotFoundException::new);

        List<Gpu> gpus = getAllGpusById(gpuIds);


        boolean isPowerSupplyCompatibleWithMotherboardPower = powerSupplyCompatibilityCheckService
                .checkCompatibilityWithMotherboardPower(powerSupply, motherboard);

        boolean isPowerSupplyCompatibleWithMotherboardCpuPower = powerSupplyCompatibilityCheckService
                .checkCompatibilityWithMotherboardCpuPower(powerSupply, motherboard);

        boolean isProcessorCompatibleWithMotherboardSocket = processorCompatibilityCheckService
                .checkCompatibilityWithMotherboardSocket(processor, motherboard);

        boolean isRamTypeCompatibleWithMotherboard = ramCompatibilityCheckService
                .checkRamTypeCompatibilityWithMotherboard(ram, motherboard);

        boolean isRamAmountCompatibleWithMotherboard = ramCompatibilityCheckService
                .checkRamAmountCompatibilityWithMotherboard(ram, motherboard);

        boolean isRamGbAmountCompatibleWithMotherboard = ramCompatibilityCheckService
                .checkRamGbAmountCompatibilityWithMotherboard(ram, motherboard);

        boolean isTdpValid = powerSupplyCompatibilityCheckService.checkTdp(powerSupply, processor, gpus);

        Map<String, String> powerSupplyCompatibilityWithGpuPower = powerSupplyCompatibilityCheckService
                .checkCompatibilityWithGpuPower(powerSupply, gpus);

        return new PcResponseDto(
                isPowerSupplyCompatibleWithMotherboardCpuPower,
                isPowerSupplyCompatibleWithMotherboardPower,
                isProcessorCompatibleWithMotherboardSocket,
                isRamTypeCompatibleWithMotherboard,
                isRamAmountCompatibleWithMotherboard,
                isRamGbAmountCompatibleWithMotherboard,
                isTdpValid,
                powerSupplyCompatibilityWithGpuPower
        );
    }

    private List<Gpu> getAllGpusById(List<BigInteger> gpuIds) {
        List<Gpu> gpus = new ArrayList<>(gpuIds.size());
        gpuIds.forEach(id -> {
            Gpu gpu = gpuEntityRepository
                    .findById(id)
                    .orElseThrow(GpuNotFoundException::new);
            gpus.add(gpu);
        });

        return gpus;
    }


}
