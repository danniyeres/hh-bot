package org.hhautoresponder.controller;

import lombok.RequiredArgsConstructor;
import org.hhautoresponder.dto.resume.ResumeDto;
import org.hhautoresponder.dto.resume.ResumeResponseDto;
import org.hhautoresponder.service.HhResumeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/resume")
public class HhResumeController {
    private final HhResumeService resumeService;

    @GetMapping("/get")
    public ResumeResponseDto getResumes (@RequestParam Long userId) {
        return resumeService.getResumes(userId);
    }

    @GetMapping("/get_list")
    public List<ResumeDto> getResumesList (@RequestParam Long userId) {
        return resumeService.getResumesList(userId);
    }
}