package org.hhautoresponder.service;

import lombok.extern.slf4j.Slf4j;
import org.hhautoresponder.dto.vacancy.VacancyDto;
import org.hhautoresponder.dto.vacancy.VacancyResponseDto;
import org.hhautoresponder.repository.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class HhVacancyService {
    private final WebClient webClient;
    private final TokenProvider tokenProvider;
    private final HhResumeService resumeService;
    private final UserRepository userRepository;

    public HhVacancyService(WebClient.Builder webClientBuilder, TokenProvider tokenProvider, HhResumeService resumeService, UserRepository userRepository) {
        this.webClient = webClientBuilder.build();
        this.tokenProvider = tokenProvider;
        this.resumeService = resumeService;
        this.userRepository = userRepository;
    }

    public VacancyResponseDto getVacancies(Long userId, String searchText, String area) {
        var accessToken = tokenProvider.getValidAccessToken(userId);

        var uri = UriComponentsBuilder
                .fromUriString("https://api.hh.ru/vacancies")
                .queryParam("text", encodeValue(searchText))
                .queryParam("per_page", 20)
                .queryParam("page", 0);

        if (area != null && !area.isBlank() && !area.equals("0")) {
            uri.queryParam("area", area);
        }
        return webClient.get()
                .uri(uri.build().toUri())
                .headers(headers -> headers.setBearerAuth(accessToken))
                .header(HttpHeaders.USER_AGENT, "HH-bot/1.0 (eleusizdaniyar777@gmail.com)")
                .retrieve()
                .bodyToMono(VacancyResponseDto.class)
                .block();
    }

    public void sendResponse(String telegramId, String searchText, String area, String message) {
        var user = userRepository.findByTelegramId(telegramId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        Long userId = user.getUserId();
        String finalArea = (area != null && !area.isBlank()) ? area : null;
        String finalMessage = (message != null && !message.isBlank()) ? message : null;

        sendResponse(userId, searchText, finalArea, finalMessage);
    }

    public void sendResponse(Long userId, String searchText, String area, String message) {
        var items = getVacancies(userId, searchText, area);

        if (items != null && items.getItems() != null) {
            int count = Math.min(items.getItems().size(), 30);
            String resumeId = resumeService.getResumes(userId).getItems().get(0).getId();

            for (int i = 0; i < count; i++) {
                VacancyDto item = items.getItems().get(i);
                var vacancyId = item.getId();

                if (resumeId != null && vacancyId != null) {
                    try {
                        if (message != null) {
                            sendResponseToVacancy(userId, vacancyId, resumeId, message);
                        }
                        sendResponseToVacancy(userId, vacancyId, resumeId, null);
                    } catch (Exception e) {
                        log.error("Failed to send response to vacancy: {} with resume: {}", vacancyId, resumeId);
                        continue;
                    }
                    log.info("Response sent to vacancy: {} with resume: {}", vacancyId, resumeId);
                } else {
                    log.error("Resume ID is null for user {}", userId);
                }
            }
            log.info("Responses sent to {} vacancies.", count);
        } else {
            log.error("No vacancies found or error occurred while fetching vacancies.");
        }
    }

    private void sendResponseToVacancy(Long userId, String vacancyId, String resumeId, String message) {
        var accessToken = tokenProvider.getValidAccessToken(userId);

        var bodyValues = "vacancy_id=" + encodeValue(vacancyId) + "&resume_id=" + encodeValue(resumeId) + "&message=" + encodeValue(message);
        if (message == null){
            bodyValues = "vacancy_id=" + encodeValue(vacancyId) + "&resume_id=" + encodeValue(resumeId);
        } else if (message.equals("null")) {
            bodyValues = "vacancy_id=" + encodeValue(vacancyId) + "&resume_id=" + encodeValue(resumeId);
        }

        try {
            webClient.post()
                    .uri("https://api.hh.ru/negotiations")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .header("HH-User-Agent", "HH-bot/1.0 (eleusizdaniyar777@gmail.com)")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .bodyValue(bodyValues)
                    .retrieve()
                    .onStatus(HttpStatus.BAD_REQUEST::equals, response -> response.bodyToMono(String.class)
                            .flatMap(body -> {
                                System.out.println("Ошибка 400: " + body);
                                return Mono.error(new RuntimeException("Bad Request: " + body));
                            }))
                    .onStatus(HttpStatus.FORBIDDEN::equals, response -> response.bodyToMono(String.class)
                            .flatMap(body -> {
                                System.out.println("Ошибка 403: " + body);
                                return Mono.error(new RuntimeException("Forbidden: " + body));
                            }))
                    .toEntity(String.class)
                    .doOnSuccess(response -> log.info("Successfully responded to vacancy {}", vacancyId))
                    .doOnError(e -> log.error("Failed to respond to vacancy {}", vacancyId, e))
                    .block();

            Thread.sleep(10000);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted", e);
        } catch (Exception e) {
            log.error("Failed to respond to vacancy: {}", e.getMessage());
            throw new RuntimeException("HH API Error: " + e.getMessage());
        }
    }

    private String encodeValue(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}