package com.rwtogether.controller;

import com.rwtogether.dto.LoginRequest;
import com.rwtogether.dto.LoginResponse;
import com.rwtogether.dto.UserDto;
import com.rwtogether.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginOrRegister(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.loginOrRegister(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(authService.getCurrentUser(userId));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserDto> updateProfile(Authentication authentication,
                                                  @RequestBody Map<String, String> body) {
        Long userId = (Long) authentication.getPrincipal();
        String avatar = body.get("avatar");
        String bio = body.get("bio");
        return ResponseEntity.ok(authService.updateProfile(userId, avatar, bio));
    }
}
