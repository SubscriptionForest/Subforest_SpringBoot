package com.subforest.service;

import com.subforest.entity.User;
import com.subforest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 내 정보 조회
    public User getUserInfo(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    // 알림 설정 변경
    @Transactional
    public boolean updateNotification(Long userId, boolean enabled) {
        User user = getUserInfo(userId);
        user.setNotificationEnabled(enabled);
        userRepository.save(user);
        return enabled;
    }

    // 비밀번호 변경
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getUserInfo(userId);

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Invalid old password");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // 계정 비활성화
    @Transactional
    public void deactivateAccount(Long userId) {
        User user = getUserInfo(userId);
        user.setStatus(User.UserStatus.INACTIVE);
        userRepository.save(user);
    }
}
