package com.ssafy.italian_brainrot.service.user;

import com.ssafy.italian_brainrot.dto.user.GradeDTO;
import com.ssafy.italian_brainrot.dto.user.UserRequestDTO;
import com.ssafy.italian_brainrot.dto.user.UserResponseDTO;
import com.ssafy.italian_brainrot.entity.User;
import com.ssafy.italian_brainrot.mapper.UserMapper;
import com.ssafy.italian_brainrot.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);



    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public boolean join(UserRequestDTO user) {
        userRepository.save(userMapper.convertToUser(user));
        return userRepository.existsById(user.getId());
    }

    @Override
    public boolean login(String id, String pass) {
        User user = userRepository.findByIdAndPass(id, pass);
        return user != null;
    }

    @Override
    public boolean isUsedId(String id) {
        User user = userRepository.findById(id).orElse(null);
        return user != null;
    }

    @Override
    public UserResponseDTO getUserById(String id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return null;
        }
        return userMapper.convertToUserResponseDTO(user);
    }

    @Override
    @Transactional
    public String updateFcmToken(String userId, String fcmToken) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            logger.warn("FCM 토큰 업데이트 실패: 사용자를 찾을 수 없음 - {}", userId);
            return null;
        }

        user.setFcmToken(fcmToken);
        userRepository.save(user);
        logger.debug("FCM 토큰 업데이트 성공: {} -> {}", userId, fcmToken);
        return user.getFcmToken();
    }

    @Override
    @Transactional
    public int chargePoint(String userId, int point) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            logger.warn("포인트 충전 실패: 사용자를 찾을 수 없음 - {}", userId);
            return -1;
        }

        user.setPoint(user.getPoint() + point);
        userRepository.save(user);
        logger.debug("포인트 충전 성공: {} -> {} 포인트 충전, 총 {} 포인트", userId, point, user.getPoint());
        return user.getPoint();
    }


}