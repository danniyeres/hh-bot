package org.hhautoresponder.dto.vacancy;

import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VacancyResponseDto {
    private List<VacancyDto> items;
}
