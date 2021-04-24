package com.computerservice.entity.authentication;

import lombok.Data;

@Data
public class StatusResponse {
    private String status;

    public StatusResponse(String status) {
        this.status = status;
    }

}
