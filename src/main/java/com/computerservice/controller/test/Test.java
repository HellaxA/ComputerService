package com.computerservice.controller.test;

import com.computerservice.dao.test.TestDao;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class Test {

    private final TestDao testDao;

    @GetMapping
    public List<String> getTest() {
        return testDao.getTestNames();
    }
}
