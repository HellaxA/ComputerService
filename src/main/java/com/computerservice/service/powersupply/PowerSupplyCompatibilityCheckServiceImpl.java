package com.computerservice.service.powersupply;

import com.computerservice.entity.pc.gpu.Gpu;
import com.computerservice.entity.pc.motherboard.Motherboard;
import com.computerservice.entity.pc.powersupply.PowerSupply;
import com.computerservice.entity.pc.processor.Processor;
import com.computerservice.repository.gpu.GpuInterfaceEntityRepository;
import com.computerservice.repository.motherboard.MotherboardInterfaceEntityRepository;
import com.computerservice.repository.processor.ProcessorInterfaceEntityRepository;
import lombok.RequiredArgsConstructor;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PowerSupplyCompatibilityCheckServiceImpl implements PowerSupplyCompatibilityCheckService {

    public static final int APPROXIMATE_ADDITIONAL_TDP = 100;
    private static final String OK = "Ok";
    public static final String NO_AVAILABLE_GPU_ADDITIONAL_POWER = "No available gpu additional power.";
    public static final String NOT_ENOUGH_POWER_SUPPLY_PINS = "Not enough power supply pins.";
    private final MotherboardInterfaceEntityRepository motherboardInterfaceEntityRepository;
    private final ProcessorInterfaceEntityRepository processorInterfaceEntityRepository;
    private final GpuInterfaceEntityRepository gpuInterfaceEntityRepository;

    @Override
    public boolean checkCompatibilityWithMotherboardPower(PowerSupply powerSupply, Motherboard motherboard) {

        String motherboardPowerPin = motherboard.getPowerPin();
        String powerSupplyMotherboardPowerPin = powerSupply.getMotherboardPowerPin();

        return motherboardInterfaceEntityRepository
                .existsPowerSupplyWithSuchMotherboardInterface(
                        motherboardPowerPin,
                        powerSupplyMotherboardPowerPin);
    }

    @Override
    public boolean checkCompatibilityWithMotherboardCpuPower(PowerSupply powerSupply, Motherboard motherboard) {

        String motherboardCpuInterface = motherboard.getProcessorPowerPin();
        String powerSupplyCpuInterface = powerSupply.getProcessorPowerPin();

        return processorInterfaceEntityRepository
                .existsPowerSupplyWithSuchCpuInterface(
                        motherboardCpuInterface,
                        powerSupplyCpuInterface);
    }

    @Override
    public Map<String, String> checkCompatibilityWithGpuPower(PowerSupply powerSupply, List<Gpu> gpus) {
        String powerSupplyInterface = powerSupply.getGpuAddPowerPin();

        Expression expression = new ExpressionBuilder(powerSupply.getGpuAddPowerPin()).build();
        double maxPinNumber = expression.evaluate();
        double pinNumber = 0;

        Map<String, String> resultMap = new HashMap<>();

        for (Gpu gpu : gpus) {
            boolean exists = gpuInterfaceEntityRepository
                    .existsPowerSupplyWithSuchGpuInterface(
                            gpu.getAddPower(),
                            powerSupplyInterface
                    );
            Expression tempExpression = new ExpressionBuilder(gpu.getAddPower()).build();
            pinNumber += tempExpression.evaluate();

            if (pinNumber > maxPinNumber) {
                resultMap.put(gpu.getName(), NOT_ENOUGH_POWER_SUPPLY_PINS);
            } else if (exists) {
                resultMap.put(gpu.getName(), OK);
            } else {
                resultMap.put(gpu.getName(), NO_AVAILABLE_GPU_ADDITIONAL_POWER);
            }
        }

        return resultMap;
    }

    @Override
    public boolean checkTdp(PowerSupply powerSupply, Processor processor, List<Gpu> gpus) {
        int cpuTdp = processor.getTdp();
        int gpuTdp = gpus.stream().mapToInt(Gpu::getTdp).sum();

        return powerSupply.getPower() >= cpuTdp + gpuTdp + APPROXIMATE_ADDITIONAL_TDP;
    }

}
