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
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

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
    public PcCompatibilityCheckResponseDto checkPcCompatibility(Pc pc) {
        Optional<PowerSupply> powerSupply = Optional.ofNullable(pc.getPowerSupply());
        Optional<Motherboard> motherboard = Optional.ofNullable(pc.getMotherboard());
        Optional<Ram> ram = Optional.ofNullable(pc.getRam());
        Optional<Processor> processor = Optional.ofNullable(pc.getProcessor());
        Optional<List<Gpu>> gpus = Optional.ofNullable(pc.getGpus());

        PcCompatibilityCheckResponseDto pcCheck = checkPcEntityCompatibility(powerSupply, motherboard, ram, processor, gpus);
        logPcCheckResponseDto(pcCheck);

        return pcCheck;
    }

    @Override
    public PcCompatibilityCheckResponseDto checkPcCompatibility(PcRequestDto pcRequestDto) {
        BigInteger powerSupplyId = pcRequestDto.getPowerSupplyId();
        BigInteger motherboardId = pcRequestDto.getMotherboardId();
        BigInteger ramId = pcRequestDto.getRamId();
        BigInteger processorId = pcRequestDto.getProcessorId();
        List<BigInteger> gpuIds = pcRequestDto.getGpuIds();

        Optional<PowerSupply> powerSupply = powerSupplyId == null ?
                Optional.empty() : powerSupplyEntityRepository.findById(powerSupplyId);
        Optional<Motherboard> motherboard = motherboardId == null ?
                Optional.empty() : motherboardEntityRepository.findById(motherboardId);
        Optional<Ram> ram = ramId == null ?
                Optional.empty() : ramEntityRepository.findById(ramId);
        Optional<Processor> processor = processorId == null ?
                Optional.empty() : processorEntityRepository.findById(processorId);
        Optional<List<Gpu>> gpus = gpuIds == null ?
                Optional.empty() : findAllGpusByIds(gpuIds);


        PcCompatibilityCheckResponseDto pcCheck = checkPcEntityCompatibility(powerSupply, motherboard, ram, processor, gpus);
        logPcCheckResponseDto(pcCheck);

        return pcCheck;
    }

    private PcCompatibilityCheckResponseDto checkPcEntityCompatibility(Optional<PowerSupply> powerSupply, Optional<Motherboard> motherboard, Optional<Ram> ram, Optional<Processor> processor, Optional<List<Gpu>> gpus) {
        boolean isPowerSupplyCompatibleWithMotherboardPower = true;
        boolean isPowerSupplyCompatibleWithMotherboardCpuPower = true;

        if (powerSupply.isPresent() && motherboard.isPresent()) {
            isPowerSupplyCompatibleWithMotherboardPower = powerSupplyCompatibilityCheckService
                    .checkCompatibilityWithMotherboardPower(powerSupply.get(), motherboard.get());

            isPowerSupplyCompatibleWithMotherboardCpuPower = powerSupplyCompatibilityCheckService
                    .checkCompatibilityWithMotherboardCpuPower(powerSupply.get(), motherboard.get());

        }

        boolean isRamTypeCompatibleWithMotherboard = true;
        boolean isRamAmountCompatibleWithMotherboard = true;
        boolean isRamGbAmountCompatibleWithMotherboard = true;

        if (ram.isPresent() && motherboard.isPresent()) {
            isRamTypeCompatibleWithMotherboard = ramCompatibilityCheckService
                    .checkRamTypeCompatibilityWithMotherboard(ram.get(), motherboard.get());

            isRamAmountCompatibleWithMotherboard = ramCompatibilityCheckService
                    .checkRamAmountCompatibilityWithMotherboard(ram.get(), motherboard.get());

            isRamGbAmountCompatibleWithMotherboard = ramCompatibilityCheckService
                    .checkRamGbAmountCompatibilityWithMotherboard(ram.get(), motherboard.get());
        }

        boolean isProcessorCompatibleWithMotherboardSocket =
                processor.isEmpty() || motherboard.isEmpty() ||
                        processorCompatibilityCheckService
                                .checkCompatibilityWithMotherboardSocket(processor.get(), motherboard.get());

        boolean isTdpValid = powerSupply.isEmpty() || processor.isEmpty() || gpus.isEmpty() ||
                powerSupplyCompatibilityCheckService
                        .checkTdp(powerSupply.get(), processor.get(), gpus.get());

        Map<String, String> powerSupplyCompatibilityWithGpuPower = Collections.emptyMap();
        if (gpus.isPresent() && powerSupply.isPresent()) {
            powerSupplyCompatibilityWithGpuPower = powerSupplyCompatibilityCheckService
                    .checkCompatibilityWithGpuPower(powerSupply.get(), gpus.get());
        }

        return new PcCompatibilityCheckResponseDto(
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

    private Optional<List<Gpu>> findAllGpusByIds(List<BigInteger> gpuIds) {
        List<Gpu> gpus = new ArrayList<>();
        for (BigInteger gpuId : gpuIds) {
            gpus.add(gpuEntityRepository.findById(gpuId).orElseThrow(GpuNotFoundException::new));
        }
        return gpus.isEmpty() ? Optional.empty() : Optional.of(gpus);
    }

    @Override
    public PcCompListDto fixComputerAssembly(PcReqAndResDto pcReqAndResDto) {
        PcCompatibilityCheckResponseDto pcCompCheckResponseDto =
                pcReqAndResDto.getPcCompatibilityCheckResponseDto();
        Pc pc = pcReqAndResDto.getPc();

        Processor cpu = pc.getProcessor();
        Motherboard mb = pc.getMotherboard();
        Ram ram = pc.getRam();
        PowerSupply ps = pc.getPowerSupply();
        List<Gpu> gpus = pc.getGpus();

        PcCompListDto initPcCompListDto = new PcCompListDto();

        motherboardAndCpuSocketFix(pcCompCheckResponseDto, mb, cpu, initPcCompListDto);
        motherboardAndRamFix(pcCompCheckResponseDto, mb, ram, initPcCompListDto);

        if (cpu == null && gpus == null) powerSupplyAndMotherboardFix(pcCompCheckResponseDto, mb, initPcCompListDto);
        else if (cpu == null && mb == null) powerSupplyAndGpusFix(pcCompCheckResponseDto, gpus, initPcCompListDto);
        else if (mb == null && gpus == null) powerSupplyAndCpuFix(pcCompCheckResponseDto, cpu, initPcCompListDto);
        else if (cpu == null) powerSupplyAndMbAndGpusFix(pcCompCheckResponseDto, mb, gpus, initPcCompListDto);
        else if (mb == null) powerSupplyAndCpuAndGpusFix(pcCompCheckResponseDto, cpu, gpus, initPcCompListDto);
        else if (gpus == null) powerSupplyAndCpuAndMbFix(pcCompCheckResponseDto, cpu, mb, initPcCompListDto);
        else powerSupplyFix(pcCompCheckResponseDto, cpu, mb, gpus, initPcCompListDto);

        initPcCompListDto.setPcCompatibilityCheckResponseDto(pcCompCheckResponseDto);
        printAnswer(initPcCompListDto, gpus, ps, ram, cpu, mb);

        return initPcCompListDto;
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
            gpus = findAllGpusByIds(gpuIds).orElse(Collections.emptyList());
        } else {
            gpuMaxPrice = maxPrices.getGpuMaxPrice();
        }

        Pc initPc = new Pc(ps, mb, gpus, cpu, ram);
        PcCompatibilityCheckResponseDto initPcCompCheck = checkPcCompatibility(initPc);

        boolean isCompatible = initPcCompCheck.isRamTypeCompatibleWithMotherboard() &&
                initPcCompCheck.isRamAmountCompatibleWithMotherboard() &&
                initPcCompCheck.isRamGbAmountCompatibleWithMotherboard() &&
                initPcCompCheck.isProcessorCompatibleWithMotherboardSocket() &&
                initPcCompCheck.isTdpValid() &&
                initPcCompCheck.isPowerSupplyCompatibleWithMotherboardPower() &&
                initPcCompCheck.isPowerSupplyCompatibleWithMotherboardCpuPower() &&
                PcServiceUtils.areGpusValid(initPcCompCheck.getPowerSupplyCompatibilityWithGpuPower());

        if (!isCompatible) {
            PcCompListDto pcCompListDto = new PcCompListDto();

            pcCompListDto.setGpus(gpus);
            if (ps != null) pcCompListDto.setPowerSupplies(List.of(ps));
            if (mb != null) pcCompListDto.setMotherboards(List.of(mb));
            if (cpu != null) pcCompListDto.setProcessors(List.of(cpu));
            if (ram != null) pcCompListDto.setRams(List.of(ram));
            pcCompListDto.setPcCompatibilityCheckResponseDto(initPcCompCheck);

            return pcCompListDto;
        }

        // motherboard proposals
        mb = getMotherboardProposal(mbMaxPrice, mb, cpu, ram, ps, gpus);

        // cpus proposals
        if (cpu == null) cpu = getProcessorProposal(processorMaxPrice, mb);

        // gpus proposals
        if (gpus == null) gpus = getGpusProposal(gpuMaxPrice, cpu, ps);

        // power supplies proposals
        if (ps == null) ps = getPowerSupplyProposal(powerSupplyMaxPrice, mb, cpu, gpus);

        // rams proposals
        List<Ram> rams = ram == null ? getRamProposal(ramMaxPrice, mb) : List.of(ram);


        // compatibility check
        Pc finalPc = new Pc(ps, mb, gpus, cpu, rams.get(0));
        PcCompatibilityCheckResponseDto pcCompCheck = checkPcCompatibility(finalPc);

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
                        .findFirstByPriceLessThanEqualOrderByM2Desc(mbMaxPrice)
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
    private void powerSupplyAndMotherboardFix(PcCompatibilityCheckResponseDto pcCompatibilityCheckResponseDto,
                                              Motherboard mb, PcCompListDto initPcCompListDto) {
        if (mb != null) {
            if (!pcCompatibilityCheckResponseDto.isPowerSupplyCompatibleWithMotherboardCpuPower() ||
                    !pcCompatibilityCheckResponseDto.isPowerSupplyCompatibleWithMotherboardPower()
            ) {
                List<PowerSupply> powerSupplies = powerSupplyEntityRepository
                        .findCompatiblePowerSupplyWithMotherboard(mb.getPowerPin(), mb.getProcessorPowerPin());

                if (!powerSupplies.isEmpty()) {
                    initPcCompListDto.setPowerSupplies(powerSupplies);
                    pcCompatibilityCheckResponseDto.setPowerSupplyCompatibleWithMotherboardCpuPower(true);
                    pcCompatibilityCheckResponseDto.setPowerSupplyCompatibleWithMotherboardPower(true);
                    pcCompatibilityCheckResponseDto.setTdpValid(true);
                }
            }
        } else {
            pcCompatibilityCheckResponseDto.setPowerSupplyCompatibleWithMotherboardCpuPower(true);
            pcCompatibilityCheckResponseDto.setPowerSupplyCompatibleWithMotherboardPower(true);
            pcCompatibilityCheckResponseDto.setTdpValid(true);
        }
    }

    private void powerSupplyAndCpuFix(PcCompatibilityCheckResponseDto pcCompatibilityCheckResponseDto,
                                      Processor cpu, PcCompListDto pcCompListDto) {
        if (cpu != null) {
            if (!pcCompatibilityCheckResponseDto.isTdpValid()) {
                List<PowerSupply> powerSupplies = powerSupplyEntityRepository.findTop5ByPowerIsGreaterThanEqualOrderByPowerDesc(cpu.getTdp());
                if (!powerSupplies.isEmpty()) {
                    pcCompListDto.setPowerSupplies(powerSupplies);
                    pcCompatibilityCheckResponseDto.setTdpValid(true);
                }

            }
        } else {
            pcCompatibilityCheckResponseDto.setTdpValid(true);
        }
    }

    private void powerSupplyAndGpusFix(PcCompatibilityCheckResponseDto pcCompatibilityCheckResponseDto,
                                       List<Gpu> gpus, PcCompListDto pcCompListDto) {
        if (gpus != null) {
            Map<String, String> gpuResponse = pcCompatibilityCheckResponseDto.getPowerSupplyCompatibilityWithGpuPower();
            boolean areGpusValid = PcServiceUtils.areGpusValid(gpuResponse);

            if (!areGpusValid) {
                List<PowerSupply> powerSupplies = getCompatiblePowerSuppliesWithGpus(gpus);
                if (!powerSupplies.isEmpty()) {
                    pcCompListDto.setPowerSupplies(powerSupplies);
                    PcServiceUtils.setOkToAllKeys(gpuResponse);
                    pcCompatibilityCheckResponseDto.setTdpValid(true);
                }
            }
        } else {
            pcCompatibilityCheckResponseDto.setTdpValid(true);
        }
    }

    private void powerSupplyAndMbAndGpusFix(PcCompatibilityCheckResponseDto pcCompatibilityCheckResponseDto,
                                            Motherboard mb, List<Gpu> gpus, PcCompListDto pcCompListDto) {
        if (mb != null && gpus != null) {
            Map<String, String> gpuResponse = pcCompatibilityCheckResponseDto.getPowerSupplyCompatibilityWithGpuPower();
            boolean areGpusValid = PcServiceUtils.areGpusValid(gpuResponse);

            if (!pcCompatibilityCheckResponseDto.isTdpValid() ||
                    !pcCompatibilityCheckResponseDto.isPowerSupplyCompatibleWithMotherboardCpuPower() ||
                    !pcCompatibilityCheckResponseDto.isPowerSupplyCompatibleWithMotherboardPower() ||
                    !areGpusValid
            ) {
                List<PowerSupply> powerSupplies = getCompatiblePowerSupplies(null, mb, gpus, MAX_PRICE);
                if (!powerSupplies.isEmpty()) {
                    pcCompListDto.setPowerSupplies(powerSupplies);
                    PcServiceUtils.setOkToAllKeys(gpuResponse);
                    pcCompatibilityCheckResponseDto.setPowerSupplyCompatibleWithMotherboardCpuPower(true);
                    pcCompatibilityCheckResponseDto.setPowerSupplyCompatibleWithMotherboardPower(true);
                    pcCompatibilityCheckResponseDto.setTdpValid(true);
                }

            }
        } else {
            pcCompatibilityCheckResponseDto.setPowerSupplyCompatibleWithMotherboardCpuPower(true);
            pcCompatibilityCheckResponseDto.setPowerSupplyCompatibleWithMotherboardPower(true);
            pcCompatibilityCheckResponseDto.setTdpValid(true);
        }
    }


    private void powerSupplyAndCpuAndGpusFix(PcCompatibilityCheckResponseDto pcCompatibilityCheckResponseDto,
                                             Processor cpu, List<Gpu> gpus, PcCompListDto pcCompListDto) {
        if (cpu != null && gpus != null) {
            Map<String, String> gpuResponse = pcCompatibilityCheckResponseDto.getPowerSupplyCompatibilityWithGpuPower();
            boolean areGpusValid = PcServiceUtils.areGpusValid(gpuResponse);

            if (!pcCompatibilityCheckResponseDto.isTdpValid() || !areGpusValid
            ) {
                List<PowerSupply> powerSupplies = getCompatiblePowerSupplies(cpu, null, gpus, MAX_PRICE);
                if (!powerSupplies.isEmpty()) {
                    pcCompListDto.setPowerSupplies(powerSupplies);
                    PcServiceUtils.setOkToAllKeys(gpuResponse);
                    pcCompatibilityCheckResponseDto.setTdpValid(true);
                }

            }
        } else {
            pcCompatibilityCheckResponseDto.setTdpValid(true);
        }
    }


    private void powerSupplyAndCpuAndMbFix(PcCompatibilityCheckResponseDto pcCompatibilityCheckResponseDto,
                                           Processor cpu, Motherboard mb, PcCompListDto pcCompListDto) {
        if (cpu != null && mb != null) {
            if (!pcCompatibilityCheckResponseDto.isTdpValid() ||
                    !pcCompatibilityCheckResponseDto.isPowerSupplyCompatibleWithMotherboardCpuPower() ||
                    !pcCompatibilityCheckResponseDto.isPowerSupplyCompatibleWithMotherboardPower()
            ) {
                List<PowerSupply> powerSupplies = getCompatiblePowerSupplies(cpu, mb, null, MAX_PRICE);
                if (!powerSupplies.isEmpty()) {
                    pcCompListDto.setPowerSupplies(powerSupplies);
                    pcCompatibilityCheckResponseDto.setPowerSupplyCompatibleWithMotherboardCpuPower(true);
                    pcCompatibilityCheckResponseDto.setPowerSupplyCompatibleWithMotherboardPower(true);
                    pcCompatibilityCheckResponseDto.setTdpValid(true);
                }

            }
        } else {
            pcCompatibilityCheckResponseDto.setPowerSupplyCompatibleWithMotherboardCpuPower(true);
            pcCompatibilityCheckResponseDto.setPowerSupplyCompatibleWithMotherboardPower(true);
            pcCompatibilityCheckResponseDto.setTdpValid(true);
        }
    }


    private void powerSupplyFix(PcCompatibilityCheckResponseDto pcCompatibilityCheckResponseDto,
                                Processor cpu, Motherboard mb, List<Gpu> gpus,
                                PcCompListDto pcCompListDto) {
        if (cpu != null && mb != null && gpus != null) {
            Map<String, String> gpuResponse = pcCompatibilityCheckResponseDto.getPowerSupplyCompatibilityWithGpuPower();
            boolean areGpusValid = PcServiceUtils.areGpusValid(gpuResponse);

            if (!pcCompatibilityCheckResponseDto.isTdpValid() ||
                    !pcCompatibilityCheckResponseDto.isPowerSupplyCompatibleWithMotherboardCpuPower() ||
                    !pcCompatibilityCheckResponseDto.isPowerSupplyCompatibleWithMotherboardPower() ||
                    !areGpusValid
            ) {
                List<PowerSupply> powerSupplies = getCompatiblePowerSupplies(cpu, mb, gpus, MAX_PRICE);
                if (!powerSupplies.isEmpty()) {
                pcCompListDto.setPowerSupplies(powerSupplies);
                    PcServiceUtils.setOkToAllKeys(gpuResponse);
                    pcCompatibilityCheckResponseDto.setPowerSupplyCompatibleWithMotherboardCpuPower(true);
                    pcCompatibilityCheckResponseDto.setPowerSupplyCompatibleWithMotherboardPower(true);
                    pcCompatibilityCheckResponseDto.setTdpValid(true);
                }

            }
        } else {
            pcCompatibilityCheckResponseDto.setPowerSupplyCompatibleWithMotherboardCpuPower(true);
            pcCompatibilityCheckResponseDto.setPowerSupplyCompatibleWithMotherboardPower(true);
            pcCompatibilityCheckResponseDto.setTdpValid(true);
        }
    }

    private void motherboardAndRamFix(PcCompatibilityCheckResponseDto pcCompatibilityCheckResponseDto, Motherboard mb, Ram ram, PcCompListDto pcCompListDto) {
        if (mb != null && ram != null) {
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
            } else {
                pcCompatibilityCheckResponseDto.setRamAmountCompatibleWithMotherboard(true);
                pcCompatibilityCheckResponseDto.setRamGbAmountCompatibleWithMotherboard(true);
                pcCompatibilityCheckResponseDto.setRamTypeCompatibleWithMotherboard(true);
            }
        }
    }


    private void motherboardAndCpuSocketFix(PcCompatibilityCheckResponseDto pcCompatibilityCheckResponseDto, Motherboard mb, Processor cpu, PcCompListDto pcCompListDto) {
        if (mb != null && cpu != null) {
            if (!pcCompatibilityCheckResponseDto.isProcessorCompatibleWithMotherboardSocket()) {
                List<Processor> processors = getCompatibleProcessors(cpu, mb);
                if (!processors.isEmpty()) {
                    pcCompListDto.setProcessors(processors);
                    pcCompatibilityCheckResponseDto.setProcessorCompatibleWithMotherboardSocket(true);
                }
            }
        } else {
            pcCompatibilityCheckResponseDto.setProcessorCompatibleWithMotherboardSocket(true);
        }

    }


    private List<PowerSupply> getCompatiblePowerSuppliesWithGpus(List<Gpu> gpus) {
        int gpuTdp = gpus != null ? gpus.stream().mapToInt(Gpu::getTdp).sum() : 0;
        int tdp = gpuTdp + APPROXIMATE_ADDITIONAL_TDP;

        String gpuAddPowerPin1 = gpus != null && gpus.size() == 1 ? gpus.get(0).getAddPower() : "%";
        String gpuAddPowerPin2 = gpus != null && gpus.size() == 2 ? gpus.get(1).getAddPower() : "%";

        return powerSupplyEntityRepository.findCompatiblePowerSupplyWithGpus(
                gpuAddPowerPin1,
                gpuAddPowerPin2,
                tdp
        );
    }

    private List<PowerSupply> getCompatiblePowerSupplies(@Nullable Processor pcu, @Nullable Motherboard mb, List<Gpu> gpus, BigDecimal maxPrice) {
        int cpuTdp = pcu != null ? pcu.getTdp() : 0;

        int gpuTdp = gpus != null ? gpus.stream().mapToInt(Gpu::getTdp).sum() : 0;
        int tdp = cpuTdp + gpuTdp + APPROXIMATE_ADDITIONAL_TDP;

        String mbPowerPin = mb != null ? mb.getPowerPin() : "%";
        String mbCpuPowerPin = mb != null ? mb.getProcessorPowerPin() : "%";

        String gpuAddPowerPin1 = gpus != null && gpus.size() == 1 ? gpus.get(0).getAddPower() : "%";
        String gpuAddPowerPin2 = gpus != null && gpus.size() == 2 ? gpus.get(1).getAddPower() : "%";

        return powerSupplyEntityRepository.findCompatiblePowerSupply(
                gpuAddPowerPin1,
                gpuAddPowerPin2,
                mbPowerPin,
                mbCpuPowerPin,
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

        Pc pc = new Pc();

        if (pcCompListDto.getRams() == null && ram != null) {
            pc.setRam(ram);
        } else if (pcCompListDto.getRams() != null && !pcCompListDto.getRams().isEmpty()) {
            pc.setRam(pcCompListDto.getRams().get(0));
        }

        if (pcCompListDto.getProcessors() == null && processor != null) {
            pc.setProcessor(processor);
        } else if (pcCompListDto.getProcessors() != null && !pcCompListDto.getProcessors().isEmpty()) {
            pc.setProcessor(pcCompListDto.getProcessors().get(0));
        }

        if (pcCompListDto.getPowerSupplies() == null && ps != null) {
            pc.setPowerSupply(ps);
        } else if (pcCompListDto.getPowerSupplies() != null && !pcCompListDto.getPowerSupplies().isEmpty()) {
            pc.setPowerSupply(pcCompListDto.getPowerSupplies().get(0));
        }

        if (motherboard != null) {
            pc.setMotherboard(motherboard);
        }

        if (gpus != null) {
            pc.setGpus(gpus);
        }

        checkPcCompatibility(pc);
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