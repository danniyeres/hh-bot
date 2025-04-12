package org.hhautoresponder.dto.vacancy;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VacancyDto {

    @JsonProperty ("vacancy_id")
    private Long vacancyId;
    @JsonProperty ("id")
    private String id;
    @JsonProperty ("name")
    private String name;
}
