package com.computerservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Pc Component is not found")
public class PcComponentNotFoundException extends RuntimeException {
}
