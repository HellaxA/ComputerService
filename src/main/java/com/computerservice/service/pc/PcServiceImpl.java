package com.computerservice.service.pc;

import com.computerservice.entity.pc.gpu.Gpu;
import com.computerservice.entity.pc.motherboard.Motherboard;
import com.computerservice.entity.pc.pc.*;
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
import com.computerservice.service.powersupply.PowerSupplyCompatibilityCheckService;
import com.computerservice.service.processor.ProcessorCompatibilityCheckService;
import com.computerservice.service.ram.RamCompatibilityCheckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.computerservice.service.powersupply.PowerSupplyCompatibilityCheckServiceImpl.APPROXIMATE_ADDITIONAL_TDP;

@Service
@RequiredArgsConstructor
@Log
public class PcServiceImpl implements PcService {

    private final PowerSupplyEntityRepository powerSupplyEntityRepository;
    private final MotherboardEntityRepository motherboardEntityRepository;
    private final GpuEntityRepository gpuEntityRepository;
    private final RamEntityRepository ramEntityRepository;
    private final ProcessorEntityRepository processorEntityRepository;
    private final PowerSupplyCompatibilityCheckService powerSupplyCompatibilityCheckService;
    private final ProcessorCompatibilityCheckService processorCompatibilityCheckService;
    private final RamCompatibilityCheckService ramCompatibilityCheckService;

    public PcCompatibilityCheckResponseDto checkPcCompatibility(PcRequestDto pcRequestDto) {
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

        Map<String, String> powerSupplyCompatibilityWithGpuPower = Collections.emptyMap();
        if (!gpus.isEmpty()) {
            powerSupplyCompatibilityWithGpuPower = powerSupplyCompatibilityCheckService
                    .checkCompatibilityWithGpuPower(powerSupply, gpus);
        }

        PcCompatibilityCheckResponseDto pcCheck = new PcCompatibilityCheckResponseDto(
                isPowerSupplyCompatibleWithMotherboardCpuPower,
                isPowerSupplyCompatibleWithMotherboardPower,
                isProcessorCompatibleWithMotherboardSocket,
                isRamTypeCompatibleWithMotherboard,
                isRamAmountCompatibleWithMotherboard,
                isRamGbAmountCompatibleWithMotherboard,
                isTdpValid,
                powerSupplyCompatibilityWithGpuPower
        );
        logPcCheckResponseDto(pcCheck);

        return pcCheck;
    }

    @Override
    public PcCompListDto fixComputerAssembly(PcReqAndResDto pcReqAndResDto) {
        PcCompatibilityCheckResponseDto pcCompatibilityCheckResponseDto =
                pcReqAndResDto.getPcCompatibilityCheckResponseDto();
        Pc pc = pcReqAndResDto.getPc();

        Processor pcu = pc.getProcessor();
        Motherboard mb = pc.getMotherboard();
        Ram ram = pc.getRam();
        PowerSupply ps = pc.getPowerSupply();
        List<Gpu> gpus = pc.getGpus();

        PcCompListDto pcCompListDto = new PcCompListDto();

        motherboardAndCpuSocketFix(pcCompatibilityCheckResponseDto, pcu, mb, pcCompListDto);
        motherboardAndRamFix(pcCompatibilityCheckResponseDto, mb, ram, pcCompListDto);
        powerSupplyFix(pcCompatibilityCheckResponseDto, pcu, mb, gpus, pcCompListDto);

        pcCompListDto.setPcCompatibilityCheckResponseDto(pcCompatibilityCheckResponseDto);

        printAnswer(pcCompListDto, gpus, ps, ram, pcu, mb);

        return pcCompListDto;
    }

    private void powerSupplyFix(PcCompatibilityCheckResponseDto pcCompatibilityCheckResponseDto, Processor pcu, Motherboard mb, List<Gpu> gpus, PcCompListDto pcCompListDto) {

        Map<String, String> gpuResponse = pcCompatibilityCheckResponseDto.getPowerSupplyCompatibilityWithGpuPower();
        boolean areGpusValid = PcServiceUtils.areGpusValid(gpuResponse);

        if (!pcCompatibilityCheckResponseDto.isTdpValid() ||
                !pcCompatibilityCheckResponseDto.isPowerSupplyCompatibleWithMotherboardCpuPower() ||
                !pcCompatibilityCheckResponseDto.isPowerSupplyCompatibleWithMotherboardPower() ||
                !areGpusValid
        ) {
            List<PowerSupply> powerSupplies = getCompatiblePowerSupplies(pcu, mb, gpus);
            if (!powerSupplies.isEmpty()) {
                for (Map.Entry<String, String> pair : gpuResponse.entrySet()) {
                    if (!pair.getValue().equals("Ok")) {
                        pair.setValue("Ok");
                    }
                }
                pcCompatibilityCheckResponseDto.setPowerSupplyCompatibleWithMotherboardCpuPower(true);
                pcCompatibilityCheckResponseDto.setPowerSupplyCompatibleWithMotherboardPower(true);
                pcCompatibilityCheckResponseDto.setTdpValid(true);
                pcCompListDto.setPowerSupplies(powerSupplies);
            }

        }
    }

    private void motherboardAndRamFix(PcCompatibilityCheckResponseDto pcCompatibilityCheckResponseDto, Motherboard mb, Ram ram, PcCompListDto pcCompListDto) {
        if (!pcCompatibilityCheckResponseDto.isRamAmountCompatibleWithMotherboard() ||
                !pcCompatibilityCheckResponseDto.isRamGbAmountCompatibleWithMotherboard() ||
                !pcCompatibilityCheckResponseDto.isRamTypeCompatibleWithMotherboard()
        ) {
            List<Ram> rams = getCompatibleRams(mb, ram);
            if (!rams.isEmpty()) {
                pcCompListDto.setRams(rams);
                pcCompatibilityCheckResponseDto.setRamAmountCompatibleWithMotherboard(true);
                pcCompatibilityCheckResponseDto.setRamGbAmountCompatibleWithMotherboard(true);
                pcCompatibilityCheckResponseDto.setRamTypeCompatibleWithMotherboard(true);
            }

        }
    }

    private void motherboardAndCpuSocketFix(PcCompatibilityCheckResponseDto pcCompatibilityCheckResponseDto, Processor pcu, Motherboard mb, PcCompListDto pcCompListDto) {
        if (!pcCompatibilityCheckResponseDto.isProcessorCompatibleWithMotherboardSocket()) {
            List<Processor> processors = getCompatibleProcessors(pcu, mb);
            if (!processors.isEmpty()) {
                pcCompListDto.setProcessors(processors);
                pcCompatibilityCheckResponseDto.setProcessorCompatibleWithMotherboardSocket(true);
            }
        }
    }

    private List<PowerSupply> getCompatiblePowerSupplies(Processor pcu, Motherboard mb, List<Gpu> gpus) {
        int cpuTdp = pcu.getTdp();
        int gpuTdp = gpus.stream().mapToInt(Gpu::getTdp).sum();
        int tdp = cpuTdp + gpuTdp + APPROXIMATE_ADDITIONAL_TDP;

        String gpuAddPowerPin1 = gpus.size() == 1 ? gpus.get(0).getAddPower() : "%";
        String gpuAddPowerPin2 = gpus.size() == 2 ? gpus.get(1).getAddPower() : "%";
        return powerSupplyEntityRepository.findCompatiblePowerSupply(
                gpuAddPowerPin1,
                gpuAddPowerPin2,
                mb.getPowerPin(),
                mb.getProcessorPowerPin(),
                tdp
        );
    }

    private List<Ram> getCompatibleRams(Motherboard mb, Ram ram) {

        return ramEntityRepository.findTop3ByCapacityLessThanEqualAndAmountLessThanEqualAndTypeAndPriceLessThanEqualOrderByAvgBenchDesc(
                mb.getMaxRam() / ram.getAmount(),
                mb.getNumRam(),
                mb.getRamType(),
                ram.getPrice()
        );
    }

    //TODO change query to select with OR   -findByIdIn
    private List<Gpu> getAllGpusById(List<BigInteger> gpuIds) {
        if (!gpuIds.isEmpty()) {
            List<Gpu> gpus = new ArrayList<>(gpuIds.size());
            gpuIds.forEach(id -> {
                Gpu gpu = gpuEntityRepository
                        .findById(id)
                        .orElseThrow(GpuNotFoundException::new);
                gpus.add(gpu);
            });
            return gpus;
        }
        return Collections.emptyList();

    }

    private List<Processor> getCompatibleProcessors(Processor pcu, Motherboard mb) {
        return processorEntityRepository.findTop3ByTdpLessThanEqualAndPriceLessThanEqualAndSocketOrderByCore8ptsDesc(
                pcu.getTdp(),
                pcu.getPrice(),
                mb.getSocket()
        );
    }

    //TODO test method
    private void printAnswer(PcCompListDto pcCompListDto, List<Gpu> gpus, PowerSupply ps, Ram ram, Processor processor, Motherboard motherboard) {
        log.info("pcCompListDto: " +
                "\ngpus: " + pcCompListDto.getGpus() +
                "\npower supplies: " + pcCompListDto.getPowerSupplies() +
                "\nmotherboards: " + pcCompListDto.getMotherboards() +
                "\nprocessors: " + pcCompListDto.getProcessors() +
                "\nrams: " + pcCompListDto.getRams()
        );
        PcRequestDto pcRequestDto = new PcRequestDto();

        if (pcCompListDto.getRams() == null) {
            pcRequestDto.setRamId(ram.getId());
        } else {
            pcRequestDto.setRamId(pcCompListDto.getRams().get(0).getId());
        }

        if (pcCompListDto.getProcessors() == null) {
            pcRequestDto.setProcessorId(processor.getId());
        } else {
            pcRequestDto.setProcessorId(pcCompListDto.getProcessors().get(0).getId());
        }

        if (pcCompListDto.getPowerSupplies() == null) {
            pcRequestDto.setPowerSupplyId(ps.getId());
        } else {
            pcRequestDto.setPowerSupplyId(pcCompListDto.getPowerSupplies().get(0).getId());
        }

        pcRequestDto.setMotherboardId(motherboard.getId());

        List<BigInteger> gpuIds = new ArrayList<>();
        if (gpus != null) {
            for (Gpu gpu : gpus) {
                gpuIds.add(gpu.getId());
            }
        }
        pcRequestDto.setGpuIds(gpuIds);

        checkPcCompatibility(pcRequestDto);
    }

    public void logPcCheckResponseDto(PcCompatibilityCheckResponseDto pcCheck) {
        log.info(
                "pcCompListDto check: " +
                        "\nPowerSupplyCompatibilityWithGpuPower: " + pcCheck.getPowerSupplyCompatibilityWithGpuPower() +
                        "\nisPowerSupplyCompatibleWithMotherboardPower: " + pcCheck.isPowerSupplyCompatibleWithMotherboardPower() +
                        "\nisPowerSupplyCompatibleWithMotherboardCpuPower: " + pcCheck.isPowerSupplyCompatibleWithMotherboardCpuPower() +
                        "\nisProcessorCompatibleWithMotherboardSocket: " + pcCheck.isProcessorCompatibleWithMotherboardSocket() +
                        "\nisRamGbAmountCompatibleWithMotherboard: " + pcCheck.isRamGbAmountCompatibleWithMotherboard() +
                        "\nisRamAmountCompatibleWithMotherboard: " + pcCheck.isRamAmountCompatibleWithMotherboard() +
                        "\nisRamTypeCompatibleWithMotherboard: " + pcCheck.isRamTypeCompatibleWithMotherboard() +
                        "\nisTdpValid: " + pcCheck.isTdpValid()
        );
    }
}
