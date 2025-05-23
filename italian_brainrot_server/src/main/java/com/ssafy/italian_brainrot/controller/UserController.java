package com.ssafy.italian_brainrot.controller;

import com.ssafy.italian_brainrot.dto.user.UserRequestDTO;
import com.ssafy.italian_brainrot.entity.User;
import com.ssafy.italian_brainrot.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public Boolean join(@RequestBody UserRequestDTO user) {
        return userService.join(user);
    }

    @PostMapping("/login")
    public User login(@RequestBody UserRequestDTO user, HttpServletResponse response) {
        User result = userService.login(user.getId(), user.getPass());

        // 쿠키 설정
        if (result != null) {
            Cookie cookie;
            try {
                cookie = new Cookie("loginId", URLEncoder.encode(result.getId(), "UTF-8"));
                cookie.setMaxAge(60 * 5);
                response.addCookie(cookie);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    @PostMapping("/info")
    public Map<String, Object> getUserInfo(@RequestBody UserRequestDTO user) {
        Map<String, Object> map = new HashMap<>();

        // user 조회
        User resultUser = userService.login(user.getId(), user.getPass());

        // user가 null이면 id, pass가 틀렸다는 뜻, null 반환
        if (resultUser == null)
            return null;

        // user가 주문한 주문 내역들 조회
//        List<Order> resultOrder = oService.getOrderByUser(user.getId());

        map.put("user", resultUser);
//        map.put("order", resultOrder);
        map.put("grade", getGrade(resultUser.getStamps()));

        return map;
    }

    @Operation(summary = "사용자 정보 조회", description = "관통 6단계(Android app)에서 사용된다.\n\n"
            + "로그인 성공한 cookie 정보가 없으면 전체 null이 반환된다.\n\n")
    @GetMapping("/info")
    public Map<String, Object> getUserInfo2(@RequestParam String id, HttpServletRequest request) {
        logger.debug("getUserInfo2: " + id);

        Cookie[] cookies = request.getCookies();
        boolean hasLoginCookie = false;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("loginId".equals(cookie.getName()) && cookie.getValue() != null) {
                    hasLoginCookie = true;
                    break;
                }
            }
        }

        if (!hasLoginCookie) {
            return null;
        }

        Map<String, Object> map = new HashMap<>();

        User resultUser = userService.selectUser(id);

        if (resultUser == null) {
            return null;
        }

        // user가 주문한 주문 내역들 조회
//        List<Order> resultOrder = oService.getOrderByUser(user.getId());

        map.put("user", resultUser);
//        map.put("order", resultOrder);
        map.put("grade", getGrade(resultUser.getStamps()));

        return map;
    }

    @GetMapping("/isUsed/{id}")
    public Boolean isUsed(@PathVariable("id") String id) {
        return userService.isUsedId(id);
    }

    /**
     * 사용자 등급정보를 계산하여 리턴한다. 이미지가 seeds.png, 등급이 씨앗, step이 1, stepMax가 1, to가 7 이라면,
     * 씨앗 1단계 등급의 사용자가 다음단계로 가려면 7개 더 모아야 함을 의미한다. img, step, stepMax, to, title이 각
     * key가 되고, 각각의 key을 맞게 입력하여 리턴하면 된다.
     *
     * src/test/java에 있는 테스트 케이스를 통과하면 정상동작한다.
     *
     * @param stamp
     * @return
     */
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