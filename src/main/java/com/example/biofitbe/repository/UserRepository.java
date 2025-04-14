package com.example.biofitbe.repository;

import com.example.biofitbe.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUserId(Long userId);
    Optional<User> deleteByEmail(String email);
    @Query("SELECT u.userId FROM User u") // Lấy ID thực thể (khóa chính)
    List<Long> findAllUserIds();
}