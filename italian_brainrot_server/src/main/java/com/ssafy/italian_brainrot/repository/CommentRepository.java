package com.ssafy.italian_brainrot.repository;

import com.ssafy.italian_brainrot.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findByProductId(int productId);
}
