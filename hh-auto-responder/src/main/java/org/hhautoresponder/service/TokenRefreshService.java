package org.hhautoresponder.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.hhautoresponder.dto.user.OAuthResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class TokenRefreshService {

    @Value("${hh.client.id}")
    private String CLIENT_ID;

    @Value("${hh.client.secret}")
    private String CLIENT_SECRET;

    private final WebClient webClient;

    public TokenRefreshService(WebClient.Builder webClient) {
        this.webClient = webClient.build();
    }

    @Transactional
    public OAuthResponse refreshToken(String refreshToken) {
        var request = webClient.post()
                .uri("https://hh.ru/oauth/token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .bodyValue("grant_type=refresh_token" +
                        "&client_id=" + encodeValue(CLIENT_ID) +
                        "&client_secret=" + encodeValue(CLIENT_SECRET) +
                        "&refresh_token=" + encodeValue(refreshToken))
                .retrieve()
                .bodyToMono(OAuthResponse.class)
                .doOnSuccess(response -> log.debug("Successfully refreshed OAuth token"))
                .doOnError(e -> log.error("Failed to refresh OAuth token", e))
                .block();
        if (request == null) {
            log.error("Failed to refresh OAuth token: response is null");
            throw new RuntimeException("Failed to refresh OAuth token: response is null");
        }
        return request;
    }

    private String encodeValue(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
