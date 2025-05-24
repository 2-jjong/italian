package com.ssafy.italian_brainrot.service.user;

import com.ssafy.italian_brainrot.dto.user.UserRequestDTO;
import com.ssafy.italian_brainrot.dto.user.UserResponseDTO;

public interface UserService {

    Boolean join(UserRequestDTO user);

    Boolean login(String id, String pass);

    Boolean isUsedId(String id);

    UserResponseDTO getUserById(String id);

    String updateFcmToken(String userId, String fcmToken);

    Integer chargePoint(String userId, int point);

}