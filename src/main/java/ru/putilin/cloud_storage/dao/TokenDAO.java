package ru.putilin.cloud_storage.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.putilin.cloud_storage.entity.JWTToken;

import java.util.Optional;

@Repository
public interface TokenDAO extends JpaRepository<JWTToken, Long> {

    Optional<JWTToken> findByAuthToken(String name);

    @Modifying
    @Query("update JWTToken a set a.authToken = :jwt where a.user.id = :userId")
    void update(String jwt, Long userId);

    void deleteJWTTokenByAuthToken(String jwt);

    boolean existsJWTTokenByUserId(Long userId);



}
