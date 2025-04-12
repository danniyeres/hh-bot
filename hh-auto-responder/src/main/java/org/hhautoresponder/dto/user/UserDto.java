package org.hhautoresponder.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hhautoresponder.dto.resume.ResumeDto;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    @JsonProperty ("user_id")
    private Long userId;

    @JsonProperty ("chat_id")
    private String chatId;
    @JsonProperty ("telegram_id")
    private String telegramId;

    @JsonProperty ("id")
    private String hhId;
    @JsonProperty ("first_name")
    private String firstName;
    @JsonProperty ("last_name")
    private String lastName;
    @JsonProperty ("email")
    private String email;
    @JsonProperty ("phone")
    private String phone;

    @JsonProperty("token")
    private TokenDto token;

    private List<ResumeDto> resumes = new ArrayList<>();

}
