package com.cd_u.catatdulu_users.service;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpHeaders;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailForwadService {

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    @Value("${brevo.email.from}")
    private String fromEmail;

    public void sendMail(String to, String subject, String body) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.set("api-key", brevoApiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> emailRequest = Map.of(
                    "sender", Map.of("email", fromEmail),
                    "to", List.of(Map.of("email", to)),
                    "subject", subject,
                    "htmlContent", body
            );

            ResponseEntity<String> response = restTemplate.exchange(
                    "https://api.brevo.com/v3/smtp/email",
                    HttpMethod.POST,
                    new HttpEntity<>(emailRequest, headers),
                    String.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Email sending failed: " + response.getBody());
            }
        } catch (Exception e) {
            System.out.println("Email error (but continuing): " + e.getMessage());
        }
    }

    public void sendEmailWithAttachment(String to, String subject, String body, byte[] attachment, String filename) throws MessagingException {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.set("api-key", brevoApiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            // encode attachment to base64 for brevo api
            String base64Attachment = Base64.getEncoder().encodeToString(attachment);

            Map<String, Object> attachmentMap = Map.of(
                    "content", base64Attachment,
                    "name", filename
            );

            Map<String, Object> emailRequest = Map.of(
                    "sender", Map.of("email", fromEmail),
                    "to", List.of(Map.of("email", to)),
                    "subject", subject,
                    "htmlContent", body,
                    "attachment", List.of(attachmentMap)
            );

            ResponseEntity<String> response = restTemplate.exchange(
                    "https://api.brevo.com/v3/smtp/email",
                    HttpMethod.POST,
                    new HttpEntity<>(emailRequest, headers),
                    String.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Email with attachmentsending failed: " + response.getBody());
            }

        } catch (Exception e) {
            System.out.println("Email with attachment error: " + e.getMessage());
            throw new RuntimeException("Failed to send email with attachment", e);
        }
    }


}
