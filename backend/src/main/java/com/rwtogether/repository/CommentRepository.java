package com.rwtogether.repository;

import com.rwtogether.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByWorkIdAndTypeOrderByCreatedAtDesc(Long workId, Comment.CommentType type);

    List<Comment> findByWorkIdAndEpisodeNumOrderByCreatedAtDesc(Long workId, Integer episodeNum);

    List<Comment> findByParentIdOrderByCreatedAtAsc(Long parentId);

    List<Comment> findByWorkIdOrderByCreatedAtDesc(Long workId);
}
