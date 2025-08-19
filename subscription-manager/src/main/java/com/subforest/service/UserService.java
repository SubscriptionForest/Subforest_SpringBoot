package com.subforest.service;

import com.subforest.entity.User;
import com.subforest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User getUserInfo(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public boolean updateNotification(Long userId, boolean enabled) {
        User user = getUserInfo(userId);
        user.setNotificationEnabled(enabled);
        userRepository.save(user);
        return enabled;
    }

    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getUserInfo(userId);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Invalid old password");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void deactivateAccount(Long userId) {
        User user = getUserInfo(userId);
        user.setStatus(User.Status.INACTIVE);
        userRepository.save(user);
    }
}
