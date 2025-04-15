package org.hhautoresponder.controller;

import lombok.RequiredArgsConstructor;
import org.hhautoresponder.dto.user.OAuthResponse;
import org.hhautoresponder.dto.user.UserDto;
import org.hhautoresponder.service.HhAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth")
public class HhAuthController {
    private final HhAuthService hhAuthService;

    @GetMapping("/callback")
    public ResponseEntity<OAuthResponse> handleCallback(@RequestParam String code, @RequestParam(required = false) String state) {

        OAuthResponse response;

        if (state != null) {
            response = hhAuthService.getAuthToken(code, state);
        }else {
            response = hhAuthService.getAuthToken(code,null);
        }

        if (response != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/token")
    public ResponseEntity<OAuthResponse> getToken(@RequestParam String refreshToken) {
        var response = hhAuthService.refreshToken(refreshToken);
        if (response != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/user")
    public UserDto getUser(@RequestParam Long userId) {
        return hhAuthService.getUser(userId);
    }

    @GetMapping("/telegram_user/{telegramId}")
    public UserDto getUserByTelegramId(@PathVariable String telegramId) {
        return hhAuthService.getUserByTelegramId(telegramId);
    }
}