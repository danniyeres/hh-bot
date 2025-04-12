package org.hhautoresponder.controller;

import lombok.RequiredArgsConstructor;
import org.hhautoresponder.service.HhApi;
import org.hhautoresponder.service.HhAuthService;
import org.hhautoresponder.service.HhResumeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class HhApiController {
    private final HhApi hhApi;


    @GetMapping("/me")
    public String getUserInfo(@RequestParam String accessToken) {
        return hhApi.getUserInfo(accessToken);
    }

    @GetMapping("/mine")
    public String getApiResumes (@RequestParam Long userId){
        return hhApi.getApiResumes(userId);
    }

    @GetMapping("/api/get")
    public String getVacancies(@RequestParam Long userId,@RequestParam String searchText) {
         return hhApi.getApiVacancies(userId, searchText);
    }
}
