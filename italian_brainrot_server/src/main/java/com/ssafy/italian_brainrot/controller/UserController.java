package com.ssafy.italian_brainrot.controller;

import com.ssafy.italian_brainrot.dto.user.UserRequestDTO;
import com.ssafy.italian_brainrot.dto.user.UserResponseDTO;
import com.ssafy.italian_brainrot.service.user.UserService;
import com.ssafy.italian_brainrot.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;// 기존 코드 호환을 위해 추가
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
        String userId = getUserIdFromCookie(request);
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
        return cookieUtil.getUserIdFromRequest(request);
    }

    public Map<String, Object> getGrade(Integer stamp) {
        Map<String, Object> grade = new HashMap<>();
        int level = 0;
        int remain = 11;
        int step = 0;
        List<Level> levelData = Level.levelData;
        if (stamp > 0) {
            stamp--;
            remain = stamp;
            for (int i = 0; i < levelData.size() - 1; i++) {
                if (stamp < levelData.get(i).getMax()) {
                    if (i > 0) {
                        remain -= levelData.get(i - 1).getMax();
                    }
                    break;
                }
                level++;
            }
            step = (remain / levelData.get(level).getUnit()) + 1;
            remain = remain % levelData.get(level).getUnit();
            remain = levelData.get(level).getUnit() - remain;
        } else {
            remain = 1;
        }

        grade.put("img", levelData.get(level).getImg());
        grade.put("step", level == 4 ? null : step);
        grade.put("stepMax", levelData.get(level).getUnit());
        grade.put("title", levelData.get(level).getTitle());
        if (level < 4) {
            grade.put("to", remain);
        }
        return grade;
    }
}

@Setter
@Getter
@AllArgsConstructor
class Level {
    private String title;
    private int unit;
    private int max;
    private String img;

    public static List<Level> levelData;
    static {
        List<Level> levels = new ArrayList<>();
        levels.add(new Level("씨앗", 10, 50, "seeds.png"));
        levels.add(new Level("꽃", 15, 125, "flower.png"));
        levels.add(new Level("열매", 20, 225, "coffee_fruit.png"));
        levels.add(new Level("커피콩", 25, 350, "coffee_beans.png"));
        levels.add(new Level("커피나무", Integer.MAX_VALUE, Integer.MAX_VALUE, "coffee_tree.png"));
        levelData = levels;
    }
}