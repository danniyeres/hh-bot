package org.hhautoresponder.controller;

import lombok.RequiredArgsConstructor;
import org.hhautoresponder.dto.vacancy.VacancyResponseDto;
import org.hhautoresponder.service.HhVacancyService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vacancy")
@RequiredArgsConstructor
public class HhVacancyController {
     private final HhVacancyService hhVacancyService;

     @GetMapping("/get")
     public VacancyResponseDto getVacanciesDto(@RequestParam Long userId, @RequestParam String searchText, @RequestParam(required = false) String area) {
         if (area == null || area.isEmpty()) {
             return hhVacancyService.getVacancies(userId, searchText, null);
         }else {
             return hhVacancyService.getVacancies(userId, searchText, area);
         }
     }

     @PostMapping("/send_response")
     public String sendResponse(@RequestParam Long userId,
                                @RequestParam String searchText,
                                @RequestParam (required = false) String area,
                                @RequestParam (required = false) String message) {

         String finalArea = (area == null || area.isEmpty()) ? null : area;
         String finalMessage = (message == null || message.isEmpty()) ? null : message;

         hhVacancyService.sendResponse(userId, searchText, finalArea, finalMessage);
         return "Response sent";
     }


     @PostMapping("/response")
     public String response(@RequestParam String telegram_id,
                            @RequestParam String search_text,
                            @RequestParam (required = false) String area,
                            @RequestParam (required = false) String message) {

         String finalArea = (area == null || area.isEmpty()) ? null : area;
         String finalMessage = (message == null || message.isEmpty()) ? null : message;
         hhVacancyService.sendResponse(telegram_id, search_text, finalArea, finalMessage);
         return "Response sent";
     }
}