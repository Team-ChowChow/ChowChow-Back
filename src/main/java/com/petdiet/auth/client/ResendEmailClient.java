package com.petdiet.auth.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ResendEmailClient {

    private final WebClient webClient;

    @Value("${resend.api-key}")
    private String apiKey;

    public ResendEmailClient() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.resend.com")
                .build();
    }

    public void sendConfirmationEmail(String to, String confirmUrl) {
        Map<String, Object> body = Map.of(
                "from", "Pet Diet <no-reply@chawchaws.com>",
                "to", List.of(to),
                "subject", "[Pet Diet] 이메일 인증을 완료해주세요",
                "html", buildHtml(to, confirmUrl)
        );

        try {
            webClient.post()
                    .uri("/emails")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(body)
                    .retrieve()
                    .onStatus(status -> !status.is2xxSuccessful(), resp ->
                            resp.bodyToMono(String.class).map(errorBody -> {
                                log.error("Resend API 오류 [status={}, body={}]", resp.statusCode(), errorBody);
                                return new RuntimeException("Resend 오류: " + errorBody);
                            }))
                    .toBodilessEntity()
                    .block();
            log.info("인증 이메일 발송 완료: {}", to);
        } catch (Exception e) {
            log.error("인증 이메일 발송 실패 [to={}]: {}", to, e.getMessage());
            throw new RuntimeException("이메일 발송 중 오류가 발생했습니다.", e);
        }
    }

    private String buildHtml(String email, String confirmUrl) {
        return """
                <!DOCTYPE html>
                <html lang="ko">
                <head>
                  <meta charset="UTF-8">
                  <title>이메일 인증</title>
                  <style>
                    body { font-family: sans-serif; background: #f5f5f5; display: flex; justify-content: center; padding: 40px 0; margin: 0; }
                    .card { background: white; padding: 40px; border-radius: 12px; max-width: 480px; width: 100%%; box-shadow: 0 2px 12px rgba(0,0,0,0.08); }
                    h2 { color: #333; margin-bottom: 8px; }
                    p { color: #666; line-height: 1.6; }
                    a.btn { display: inline-block; background: #4f46e5; color: white; padding: 14px 28px;
                            border-radius: 8px; text-decoration: none; font-weight: 600; margin: 20px 0; }
                    .small { font-size: 12px; color: #999; margin-top: 20px; }
                  </style>
                </head>
                <body>
                  <div class="card">
                    <h2>이메일 인증 안내</h2>
                    <p><b>%s</b> 계정의 이메일 인증을 완료하려면 아래 버튼을 클릭해주세요.<br>링크는 24시간 동안 유효합니다.</p>
                    <a class="btn" href="%s">이메일 인증하기</a>
                    <p class="small">본인이 요청하지 않은 경우 이 이메일을 무시하셔도 됩니다.</p>
                  </div>
                </body>
                </html>
                """.formatted(email, confirmUrl);
    }
}
