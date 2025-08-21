package com.subforest.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(JwtFilter.class);
    private static final org.springframework.util.AntPathMatcher PATH = new org.springframework.util.AntPathMatcher();

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    // 공개(permitAll) 경로는 필터 건너뜀
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return PATH.match("/auth/**", uri)
            || PATH.match("/v3/api-docs/**", uri)
            || PATH.match("/swagger-ui/**", uri)
            || PATH.match("/error", uri)
            || ("OPTIONS".equalsIgnoreCase(request.getMethod())); // CORS preflight
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        String auth = request.getHeader("Authorization");
        log.info("[JwtFilter] enter uri={} auth={}", uri,
                auth == null ? "null" : (auth.length() > 30 ? auth.substring(0,15)+"...(len="+auth.length()+")" : auth));

        if (auth == null || !auth.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = auth.substring(7).trim();
        try {
            String email = jwtUtil.getEmailFromToken(token);
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails user = userDetailsService.loadUserByUsername(email);
                if (jwtUtil.validateToken(token)) {
                    var at = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    at.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(at);
                }
            }
            chain.doFilter(request, response);
        } catch (io.jsonwebtoken.JwtException e) {
            String sample = token.length() > 30 ? token.substring(0,15)+"..."+token.substring(token.length()-15) : token;
            log.warn("JWT verify failed: {} token={}", e.getClass().getSimpleName(), sample);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"invalid_token\",\"message\":\"" + e.getClass().getSimpleName() + "\"}");
        }
    }
}
