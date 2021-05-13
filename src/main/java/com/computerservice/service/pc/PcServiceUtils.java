package com.computerservice.service.pc;

import com.computerservice.entity.pc.pc.Pc;

import java.math.BigDecimal;

import java.util.Map;

public class PcServiceUtils {
    private PcServiceUtils() {
    }
    public static int getOffset(int page, int size) {
        return size * page;
    }

    public static boolean areGpusValid(Map<String, String> pcCheckGpuPower) {
        boolean areGpusValid = true;
        for (Map.Entry<String, String> pair : pcCheckGpuPower.entrySet()) {
            if (!pair.getValue().equals("Ok")) {
                areGpusValid = false;
                break;
            }
        }
        return areGpusValid;
    }

    public static void setOkToAllKeys(Map<String, String> gpuResponse) {
        for (Map.Entry<String, String> pair : gpuResponse.entrySet()) {
            if (!pair.getValue().equals("Ok")) {
                pair.setValue("Ok");
            }
        }
    }

    public static BigDecimal getPcPrice(Pc pc) {
        BigDecimal mbPrice = pc.getMotherboard() == null ? BigDecimal.ZERO : pc.getMotherboard().getPrice();
        BigDecimal psPrice = pc.getPowerSupply() == null ? BigDecimal.ZERO : pc.getPowerSupply().getPrice();
        BigDecimal ramPrice = pc.getRam() == null ? BigDecimal.ZERO : pc.getRam().getPrice();
        BigDecimal cpuPrice = pc.getProcessor() == null ? BigDecimal.ZERO : pc.getProcessor().getPrice();

        BigDecimal gpusPrice = BigDecimal.ZERO;
        if (pc.getGpus() != null) {
            for (int i = 0; i < pc.getGpus().size(); i++) {
                gpusPrice = gpusPrice.add(pc.getGpus().get(i).getPrice());
            }
        }

        return mbPrice.add(psPrice).add(ramPrice).add(cpuPrice).add(gpusPrice);
    }
}

