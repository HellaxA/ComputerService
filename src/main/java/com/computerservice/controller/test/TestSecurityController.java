package com.computerservice.controller.test;

import com.computerservice.repository.gpu.GpuEntityRepository;
import com.computerservice.repository.processor.SupportedCpuEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class TestSecurityController {
    private final SupportedCpuEntityRepository supportedCpuEntityRepository;

    @GetMapping("/admin/get")
    public String getAdmin() {
        System.out.println();
        return "Hi admin";
    }

    @GetMapping("/user/get")
    public String getUser() {
        return "Hi user";
    }

}
