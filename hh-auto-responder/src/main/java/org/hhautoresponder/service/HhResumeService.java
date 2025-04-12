package org.hhautoresponder.service;

import org.hhautoresponder.dto.resume.ResumeDto;
import org.hhautoresponder.dto.resume.ResumeResponseDto;
import org.hhautoresponder.repository.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class HhResumeService {
    private final WebClient webClient;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    public HhResumeService(WebClient.Builder webClientBuilder, TokenProvider tokenProvider, UserRepository userRepository) {
        this.webClient = webClientBuilder.build();
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
    }

    public ResumeResponseDto getResumes (Long userId){
        var accessToken = tokenProvider.getValidAccessToken(userId);
        return webClient.get()
                .uri("https://api.hh.ru/resumes/mine")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(ResumeResponseDto.class)
                .block();
    }

    public List<ResumeDto> getResumesList (Long userId){
        var user =  userRepository.findByUserId(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        var resumes = user.getResumes();

        if (resumes != null && !resumes.isEmpty()) {
            return resumes.stream()
                    .map(resume -> ResumeDto.builder()
                            .resumeId(resume.getResumeId())
                            .title(resume.getTitle())
                            .id(resume.getId())
                            .build())
                    .toList();
        } else {
            throw new IllegalArgumentException("No resumes found for user");
        }
    }

    public ResumeDto getResumeById (Long userId, String id){
        List<ResumeDto> resumes = getResumes(userId).getItems();
        ResumeDto resumeDto = null;
        for (ResumeDto resume: resumes){
            if (id.equals(resume.getId())){
                resumeDto = resume;
            }
            else {
                throw new IllegalArgumentException("Resume not found");
            }
        }
        return resumeDto;
    }
}
