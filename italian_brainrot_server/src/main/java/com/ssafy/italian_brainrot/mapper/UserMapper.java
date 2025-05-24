
package com.ssafy.italian_brainrot.mapper;

import com.ssafy.italian_brainrot.dto.user.GradeDTO;
import com.ssafy.italian_brainrot.dto.user.UserRequestDTO;
import com.ssafy.italian_brainrot.dto.user.UserResponseDTO;
import com.ssafy.italian_brainrot.entity.User;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User convertToUser(UserRequestDTO userRequestDTO) {
        return User.builder()
                .id(userRequestDTO.getId())
                .name(userRequestDTO.getName())
                .pass(userRequestDTO.getPass())
                .build();
    }

    public UserResponseDTO convertToUserResponseDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .grade(calculateGrade(user.getStamps()))
                .point(user.getPoint())
                .build();
    }

    @Getter
    private enum Level {
        SEED("씨앗", 10, 50, "seeds.png"),
        FLOWER("꽃", 15, 125, "flower.png"),
        FRUIT("열매", 20, 225, "coffee_fruit.png"),
        BEAN("커피콩", 25, 350, "coffee_beans.png"),
        TREE("커피나무", Integer.MAX_VALUE, Integer.MAX_VALUE, "coffee_tree.png");

        private final String title;
        private final int unit;
        private final int max;
        private final String img;

        Level(String title, int unit, int max, String img) {
            this.title = title;
            this.unit = unit;
            this.max = max;
            this.img = img;
        }
    }

    private GradeDTO calculateGrade(int stamps) {
        int level = 0;
        int remain;
        int step = 0;
        Level[] levels = Level.values();

        if (stamps > 0) {
            stamps--;
            remain = stamps;
            for (int i = 0; i < levels.length - 1; i++) {
                if (stamps < levels[i].getMax()) {
                    if (i > 0) {
                        remain -= levels[i - 1].getMax();
                    }
                    break;
                }
                level++;
            }
            step = (remain / levels[level].getUnit()) + 1;
            remain = remain % levels[level].getUnit();
            remain = levels[level].getUnit() - remain;
        } else {
            remain = 1;
        }

        Level currentLevel = levels[level];

        return GradeDTO.builder()
                .title(currentLevel.getTitle())
                .img(currentLevel.getImg())
                .step(level == 4 ? null : step)
                .stepMax(currentLevel.getUnit())
                .to(level < 4 ? remain : null)
                .totalStamps(stamps + 1)
                .build();
    }
}