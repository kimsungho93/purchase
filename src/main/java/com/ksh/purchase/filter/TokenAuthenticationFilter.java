package com.ksh.purchase.filter;

import com.ksh.purchase.exception.CustomException;
import com.ksh.purchase.service.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;

    private final static String TOKEN_PREFIX = "Bearer ";
    private static final List<String> openApiEndpoints = List.of(
            "/api/v1/users",                              // 회원가입
            "/api/v1/auth/email/verify",            // 이메일 인증
            "/api/v1/users/login"                      // 로그인
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (shouldFilter(request.getRequestURI())) {
            processAuthentication(request);
        }
        filterChain.doFilter(request, response);
    }

    // 요청 URI가 공개 API에 해당하는지 확인
    private boolean shouldFilter(String requestURI) {
        return openApiEndpoints.stream().noneMatch(uri -> pathMatcher.match(uri, requestURI));
    }

    // 인증 처리 로직
    private void processAuthentication(HttpServletRequest request) {
        final String authorizationHeader = request.getHeader("Authorization");
        validateAuthorizationHeader(authorizationHeader);

        final String token = extractToken(authorizationHeader);
        if (tokenProvider.validateToken(token)) {
            setAuthentication(token);
        } else {
            throw new CustomException("유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED);
        }
    }

    // Authorization 헤더 검증
    private void validateAuthorizationHeader(String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith(TOKEN_PREFIX)) {
            throw new CustomException("로그인 후 이용해주세요.", HttpStatus.UNAUTHORIZED);
        }
    }

    // Bearer 제거 후 토큰 추출
    private String extractToken(String authorizationHeader) {
        return authorizationHeader.substring(TOKEN_PREFIX.length());
    }

    // SecurityContext에 인증 정보 등록
    private void setAuthentication(String token) {
        Authentication authentication = tokenProvider.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}