package com.cd_u.catatdulu_users.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpHeaders;

import java.util.List;
import java.util.Map;

@Service
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
                    "http://api.brevo.com/v3/smtp/email",
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
}
