package com.ssafy.italian_brainrot.service.user;

import com.ssafy.italian_brainrot.dto.user.UserRequestDTO;
import com.ssafy.italian_brainrot.entity.User;
import com.ssafy.italian_brainrot.mapper.UserMapper;
import com.ssafy.italian_brainrot.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
        userRepository.save(userMapper.convertUser(user));
        return userRepository.existsById(user.getId());
    }

    @Override
    public User login(String id, String pass) {
        return userRepository.findByIdAndPass(id, pass);
    }

    @Override
    public boolean isUsedId(String id) {
        User user = userRepository.findById(id).orElse(null);
        return user != null;
    }

    @Override
    public User selectUser(String id) {
        return userRepository.findById(id).orElse(null);
    }
}
