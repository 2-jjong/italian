package com.ssafy.italian_brainrot.mapper;

import com.ssafy.italian_brainrot.dto.user.UserRequestDTO;
import com.ssafy.italian_brainrot.dto.user.UserResponseDTO;
import com.ssafy.italian_brainrot.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User convertUser(UserRequestDTO userRequestDTO) {
        return User.builder()
                .id(userRequestDTO.getId())
                .name(userRequestDTO.getName())
                .pass(userRequestDTO.getPass())
                .build();
    }

    public UserResponseDTO convertUserResponseDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .stamps(user.getStamps())
                .point(user.getPoint())
                .build();
    }
}
