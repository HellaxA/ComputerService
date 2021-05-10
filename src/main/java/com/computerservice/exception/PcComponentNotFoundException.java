package com.computerservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "PC component is not found")
public class PcComponentNotFoundException extends RuntimeException {
    public PcComponentNotFoundException() {
        super("PC component is not found");
    }
}
