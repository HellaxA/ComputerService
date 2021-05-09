package com.computerservice.controller.pc;

import com.computerservice.entity.pc.pc.*;
import com.computerservice.service.pc.PcService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/pc/compatibility")
public class PcController {

    private final PcService pcService;

    @PostMapping("/check")
    public PcCompatibilityCheckResponseDto checkPcCompatibility(@RequestBody PcRequestDto pcRequestDto) {
        return pcService.checkPcCompatibility(pcRequestDto);
    }

    @PostMapping("/fix")
    public PcCompListDto fixComputerAssembly(@RequestBody PcReqAndResDto pcReqAndResDto) {
        return pcService.fixComputerAssembly(pcReqAndResDto);
    }

    @PostMapping("/propose")
    public PcCompListDto proposeComponents(@RequestBody PcRequestDto pcRequestDto) {
        return pcService.proposeComponents(pcRequestDto);
    }


}
