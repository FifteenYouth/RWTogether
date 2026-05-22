package com.rwtogether.controller;

import com.rwtogether.dto.UserDto;
import com.rwtogether.dto.WorkDto;
import com.rwtogether.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDto>> list() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/{id}/works")
    public ResponseEntity<List<WorkDto>> getWorks(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserWorks(id));
    }
}
