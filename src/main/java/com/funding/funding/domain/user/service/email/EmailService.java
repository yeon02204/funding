package com.funding.funding.domain.user.service.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class EmailService {

    @Value("${resend.api-key:}")
    private String resendApiKey;

    @Value("${resend.from:onboarding@resend.dev}")
    private String fromEmail;

    @Async
    public void sendVerificationCode(String to, String code) {
        String subject = "[Fundit] 이메일 인증 코드";
        String body = "<div style='font-family:Arial,sans-serif;max-width:480px;margin:0 auto'>"
                + "<h2>이메일 인증</h2>"
                + "<p>아래 인증 코드를 입력해 주세요. 코드는 <strong>5분</strong> 동안 유효합니다.</p>"
                + "<div style='background:#f5f5f5;padding:20px;text-align:center;border-radius:8px;margin:20px 0'>"
                + "<span style='font-size:32px;font-weight:bold;letter-spacing:8px;color:#2d3a4a'>" + code + "</span>"
                + "</div>"
                + "<p style='color:#999;font-size:12px'>본인이 요청하지 않은 경우 이 이메일을 무시하세요.</p>"
                + "</div>";
        sendHtml(to, subject, body);
    }

    @Async
    public void sendPasswordResetLink(String to, String token, String frontendUrl) {
        String resetLink = frontendUrl + "/reset-password?token=" + token;
        String subject = "[Fundit] 비밀번호 재설정";
        String body = "<div style='font-family:Arial,sans-serif;max-width:480px;margin:0 auto'>"
                + "<h2>비밀번호 재설정</h2>"
                + "<p>아래 버튼을 클릭해 비밀번호를 재설정하세요. 링크는 <strong>30분</strong> 동안 유효합니다.</p>"
                + "<div style='text-align:center;margin:30px 0'>"
                + "<a href='" + resetLink + "' style='background:#2d3a4a;color:#fff;padding:12px 32px;border-radius:6px;text-decoration:none;font-size:16px'>비밀번호 재설정</a>"
                + "</div>"
                + "<p style='color:#999;font-size:12px'>본인이 요청하지 않은 경우 이 이메일을 무시하세요.</p>"
                + "</div>";
        sendHtml(to, subject, body);
    }

    @Async
    public void sendFoundEmail(String to, String maskedEmail) {
        String subject = "[Fundit] 아이디 찾기 결과";
        String body = "<div style='font-family:Arial,sans-serif;max-width:480px;margin:0 auto'>"
                + "<h2>아이디 찾기</h2>"
                + "<p>회원님의 아이디(이메일)를 안내해 드립니다.</p>"
                + "<div style='background:#f5f5f5;padding:20px;text-align:center;border-radius:8px;margin:20px 0'>"
                + "<span style='font-size:20px;font-weight:bold;color:#2d3a4a'>" + maskedEmail + "</span>"
                + "</div>"
                + "<p style='color:#999;font-size:12px'>본인이 요청하지 않은 경우 이 이메일을 무시하세요.</p>"
                + "</div>";
        sendHtml(to, subject, body);
    }

    private void sendHtml(String to, String subject, String htmlBody) {
        try {
            String escapedHtml = htmlBody.replace("\\", "\\\\").replace("\"", "\\\"");
            String json = "{\"from\":\"" + fromEmail + "\",\"to\":[\"" + to + "\"],\"subject\":\"" + subject + "\",\"html\":\"" + escapedHtml + "\"}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.resend.com/emails"))
                    .header("Authorization", "Bearer " + resendApiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                System.err.println("[EmailService] 발송 실패: " + to + " / " + response.body());
            }
        } catch (Exception e) {
            System.err.println("[EmailService] 발송 실패: " + to + " / " + e.getMessage());
        }
    }
}