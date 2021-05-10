package com.computerservice.exception.gpu;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Gpu is not found")
public class GpuNotFoundException extends RuntimeException {
    public GpuNotFoundException() {
        super("Gpu is not found");
    }
}
