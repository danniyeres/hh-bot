package org.hhautoresponder.repository;

import org.hhautoresponder.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Long> {
}
