package com.rwtogether.service;

import com.rwtogether.dto.ProgressDto;
import com.rwtogether.dto.WorkDetailDto;
import com.rwtogether.entity.User;
import com.rwtogether.entity.UserWork;
import com.rwtogether.entity.Work;
import com.rwtogether.exception.BusinessException;
import com.rwtogether.exception.ResourceNotFoundException;
import com.rwtogether.repository.UserRepository;
import com.rwtogether.repository.UserWorkRepository;
import com.rwtogether.repository.WorkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private final UserWorkRepository userWorkRepository;
    private final WorkRepository workRepository;
    private final UserRepository userRepository;

    public ProgressDto getUserProgress(Long userId, Long workId) {
        return userWorkRepository.findByUserIdAndWorkId(userId, workId)
                .map(this::toDto)
                .orElse(null);
    }

    public List<WorkDetailDto.UserProgressDto> getWorkProgresses(Long workId, Long excludeUserId) {
        return userWorkRepository.findByWorkId(workId).stream()
                .filter(uw -> !uw.getUser().getId().equals(excludeUserId))
                .map(this::toUserProgressDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProgressDto setProgress(Long userId, Long workId, String status, String progressDetail, Integer rating) {
        Work work = workRepository.findById(workId)
                .orElseThrow(() -> new ResourceNotFoundException("作品不存在"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        UserWork userWork = userWorkRepository.findByUserIdAndWorkId(userId, workId)
                .orElseGet(() -> {
                    UserWork uw = new UserWork();
                    uw.setUser(user);
                    uw.setWork(work);
                    return uw;
                });

        UserWork.Status newStatus = UserWork.Status.valueOf(status);
        UserWork.Status oldStatus = userWork.getStatus();

        userWork.setStatus(newStatus);
        if (progressDetail != null) {
            userWork.setProgressDetail(progressDetail);
        }
        if (rating != null) {
            userWork.setRating(rating);
        }

        // 首次标记在看时设置开始日期
        if (newStatus == UserWork.Status.WATCHING && oldStatus == UserWork.Status.WANT) {
            userWork.setStartedAt(LocalDate.now());
        }
        // 标记完成时设置结束日期
        if (newStatus == UserWork.Status.DONE) {
            userWork.setFinishedAt(LocalDate.now());
            if (userWork.getStartedAt() == null) {
                userWork.setStartedAt(LocalDate.now());
            }
        }

        userWorkRepository.save(userWork);
        return toDto(userWork);
    }

    @Transactional
    public void removeProgress(Long userId, Long workId) {
        userWorkRepository.findByUserIdAndWorkId(userId, workId)
                .ifPresent(userWorkRepository::delete);
    }

    public long countByStatus(Long userId, String status) {
        return userWorkRepository.countByUserIdAndStatus(userId, UserWork.Status.valueOf(status));
    }

    public List<UserWork> getByUserIdAndStatus(Long userId, String status) {
        return userWorkRepository.findByUserIdAndStatus(userId, UserWork.Status.valueOf(status));
    }

    public List<UserWork> getByUserId(Long userId) {
        return userWorkRepository.findByUserId(userId);
    }

    private ProgressDto toDto(UserWork uw) {
        return new ProgressDto(
                uw.getId(),
                uw.getStatus().name(),
                uw.getProgressDetail(),
                uw.getRating(),
                uw.getStartedAt(),
                uw.getFinishedAt()
        );
    }

    private WorkDetailDto.UserProgressDto toUserProgressDto(UserWork uw) {
        return new WorkDetailDto.UserProgressDto(
                uw.getUser().getId(),
                uw.getUser().getUsername(),
                uw.getUser().getAvatar(),
                uw.getStatus().name(),
                uw.getProgressDetail(),
                uw.getRating()
        );
    }
}
