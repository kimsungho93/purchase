package com.ksh.purchase.filter;

import com.ksh.purchase.entity.User;
import com.ksh.purchase.exception.CustomException;
import com.ksh.purchase.exception.ErrorCode;
import com.ksh.purchase.service.RedisService;
import com.ksh.purchase.service.TokenProvider;
import com.ksh.purchase.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
@Component
@Slf4j
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;
    private final UserService userService;
    private final RedisService redisService;

    private final static String TOKEN_PREFIX = "Bearer ";
    private static final Map<String, String> openApiEndpoints = Map.of(
            "/api/v1/auth/email/verify", "GET",
            "/api/v1/users/login", "POST",
            "/api/v1/users", "POST",
            "/api/v1/products", "GET"
    );
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("doFilterInternal");
        log.info("requestURI: {}", request.getRequestURI());
        if (request.getRequestURI().contains("/h2-console")) {
            filterChain.doFilter(request, response);
            return;
        }
        if (!shouldFilter(request.getRequestURI(), request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }
        processAuthentication(request);
        filterChain.doFilter(request, response);
    }

    // 요청 URI가 공개 API에 해당하는지 확인
    private boolean shouldFilter(String requestURI, String method) {
        log.info("requestURI: {}, method: {}", requestURI, method);
        return openApiEndpoints.entrySet().stream()
                .noneMatch(entry -> pathMatcher.match(entry.getKey(), requestURI) && entry.getValue().equals(method));

    }

    // 인증 처리 로직
    private void processAuthentication(HttpServletRequest request) {
        log.info("processAuthentication");
        final String authorizationHeader = request.getHeader("Authorization");
        validateAuthorizationHeader(authorizationHeader);

        final String token = extractToken(authorizationHeader);
        if (tokenProvider.validateToken(token) && redisService.hasKey(token)) { // 이 부분 추가
            setAuthentication(token);
        } else {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    // Authorization 헤더 검증
    private void validateAuthorizationHeader(String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith(TOKEN_PREFIX)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_REQUEST);
        }
    }

    // Bearer 제거 후 토큰 추출
    private String extractToken(String authorizationHeader) {
        return authorizationHeader.substring(TOKEN_PREFIX.length());
    }

    // SecurityContext에 인증 정보 등록
    private void setAuthentication(String token) {
        log.info("setAuthentication");
        Long userId = tokenProvider.getUserIdFromToken(token);
        User findUser = userService.findById(userId);
        org.springframework.security.core.userdetails.User user =
                new org.springframework.security.core.userdetails.User(findUser.getId().toString(), findUser.getPassword(), findUser.getAuthorities());

        UsernamePasswordAuthenticationToken authenticationToken =
                UsernamePasswordAuthenticationToken.authenticated(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
