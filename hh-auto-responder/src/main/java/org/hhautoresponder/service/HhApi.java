package org.hhautoresponder.service;

import lombok.extern.slf4j.Slf4j;
import org.hhautoresponder.dto.vacancy.VacancyResponseDto;
import org.hhautoresponder.mapper.UserMapper;
import org.hhautoresponder.repository.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class HhApi {
    private final WebClient webClient;
    private final TokenProvider tokenProvider;

    public HhApi(WebClient.Builder webClient, TokenProvider tokenProvider) {
        this.webClient = webClient.build();
        this.tokenProvider = tokenProvider;
    }


    public String getUserInfo(String accessToken) {
        return webClient.get()
                .uri("https://api.hh.ru/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> log.debug("Successfully obtained user info"))
                .doOnError(e -> log.error("Failed to obtain user info", e))
                .block();
    }

    public String getApiResumes(Long userId){
        var accessToken = tokenProvider.getValidAccessToken(userId);
        return webClient.get()
                .uri("https://api.hh.ru/resumes/mine")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

        public String getApiVacancies(Long userId, String searchText) {
        var accessToken = tokenProvider.getValidAccessToken(userId);
        return webClient.get()
                .uri("https://api.hh.ru/vacancies?text=" + searchText)
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public VacancyResponseDto getVacancies(Long userId, String searchText) {
        var accessToken = tokenProvider.getValidAccessToken(userId);
        return webClient.get()
                .uri("https://api.hh.ru/vacancies?text=" + searchText+"&host=hh.kz")
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(VacancyResponseDto.class)
                .block();
    }
}
