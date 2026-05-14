package com.example.sessionsecurity.common.mail;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record MailSendRequest(
        @Email @NotBlank String to,
        @NotBlank String subject,
        @NotBlank String text
) {
}
