package org.hhautoresponder.repository;

import org.hhautoresponder.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByChatId(String chatId);
    Optional<User> findByHhId(String hhId);

    boolean existsByHhId(String hhId);
    boolean existsByChatId(String chatId);

    Optional<User> findByToken_RefreshToken(String refreshToken);
    User findByToken_AccessToken(String accessToken);

    User findByUserId(Long userId);

    User findByTelegramId(String telegramId);
}
