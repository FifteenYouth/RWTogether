package com.rwtogether.service;

import com.rwtogether.dto.UserDto;
import com.rwtogether.dto.WorkDto;
import com.rwtogether.entity.User;
import com.rwtogether.entity.UserWork;
import com.rwtogether.entity.Work;
import com.rwtogether.exception.ResourceNotFoundException;
import com.rwtogether.repository.UserRepository;
import com.rwtogether.repository.UserWorkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserWorkRepository userWorkRepository;

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
        return toDto(user);
    }

    public List<WorkDto> getUserWorks(Long userId) {
        List<UserWork> userWorks = userWorkRepository.findByUserId(userId);
        return userWorks.stream()
                .map(uw -> {
                    Work work = uw.getWork();
                    return new WorkDto(
                            work.getId(),
                            work.getTitle(),
                            work.getCoverUrl(),
                            work.getType().name(),
                            work.getTotalEpisodes(),
                            work.getApiSource()
                    );
                })
                .collect(Collectors.toList());
    }

    private UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getUsername(), user.getAvatar(), user.getBio());
    }
}
