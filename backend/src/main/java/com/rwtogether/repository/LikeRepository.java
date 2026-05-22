package com.rwtogether.repository;

import com.rwtogether.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {

    boolean existsByUserIdAndCommentId(Long userId, Long commentId);

    long countByCommentId(Long commentId);

    Like findByUserIdAndCommentId(Long userId, Long commentId);
}
