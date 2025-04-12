package org.hhautoresponder.dto.resume;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hhautoresponder.dto.user.UserDto;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResumeDto {

    @JsonProperty ("resume_id")
    private Long resumeId;
    @JsonProperty ("id")
    private String id;
    @JsonProperty ("title")
    private String title;

    @JsonProperty("user")
    @JsonIgnore
    private UserDto user;
}
