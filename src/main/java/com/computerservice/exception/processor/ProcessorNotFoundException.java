package com.computerservice.exception.processor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Processor is not found")
public class ProcessorNotFoundException extends RuntimeException {
    public ProcessorNotFoundException() {
        super("Processor is not found");
    }
}
