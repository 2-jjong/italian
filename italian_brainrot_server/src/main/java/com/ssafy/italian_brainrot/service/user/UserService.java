package com.ssafy.italian_brainrot.service.user;

import com.ssafy.italian_brainrot.dto.user.UserRequestDTO;
import com.ssafy.italian_brainrot.dto.user.UserResponseDTO;

public interface UserService {

    /**
     * 사용자 정보를 DB에 저장하고 저장된 결과를 반환한다.
     *
     * @param user
     * @return 저장된 결과를 반환한다.
     */
    public boolean join(UserRequestDTO user);

    /**
     * id, pass에 해당하는 User 정보를 반환한다.
     *
     * @param id
     * @param pass
     * @return 조회된 결과를 반환한다.
     */
    public boolean login(String id, String pass);

    /**
     * 해당 아이디가 이미 사용 중인지를 반환한다.
     *
     * @param id
     * @return 아이디 사용 여부를 반환한다.
     */
    public boolean isUsedId(String id);

    /**
     * id 에 해당하는 User 정보를 반환한다.
     *
     * @param id
     * @return 조회된 User 정보를 반환한다.
     */
    public UserResponseDTO getUserById(String id);

    /**
     * 사용자의 FCM 토큰을 업데이트한다.
     *
     * @param userId
     * @param fcmToken
     * @return 저장된 FCM 토큰
     */
    public String updateFcmToken(String userId, String fcmToken);

    /**
     * 사용자의 포인트를 충전한다.
     *
     * @param userId
     * @param point
     * @return 반영된 포인트
     */
    public int chargePoint(String userId, int point);
}