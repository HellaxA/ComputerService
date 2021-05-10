package com.computerservice.exception.ram;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Ram is not found")
public class RamNotFoundException extends RuntimeException {
    public RamNotFoundException() {
        super("Ram is not found");
    }
}
