package com.computerservice.controller.pc;

import com.computerservice.entity.pc.pc.PcRequestDto;
import com.computerservice.entity.pc.pc.PcResponseDto;
import com.computerservice.service.pc.PcCompatibilityCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/pc/compatibility")
public class PcCompatibilityCheckController {

    private final PcCompatibilityCheckService pcCompatibilityCheckService;

    @PostMapping("/check")
    public PcResponseDto checkForCompatibility(@RequestBody PcRequestDto pcRequestDto) {
        return pcCompatibilityCheckService.checkForCompatibility(pcRequestDto);
    }
}
