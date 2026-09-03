package com.mbfreire.employee_reporting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class EmailService {

    private final RestTemplate restTemplate;

    @Value("${brevo.api.url}")
    private String apiUrl;

    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${brevo.sender.email}")
    private String senderEmail;

    @Value("${brevo.sender.name}")
    private String senderName;

    public void sendPasswordResetEmail(String toEmail, String toName, String resetToken) {
        // Fictitious link, change later
        String resetLink = "http://localhost:5173/reset-password?token=" + resetToken;

        String htmlContent = String.format(
                "<html><body>" +
                        "<h2>Olá, %s.</h2>" +
                        "<p>Recebemos uma solicitação para redefinir a senha da sua conta no sistema da Ouvidoria Interna.</p>" +
                        "<p>Clique no link abaixo para criar uma nova senha:</p>" +
                        "<p><a href='%s'><b>Redefinir minha senha</b></a></p>" +
                        "<br><p>Se você não fez esta solicitação, apenas ignore este e-mail.</p>" +
                        "</body></html>",
                toName, resetLink
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", apiKey);
        headers.set("accept", "application/json");

        Map<String, Object> body = Map.of(
                "sender", Map.of("name", senderName, "email", senderEmail),
                "to", List.of(Map.of("email", toEmail, "name", toName)),
                "subject", "Redefinição de Senha - Ouvidoria Interna",
                "htmlContent", htmlContent
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        restTemplate.postForEntity(apiUrl, request, String.class);
    }
}
