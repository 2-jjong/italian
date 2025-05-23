package com.ssafy.italian_brainrot.repository;

import com.ssafy.italian_brainrot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User findByIdAndPass(String id, String pass);
}
