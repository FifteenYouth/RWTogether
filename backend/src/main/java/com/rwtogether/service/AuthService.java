package com.rwtogether.service;

import com.rwtogether.config.JwtUtil;
import com.rwtogether.dto.LoginRequest;
import com.rwtogether.dto.LoginResponse;
import com.rwtogether.dto.UserDto;
import com.rwtogether.entity.User;
import com.rwtogether.exception.BusinessException;
import com.rwtogether.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginResponse loginOrRegister(LoginRequest request) {
        var userOpt = userRepository.findByUsername(request.getUsername());

        if (userOpt.isPresent()) {
            // 登录
            User user = userOpt.get();
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new BusinessException("密码错误");
            }
            return buildResponse(user);
        } else {
            // 注册
            User newUser = new User();
            newUser.setUsername(request.getUsername());
            newUser.setPassword(passwordEncoder.encode(request.getPassword()));
            userRepository.save(newUser);
            return buildResponse(newUser);
        }
    }

    public UserDto getCurrentUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        return toDto(user);
    }

    public UserDto updateProfile(Long userId, String avatar, String bio) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        if (avatar != null) {
            user.setAvatar(avatar);
        }
        if (bio != null) {
            user.setBio(bio);
        }
        userRepository.save(user);
        return toDto(user);
    }

    private LoginResponse buildResponse(User user) {
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        return new LoginResponse(token, toDto(user));
    }

    private UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getUsername(), user.getAvatar(), user.getBio());
    }
}
