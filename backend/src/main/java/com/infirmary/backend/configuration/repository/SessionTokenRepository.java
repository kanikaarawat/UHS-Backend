package com.infirmary.backend.configuration.repository;
import com.infirmary.backend.configuration.model.SessionToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionTokenRepository extends JpaRepository<SessionToken, Long> {
    long countByIsActiveTrue();
}
