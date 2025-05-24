package com.ssafy.italian_brainrot.interceptor;

import com.ssafy.italian_brainrot.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);
    private final CookieUtil cookieUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuthInterceptor(final CookieUtil cookieUtil) {
        this.cookieUtil = cookieUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        logger.debug("인증 체크 - URI: {}, Method: {}", requestURI, method);

        if ("OPTIONS".equals(method)) {
            return true;
        }

        String userId = cookieUtil.getUserIdFromCookie(request);

        if (userId == null) {
            logger.warn("인증 실패 - 로그인 쿠키가 없음: {}", requestURI);
            sendUnauthorizedResponse(response, "로그인이 필요합니다.");
            return false;
        }

        request.setAttribute("userId", userId);
        logger.debug("인증 성공 - userId: {}", userId);

        return true;
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", message);

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}