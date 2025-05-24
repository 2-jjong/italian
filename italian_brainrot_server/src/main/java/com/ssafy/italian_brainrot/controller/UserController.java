package com.ssafy.italian_brainrot.controller;

import com.ssafy.italian_brainrot.dto.user.UserRequestDTO;
import com.ssafy.italian_brainrot.dto.user.UserResponseDTO;
import com.ssafy.italian_brainrot.service.user.UserService;
import com.ssafy.italian_brainrot.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final CookieUtil cookieUtil;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService, CookieUtil cookieUtil) {
        this.userService = userService;
        this.cookieUtil = cookieUtil;
    }

    @PostMapping
    public Boolean join(@RequestBody UserRequestDTO user) {
        return userService.join(user);
    }

    @PostMapping("/login")
    public Boolean login(@RequestBody UserRequestDTO user, HttpServletResponse response) {
        boolean loginSuccess = userService.login(user.getId(), user.getPass());

        if (loginSuccess) {
            return cookieUtil.setLoginCookie(response, user.getId());
        }

        return false;
    }

    @PostMapping("/logout")
    public Boolean logout(HttpServletResponse response) {
        cookieUtil.removeLoginCookie(response);
        return true;
    }

    @GetMapping
    public UserResponseDTO getUserInfo(HttpServletRequest request) {
        String userId = cookieUtil.getUserIdFromCookie(request);
        if (userId == null) {
            return null;
        }

        return userService.getUserById(userId);
    }

    @GetMapping("/isUsed/{id}")
    public Boolean isUsed(@PathVariable("id") String id) {
        return userService.isUsedId(id);
    }

    @PostMapping("/token")
    public String updateFcmToken(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
        String userId = getUserIdFromCookie(httpRequest);
        if (userId == null) {
            logger.warn("FCM 토큰 업데이트 실패: 로그인이 필요함");
            return null;
        }

        String fcmToken = request.get("fcmToken");
        if (fcmToken == null || fcmToken.trim().isEmpty()) {
            logger.warn("FCM 토큰 업데이트 실패: 토큰이 비어있음");
            return null;
        }

        return userService.updateFcmToken(userId, fcmToken);
    }

    @PutMapping("/point")
    public Integer chargePoint(@RequestBody Map<String, Integer> request, HttpServletRequest httpRequest) {
        String userId = getUserIdFromCookie(httpRequest);
        if (userId == null) {
            logger.warn("포인트 충전 실패: 로그인이 필요함");
            return -1;
        }

        Integer point = request.get("point");
        if (point == null || point <= 0) {
            logger.warn("포인트 충전 실패: 유효하지 않은 포인트 값 - {}", point);
            return -1;
        }

        return userService.chargePoint(userId, point);
    }

    private String getUserIdFromCookie(HttpServletRequest request) {
        return cookieUtil.getUserIdFromCookie(request);
    }
}