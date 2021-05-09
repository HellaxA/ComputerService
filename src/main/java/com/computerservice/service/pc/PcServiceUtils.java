package com.computerservice.service.pc;

import java.util.Map;

public class PcServiceUtils {
    private PcServiceUtils() {
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
}
