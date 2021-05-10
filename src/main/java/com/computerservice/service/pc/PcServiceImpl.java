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

import java.math.BigDecimal;
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

    public static final BigDecimal MAX_PRICE = new BigDecimal(100000);
    private final PowerSupplyEntityRepository powerSupplyEntityRepository;
    private final MotherboardEntityRepository motherboardEntityRepository;
    private final GpuEntityRepository gpuEntityRepository;
    private final RamEntityRepository ramEntityRepository;
    private final ProcessorEntityRepository processorEntityRepository;
    private final PowerSupplyCompatibilityCheckService powerSupplyCompatibilityCheckService;
    private final ProcessorCompatibilityCheckService processorCompatibilityCheckService;
    private final RamCompatibilityCheckService ramCompatibilityCheckService;

    @Override
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

        List<Gpu> gpus = gpuEntityRepository.findByIdIn(gpuIds);

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

    @Override
    public PcCompListDto proposeComponents(PcIdsWithMaxPriceDto pcIdsWithMaxPriceDto) {
        PcRequestDto pcRequestDto = pcIdsWithMaxPriceDto.getPcRequestDto();
        MaxPrices maxPrices = pcIdsWithMaxPriceDto.getMaxPrices();

        BigInteger mbId = pcRequestDto.getMotherboardId();
        BigInteger cpuId = pcRequestDto.getProcessorId();
        BigInteger ramId = pcRequestDto.getRamId();
        BigInteger psId = pcRequestDto.getPowerSupplyId();
        List<BigInteger> gpuIds = pcRequestDto.getGpuIds();

        BigDecimal mbMaxPrice = null;
        BigDecimal gpuMaxPrice = null;
        BigDecimal ramMaxPrice = null;
        BigDecimal powerSupplyMaxPrice = null;
        BigDecimal processorMaxPrice = null;

        Motherboard mb = null;
        Processor cpu = null;
        Ram ram = null;
        PowerSupply ps = null;
        List<Gpu> gpus = null;

        if (mbId != null) {
            mb = motherboardEntityRepository.findById(mbId).orElseThrow(MotherboardNotFoundException::new);
        } else {
            mbMaxPrice = maxPrices.getMotherboardMaxPrice();
        }

        if (cpuId != null) {
            cpu = processorEntityRepository.findById(cpuId).orElseThrow(ProcessorNotFoundException::new);

        } else {
            processorMaxPrice = maxPrices.getProcessorMaxPrice();
        }

        if (ramId != null) {
            ram = ramEntityRepository.findById(ramId).orElseThrow(RamNotFoundException::new);
        } else {
            ramMaxPrice = maxPrices.getRamMaxPrice();
        }

        if (psId != null) {
            ps = powerSupplyEntityRepository.findById(psId).orElseThrow(PowerSupplyNotFoundException::new);

        } else {
            powerSupplyMaxPrice = maxPrices.getPowerSupplyMaxPrice();
        }

        if (gpuIds != null && !gpuIds.isEmpty()) {
            gpus = gpuEntityRepository.findByIdIn(gpuIds);

        } else {
            gpuMaxPrice = maxPrices.getGpuMaxPrice();
        }


        mb = getMotherboardProposal(mbMaxPrice, mb, cpu, ram, ps, gpus);
        if (cpu == null) {
            cpu = getProcessorProposal(processorMaxPrice, mb);
        }

        if (gpus == null) {
            gpus = getGpusProposal(gpuMaxPrice, cpu, ps);
        }

        if (ps == null) {
            ps = getPowerSupplyProposal(powerSupplyMaxPrice, mb, cpu, gpus);
        }
        List<Ram> rams = ram == null ? getRamProposal(ramMaxPrice, mb) : List.of(ram);


        PcRequestDto proposedPc = new PcRequestDto();
        proposedPc.setPowerSupplyId(ps.getId());
        proposedPc.setMotherboardId(mb.getId());
        proposedPc.setProcessorId(cpu.getId());
        proposedPc.setRamId(rams.get(0).getId());
        proposedPc.setGpuIds(List.of(gpus.get(0).getId()));

        PcCompatibilityCheckResponseDto pcCompCheck = checkPcCompatibility(proposedPc);

        return new PcCompListDto(List.of(ps), List.of(mb), gpus, List.of(cpu), rams, pcCompCheck);
    }

    private PowerSupply getPowerSupplyProposal(BigDecimal powerSupplyMaxPrice, Motherboard mb, Processor cpu, List<Gpu> gpus) {
        List<PowerSupply> powerSupplies = getCompatiblePowerSupplies(cpu, mb, gpus, powerSupplyMaxPrice);
        if (!powerSupplies.isEmpty()) {
            return powerSupplies.get(0);
        } else {
            throw new PowerSupplyNotFoundException();
        }
    }

    private List<Gpu> getGpusProposal(BigDecimal gpuMaxPrice, Processor cpu, PowerSupply ps) {

        List<Gpu> gpus = new ArrayList<>();
        Gpu gpu;
        if (ps == null) {
            gpu = gpuEntityRepository
                    .findFirstByPriceLessThanEqualOrderByAvgBenchDesc(gpuMaxPrice)
                    .orElseThrow(GpuNotFoundException::new);
        } else {
            int cpuTdp = cpu.getTdp();
            gpu = gpuEntityRepository
                    .findCompatibleGpuWithPS(gpuMaxPrice, ps.getPower() - cpuTdp - APPROXIMATE_ADDITIONAL_TDP)
                    .orElseThrow(GpuNotFoundException::new);
        }
        gpus.add(gpu);


        return gpus;
    }

    private List<Ram> getRamProposal(BigDecimal ramMaxPrice, Motherboard mb) {
        List<Ram> rams = ramEntityRepository.findTop5RamsProposals(
                ramMaxPrice,
                mb.getRamType(),
                mb.getNumRam(),
                mb.getMaxRam()
        );
        if (rams == null || rams.isEmpty()) throw new RamNotFoundException();
        return rams;
    }

    private Processor getProcessorProposal(BigDecimal processorMaxPrice, Motherboard mb) {
        return processorEntityRepository
                .findFirstBySocketAndPriceLessThanEqualOrderByCore8ptsDesc(mb.getSocket(), processorMaxPrice)
                .orElseThrow(ProcessorNotFoundException::new);
    }

    private Motherboard getMotherboardProposal(BigDecimal mbMaxPrice, Motherboard mb, Processor cpu, Ram ram, PowerSupply ps, List<Gpu> gpus) {
        if (mb == null) {
            if (cpu == null && ram == null && gpus == null && ps == null) {
                mb = motherboardEntityRepository
                        .findFirstByPriceLessThanEqualOrderByPriceAsc(mbMaxPrice)
                        .orElseThrow(MotherboardNotFoundException::new);
            } else {
                String socket = "%";
                if (cpu != null) {
                    socket = cpu.getSocket();
                }

                int amount = 0;
                int minGbAmount = 0;
                if (ram != null) {
                    amount = ram.getAmount();
                    minGbAmount = ram.getAmount() * ram.getCapacity();
                }

                String motherboardPowerPin = "%";
                String motherboardCpuPowerPin = "%";
                if (ps != null) {
                    motherboardPowerPin = ps.getMotherboardPowerPin();
                    motherboardCpuPowerPin = ps.getProcessorPowerPin();
                }

                mb = motherboardEntityRepository
                        .findCompatibleMotherboardWithCpuRamPS(
                                socket,
                                amount,
                                minGbAmount,
                                motherboardPowerPin,
                                motherboardCpuPowerPin,
                                mbMaxPrice
                        )
                        .orElseThrow(MotherboardNotFoundException::new);
            }
        }
        return mb;
    }

    private void powerSupplyFix(PcCompatibilityCheckResponseDto pcCompatibilityCheckResponseDto,
                                Processor pcu, Motherboard mb, List<Gpu> gpus,
                                PcCompListDto pcCompListDto) {

        Map<String, String> gpuResponse = pcCompatibilityCheckResponseDto.getPowerSupplyCompatibilityWithGpuPower();
        boolean areGpusValid = PcServiceUtils.areGpusValid(gpuResponse);

        if (!pcCompatibilityCheckResponseDto.isTdpValid() ||
                !pcCompatibilityCheckResponseDto.isPowerSupplyCompatibleWithMotherboardCpuPower() ||
                !pcCompatibilityCheckResponseDto.isPowerSupplyCompatibleWithMotherboardPower() ||
                !areGpusValid
        ) {

            List<PowerSupply> powerSupplies = getCompatiblePowerSupplies(pcu, mb, gpus, MAX_PRICE);

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

    private List<PowerSupply> getCompatiblePowerSupplies(Processor pcu, Motherboard mb, List<Gpu> gpus, BigDecimal maxPrice) {
        int cpuTdp = pcu.getTdp();

        int gpuTdp = gpus != null ? gpus.stream().mapToInt(Gpu::getTdp).sum() : 0;
        int tdp = cpuTdp + gpuTdp + APPROXIMATE_ADDITIONAL_TDP;

        String gpuAddPowerPin1 = gpus != null && gpus.size() == 1 ? gpus.get(0).getAddPower() : "%";
        String gpuAddPowerPin2 = gpus != null && gpus.size() == 2 ? gpus.get(1).getAddPower() : "%";

        return powerSupplyEntityRepository.findCompatiblePowerSupply(
                gpuAddPowerPin1,
                gpuAddPowerPin2,
                mb.getPowerPin(),
                mb.getProcessorPowerPin(),
                tdp,
                maxPrice
        );
    }

    private List<Ram> getCompatibleRams(Motherboard mb, Ram ram) {

        return ramEntityRepository.findTop5ByCapacityLessThanEqualAndAmountLessThanEqualAndTypeAndPriceLessThanEqualOrderByAvgBenchDesc(
                mb.getMaxRam() / ram.getAmount(),
                mb.getNumRam(),
                mb.getRamType(),
                ram.getPrice()
        );
    }

    private List<Processor> getCompatibleProcessors(Processor pcu, Motherboard mb) {
        return processorEntityRepository.findTop5ByTdpLessThanEqualAndPriceLessThanEqualAndSocketOrderByCore8ptsDesc(
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
