package com.parsun.parsun_api.platform.auth.user.verification.application.ports.out;

public interface VerificationMailSender {

    void send(String recipientEmail, String recipientName, String verificationLink,
            String language);
}
