package org.hhautoresponder.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.hhautoresponder.dto.resume.ResumeDto;
import org.hhautoresponder.dto.resume.ResumeResponseDto;
import org.hhautoresponder.dto.user.OAuthResponse;
import org.hhautoresponder.dto.user.TokenDto;
import org.hhautoresponder.dto.user.UserDto;
import org.hhautoresponder.mapper.UserMapper;
import org.hhautoresponder.model.Token;
import org.hhautoresponder.model.User;
import org.hhautoresponder.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class HhAuthService{

    @Value("${hh.client.id}")
    private String CLIENT_ID;

    @Value("${hh.client.secret}")
    private String CLIENT_SECRET;

    @Value("${hh.redirect.uri}")
    private String REDIRECT_URI;

    private final WebClient webClient;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final TokenRefreshService tokenRefreshService;

    public HhAuthService(WebClient.Builder webClientBuilder, UserRepository hhUserRepository, UserMapper userMapper, TokenRefreshService tokenRefreshService) {
        this.webClient = webClientBuilder.build();
        this.userRepository = hhUserRepository;
        this.userMapper = userMapper;
        this.tokenRefreshService = tokenRefreshService;
    }

    public UserDto getUser(Long userId) {
        var user = userRepository.findByUserId(userId);
        if (user == null) {
            return null;
        }
        return userMapper.toDto(user);
    }

    public UserDto getUserByTelegramId (String telegramId){
        var user = userRepository.findByTelegramId(telegramId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        log.info("User found by telegramId: {}", user.getTelegramId());
        return userMapper.toDto(user);
    }

    public UserDto getUserDtoInfo(String accessToken) {
        var userDto = webClient.get()
                .uri("https://api.hh.ru/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(UserDto.class)
                .doOnSuccess(response -> log.debug("Successfully obtained user info"))
                .doOnError(e -> log.error("Failed to obtain user info", e))
                .block();
        if (userDto == null) {
            log.error("Failed to obtain user info: response is null");
            throw new RuntimeException("Failed to obtain user info: response is null");
        }
        return userDto;
    }

    public ResumeResponseDto getUserResume (String accessToken){
        return webClient.get()
                .uri("https://api.hh.ru/resumes/mine")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(ResumeResponseDto.class)
                .doOnSuccess(response -> log.debug("Successfully obtained user resumes"))
                .doOnError(e -> log.error("Failed to obtain user resumes", e))
                .block();
    }

    @Transactional
    public OAuthResponse getAuthToken(String code, String state) {
        var oAuthResponse =  webClient.post()
                .uri("https://hh.ru/oauth/token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .bodyValue("grant_type=authorization_code" +
                        "&client_id=" + encodeValue(CLIENT_ID) +
                        "&client_secret=" + encodeValue(CLIENT_SECRET) +
                        "&redirect_uri=" + encodeValue(REDIRECT_URI) +
                        "&code=" + encodeValue(code))
                .retrieve()
                .bodyToMono(OAuthResponse.class)
                .doOnSuccess(response -> log.debug("Successfully obtained OAuth token"))
                .doOnError(e -> log.error("Failed to obtain OAuth token", e))
                .block();
        if (oAuthResponse == null) {
            log.error("Failed to obtain OAuth token: response is null");
            throw new RuntimeException("Failed to obtain OAuth token: response is null");
        }

        var userDto = getUserDtoInfo(oAuthResponse.getAccessToken());
        var tokenDto = TokenDto.builder()
                .accessToken(oAuthResponse.getAccessToken())
                .refreshToken(oAuthResponse.getRefreshToken())
                .expiresIn(oAuthResponse.getExpiresIn())
                .expiresAt(LocalDateTime.now().plusSeconds(oAuthResponse.getExpiresIn()))
                .build();

        List<ResumeDto> resumeResponseDto = getUserResume(oAuthResponse.getAccessToken()).getItems();

        if (state != null){
            String[] telegramData = state.split(":");
            userDto.setTelegramId(telegramData[0]);
            userDto.setChatId(telegramData[1]);
        }

        userDto.setToken(tokenDto);
        userDto.setResumes(resumeResponseDto);

        if (userRepository.existsByHhId(userDto.getHhId())){
            updateUserToken(userDto.getHhId(), tokenDto);
        } else {
            saveUserInfo(userDto);
        }

        return oAuthResponse;
    }

    @Transactional
    public OAuthResponse refreshToken(String refreshToken) {
        var request = tokenRefreshService.refreshToken(refreshToken);
        if (request == null) {
            log.error("Failed to refresh OAuth token: response is null");
            throw new RuntimeException("Failed to refresh OAuth token: response is null");
        }
        var user = userRepository.findByToken_RefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("User not found for refresh token"));
        var tokenDto = TokenDto.builder()
                .accessToken(request.getAccessToken())
                .refreshToken(request.getRefreshToken())
                .expiresIn(request.getExpiresIn())
                .expiresAt(LocalDateTime.now().plusSeconds(request.getExpiresIn()))
                .build();

        updateUserToken(user.getHhId(),tokenDto );
        return request;
    }

    @Transactional
    protected void saveUserInfo(UserDto userDto) {
        if (userDto.getToken() == null) {
            throw new IllegalArgumentException("Token cannot be null for new user");
        }

        var user = userMapper.toEntity(userDto);
        userRepository.save(user);
    }

    @Transactional
    protected void updateUserToken(String hhId, TokenDto tokenDto) {
        var user = userRepository.findByHhId(hhId).orElseThrow(() -> new RuntimeException("User not found"));
        var token = user.getToken();

        if (token == null) {
            token = new Token();
            user.setToken(token);
        }

        token.setAccessToken(tokenDto.getAccessToken());
        token.setRefreshToken(tokenDto.getRefreshToken());
        token.setExpiresIn(tokenDto.getExpiresIn());
        token.setExpiresAt(tokenDto.getExpiresAt());
        userRepository.save(user);
    }


    private String encodeValue(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}