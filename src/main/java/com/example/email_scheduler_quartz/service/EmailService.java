package com.example.email_scheduler_quartz.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@Slf4j
@AllArgsConstructor
public class EmailService {
    private JavaMailSender mailSender;

    private MailProperties mailProperties;

    public void send(String mailTo, String subject, String body)  {
        String mailFrom = mailProperties.getUsername();
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, StandardCharsets.UTF_8.toString());

            messageHelper.setFrom(mailFrom);
            messageHelper.setTo(mailTo);
            messageHelper.setSubject(subject);
            messageHelper.setText(body, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            log.error("Exception occurred when constructing email:", e);
        }
    }
}
