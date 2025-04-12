package org.hhautoresponder.dto.resume;

import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResumeResponseDto {
    private List<ResumeDto> items;
}
