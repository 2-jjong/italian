package com.ssafy.italian_brainrot.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;

@Component
public class CookieUtil {

    public String getUserIdFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("loginId".equals(cookie.getName())) {
                    String value = cookie.getValue();
                    return (value != null && !value.trim().isEmpty()) ? value : null;
                }
            }
        }
        return null;
    }

    public String getUserIdFromRequest(HttpServletRequest request) {
        Object userId = request.getAttribute("userId");
        return userId != null ? userId.toString() : null;
    }

    public Boolean setLoginCookie(HttpServletResponse response, String userId) {
        try {
            Cookie cookie = new Cookie("loginId", URLEncoder.encode(userId, "UTF-8"));
            cookie.setMaxAge(60 * 60 * 24);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void removeLoginCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("loginId", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

}