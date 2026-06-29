package com.grootan.router.service.impl;

import com.grootan.router.dto.request.CreateUserRequest;
import com.grootan.router.dto.request.UpdatePreferenceRequest;
import com.grootan.router.dto.response.UserResponse;
import com.grootan.router.entity.User;
import com.grootan.router.entity.UserPreference;
import com.grootan.router.exception.ResourceAlreadyExistsException;
import com.grootan.router.exception.ResourceNotFoundException;
import com.grootan.router.repository.UserPreferenceRepository;
import com.grootan.router.repository.UserRepository;
import com.grootan.router.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserPreferenceRepository userPreferenceRepository;

    @Override
    public UserResponse createUser(CreateUserRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already exists.");
        }

        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new ResourceAlreadyExistsException("Phone number already exists.");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .build();

        User savedUser = userRepository.save(user);

        UserPreference preference = UserPreference.builder()
                .user(savedUser)
                .emailEnabled(true)
                .smsEnabled(true)
                .build();

        userPreferenceRepository.save(preference);

        return UserResponse.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .phoneNumber(savedUser.getPhoneNumber())
                .emailEnabled(preference.getEmailEnabled())
                .smsEnabled(preference.getSmsEnabled())
                .build();
    }

    @Override
    public UserResponse getUserById(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with id : " + userId));

        UserPreference preference = userPreferenceRepository.findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User preference not found."));

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .emailEnabled(preference.getEmailEnabled())
                .smsEnabled(preference.getSmsEnabled())
                .build();
    }

    @Override
    public UserResponse updateUserPreference(Long userId,
                                             UpdatePreferenceRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with id : " + userId));

        UserPreference preference = userPreferenceRepository.findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User preference not found."));

        preference.setEmailEnabled(request.getEmailEnabled());
        preference.setSmsEnabled(request.getSmsEnabled());

        UserPreference updatedPreference = userPreferenceRepository.save(preference);

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .emailEnabled(updatedPreference.getEmailEnabled())
                .smsEnabled(updatedPreference.getSmsEnabled())
                .build();
    }

}