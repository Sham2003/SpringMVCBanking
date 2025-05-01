package com.banking.repository;

import com.banking.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    @Query("SELECT t from Token t WHERE t.user.email = :username")
    List<Token> findAllUserValidTokens(String username);

    Optional<Token> findByToken(String token);
}