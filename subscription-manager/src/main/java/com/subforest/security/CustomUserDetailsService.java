package com.subforest.security;

import com.subforest.entity.User;
import com.subforest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // Spring Security에서 username으로 불릴 때
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Not found: " + email));
        // 권한은 간단히 빈 리스트 (확장 시 role 정보를 넣어주세요)
        return new CustomUserDetails(user, List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    // JwtAuthenticationFilter에서 직접 사용하기 위한 helper
    public Authentication getAuthenticationByEmail(String email) {
        UserDetails userDetails = loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
