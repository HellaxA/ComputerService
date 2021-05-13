package com.computerservice.controller.pc;

import com.computerservice.entity.pc.pc.*;
import com.computerservice.repository.pc.PcEntityRepository;
import com.computerservice.service.pc.PcService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/pc")
public class PcController {

    private final PcService pcService;
    private final PcEntityRepository pcEntityRepository;

    @PostMapping("/compatibility/check")
    public PcCompatibilityCheckResponseDto checkPcCompatibility(@RequestBody PcRequestDto pcRequestDto) {
        return pcService.checkPcCompatibility(pcRequestDto);
    }

    @PostMapping("/compatibility/fix")
    public PcCompListDto fixComputerAssembly(@RequestBody PcReqAndResDto pcReqAndResDto) {
        return pcService.fixComputerAssembly(pcReqAndResDto);
    }

    @PostMapping("/compatibility/propose")
    public PcCompListDto proposeComponents(@RequestBody PcIdsWithMaxPriceDto pcIdsWithMaxPriceDto) {
        return pcService.proposeComponents(pcIdsWithMaxPriceDto);
    }

    @PostMapping("/compatibility/save")
    public ResponseEntity<Pc> savePc(@RequestBody PcRequestDto pcRequestDto) {

        return new ResponseEntity<>(pcService.savePc(pcRequestDto), HttpStatus.OK);
    }

    @GetMapping("/findAll")
    public PcPaginationDto findAllPaginated(@RequestParam("page") int page,
                                     @RequestParam("size") int size) {


        int totalElements = pcService.countAllPcsOfUser();
        MyPage myPage = new MyPage();
        myPage.setTotalElements(totalElements);
        List<Pc> pcs = pcService.findAllPaginated(page, size);

        return new PcPaginationDto(pcs, myPage);
    }

    @PostMapping("/{id}")
    public ResponseEntity<String> findAllPaginated(@PathVariable("id") BigInteger id) {
        pcService.removePc(id);

        return new ResponseEntity<>("PC with id=" + id + " was deleted." , HttpStatus.OK);
    }
}
