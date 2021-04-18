package com.computerservice.exception.powersupply;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Power supply is not found")
public class PowerSupplyNotFoundException extends RuntimeException {
}
