package com.rwtogether.service;

import com.rwtogether.dto.CommentDto;
import com.rwtogether.entity.Comment;
import com.rwtogether.entity.Like;
import com.rwtogether.entity.User;
import com.rwtogether.entity.Work;
import com.rwtogether.exception.ResourceNotFoundException;
import com.rwtogether.repository.CommentRepository;
import com.rwtogether.repository.LikeRepository;
import com.rwtogether.repository.UserRepository;
import com.rwtogether.repository.WorkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final WorkRepository workRepository;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Transactional(readOnly = true)
    public List<CommentDto> getWorkComments(Long workId, Long userId) {
        List<Comment> comments = commentRepository.findByWorkIdOrderByCreatedAtDesc(workId);
        return comments.stream()
                .filter(c -> c.getParent() == null)
                .map(c -> toDto(c, userId))
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentDto addComment(Long userId, Long workId, String content, String type, Integer episodeNum, Integer seasonNum) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
        Work work = workRepository.findById(workId)
                .orElseThrow(() -> new ResourceNotFoundException("作品不存在"));

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setWork(work);
        comment.setContent(content);
        comment.setType(Comment.CommentType.valueOf(type != null ? type : "REVIEW"));
        comment.setEpisodeNum(episodeNum);
        comment.setSeasonNum(seasonNum);

        commentRepository.save(comment);
        return toDto(comment, userId);
    }

    @Transactional
    public CommentDto replyComment(Long userId, Long commentId, String content) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
        Comment parent = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("评论不存在"));

        Comment reply = new Comment();
        reply.setUser(user);
        reply.setWork(parent.getWork());
        reply.setContent(content);
        reply.setType(Comment.CommentType.REVIEW);
        reply.setParent(parent);

        commentRepository.save(reply);
        return toDto(reply, userId);
    }

    @Transactional
    public boolean toggleLike(Long userId, Long commentId) {
        if (likeRepository.existsByUserIdAndCommentId(userId, commentId)) {
            Like like = likeRepository.findByUserIdAndCommentId(userId, commentId);
            likeRepository.delete(like);
            return false;
        } else {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new ResourceNotFoundException("评论不存在"));

            Like like = new Like();
            like.setUser(user);
            like.setComment(comment);
            likeRepository.save(like);
            return true;
        }
    }

    private CommentDto toDto(Comment comment, Long userId) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setUserId(comment.getUser().getId());
        dto.setUsername(comment.getUser().getUsername());
        dto.setAvatar(comment.getUser().getAvatar());
        dto.setContent(comment.getContent());
        dto.setType(comment.getType().name());
        dto.setEpisodeNum(comment.getEpisodeNum());
        dto.setSeasonNum(comment.getSeasonNum());
        dto.setParentId(comment.getParent() != null ? comment.getParent().getId() : null);
        dto.setCreatedAt(comment.getCreatedAt().format(FMT));
        dto.setLikeCount(likeRepository.countByCommentId(comment.getId()));
        dto.setLikedByMe(likeRepository.existsByUserIdAndCommentId(userId, comment.getId()));

        // 加载回复
        List<Comment> replies = commentRepository.findByParentIdOrderByCreatedAtAsc(comment.getId());
        dto.setReplies(replies.stream()
                .map(r -> toDto(r, userId))
                .collect(Collectors.toList()));

        return dto;
    }
}
