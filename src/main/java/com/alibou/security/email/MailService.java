package com.alibou.security.email;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class MailService {

    private final JavaMailSender mailSender;

    MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Value("${application.frontend.default-url}")
    private String defaultFrontendUrl;

    public void sendForgotMessage(String email, String token, String baseUrl) {

        var url = baseUrl != null ? baseUrl : defaultFrontendUrl;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@test.com");
        message.setTo(email);
        message.setSubject("reset you password");
        message.setText(String.format("Click <a href=\"%s/reset/%s\">here</a> to reset your password", url, token));

        mailSender.send(message);



    }
}
