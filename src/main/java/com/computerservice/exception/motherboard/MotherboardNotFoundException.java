package com.computerservice.exception.motherboard;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Motherboard is not found")
public class MotherboardNotFoundException extends RuntimeException {
    public MotherboardNotFoundException() {
        super("Motherboard is not found");
    }
}
