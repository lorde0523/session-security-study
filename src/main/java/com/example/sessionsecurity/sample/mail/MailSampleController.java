package com.example.sessionsecurity.sample.mail;

import com.example.sessionsecurity.common.mail.MailSendRequest;
import com.example.sessionsecurity.common.mail.MailService;
import com.example.sessionsecurity.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sample/mail")
public class MailSampleController {

    private final MailService mailService;

    public MailSampleController(MailService mailService) {
        this.mailService = mailService;
    }

    @Operation(summary = "SMTP 메일 발송 샘플")
    @PostMapping
    public ApiResponse<Void> send(@Valid @RequestBody MailSendRequest request) {
        mailService.send(request);
        return ApiResponse.ok();
    }
}
