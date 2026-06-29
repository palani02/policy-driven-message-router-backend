package com.grootan.router.service;

import com.grootan.router.dto.request.CreateUserRequest;
import com.grootan.router.dto.request.UpdatePreferenceRequest;
import com.grootan.router.dto.response.UserResponse;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    UserResponse getUserById(Long userId);

    UserResponse updateUserPreference(Long userId,
                                      UpdatePreferenceRequest request);

}