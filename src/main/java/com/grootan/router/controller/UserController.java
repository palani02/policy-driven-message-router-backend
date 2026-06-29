package com.grootan.router.controller;

import com.grootan.router.dto.request.CreateUserRequest;
import com.grootan.router.dto.request.UpdatePreferenceRequest;
import com.grootan.router.dto.response.UserResponse;
import com.grootan.router.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request) {

        UserResponse response = userService.createUser(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long userId) {

        UserResponse response = userService.getUserById(userId);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{userId}/preferences")
    public ResponseEntity<UserResponse> updateUserPreference(
            @PathVariable Long userId,
            @Valid @RequestBody UpdatePreferenceRequest request) {

        UserResponse response =
                userService.updateUserPreference(userId, request);

        return ResponseEntity.ok(response);
    }

}