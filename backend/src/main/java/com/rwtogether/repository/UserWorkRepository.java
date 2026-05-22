package com.rwtogether.repository;

import com.rwtogether.entity.UserWork;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserWorkRepository extends JpaRepository<UserWork, Long> {

    List<UserWork> findByUserIdAndStatus(Long userId, UserWork.Status status);

    List<UserWork> findByUserId(Long userId);

    List<UserWork> findByWorkId(Long workId);

    Optional<UserWork> findByUserIdAndWorkId(Long userId, Long workId);

    long countByUserIdAndStatus(Long userId, UserWork.Status status);
}
