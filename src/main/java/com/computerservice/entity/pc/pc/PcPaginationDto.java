package com.computerservice.entity.pc.pc;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PcPaginationDto {
    private List<Pc> pcs;
    private MyPage page;
}
