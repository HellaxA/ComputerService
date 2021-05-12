package com.computerservice.service.email;

public interface EmailService {
    void sendMessage(String email, String constructUrl, String subject);
}
