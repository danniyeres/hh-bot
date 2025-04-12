package org.hhautoresponder.mapper;

import org.hhautoresponder.dto.resume.ResumeDto;
import org.hhautoresponder.dto.user.TokenDto;
import org.hhautoresponder.dto.user.UserDto;
import org.hhautoresponder.model.Resume;
import org.hhautoresponder.model.Token;
import org.hhautoresponder.model.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        if (user == null || user.getToken() == null || user.getResumes() == null) {
            throw new IllegalArgumentException("User or Token cannot be null");
        }
        var userDto = UserDto.builder()
                .userId(user.getUserId())
                .chatId(user.getChatId())
                .telegramId(user.getTelegramId())
                .hhId(user.getHhId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build();

        var tokenDto = TokenDto.builder()
                .accessToken(user.getToken().getAccessToken())
                .refreshToken(user.getToken().getRefreshToken())
                .expiresIn(user.getToken().getExpiresIn())
                .expiresAt(user.getToken().getExpiresAt())
                .build();

        List<ResumeDto> resumeDtoList = user.getResumes().stream()
                .map(resume -> ResumeDto.builder()
                        .resumeId(resume.getResumeId())
                        .id(resume.getId())
                        .title(resume.getTitle())
                        .build())
                .toList();

        userDto.setToken(tokenDto);
        userDto.setResumes(resumeDtoList);

        return userDto;
    }

    public User toEntity(UserDto userDto) {
        if (userDto == null || userDto.getToken() == null || userDto.getResumes() == null) {
            throw new IllegalArgumentException("UserDto or TokenDto cannot be null");
        }
        var user = User.builder()
                .userId(userDto.getUserId())
                .chatId(userDto.getChatId())
                .telegramId(userDto.getTelegramId())
                .hhId(userDto.getHhId())
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .email(userDto.getEmail())
                .phone(userDto.getPhone())
                .build();

        var token = Token.builder()
                .accessToken(userDto.getToken().getAccessToken())
                .refreshToken(userDto.getToken().getRefreshToken())
                .expiresIn(userDto.getToken().getExpiresIn())
                .expiresAt(userDto.getToken().getExpiresAt())
                .user(user)
                .build();

        List<Resume> resumeList = userDto.getResumes().stream()
                .map(resumeDto -> Resume.builder()
                        .id(resumeDto.getId())
                        .title(resumeDto.getTitle())
                        .user(user)
                        .build())
                .toList();

        user.setToken(token);
        user.setResumes(resumeList);

        return user;
    }
}
