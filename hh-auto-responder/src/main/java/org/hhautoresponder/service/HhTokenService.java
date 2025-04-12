package org.hhautoresponder.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hhautoresponder.dto.user.OAuthResponse;
import org.hhautoresponder.model.Token;
import org.hhautoresponder.model.User;
import org.hhautoresponder.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class HhTokenService implements TokenProvider {
    private final HhAuthService hhAuthService;
    private final UserRepository userRepository;

    @Transactional
    public String getValidAccessToken(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Token token = user.getToken();
        if (token == null) {
            throw new RuntimeException("Token not found for user");
        }

        if (isTokenExpired(token)) {
            OAuthResponse newTokens = hhAuthService.refreshToken(token.getRefreshToken());
            updateToken(token, newTokens);
        }

        return token.getAccessToken();
    }

    private boolean isTokenExpired(Token token) {
        return token.getExpiresAt() == null || token.getExpiresAt().isBefore(LocalDateTime.now());
    }

    private void updateToken(Token token, OAuthResponse newTokens) {
        token.setAccessToken(newTokens.getAccessToken());
        token.setRefreshToken(newTokens.getRefreshToken());
        token.setExpiresIn(newTokens.getExpiresIn());
        token.setExpiresAt(LocalDateTime.now().plusSeconds(newTokens.getExpiresIn()));
        userRepository.save(token.getUser());
    }
}