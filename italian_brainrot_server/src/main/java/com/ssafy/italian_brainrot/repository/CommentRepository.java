package com.ssafy.italian_brainrot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.italian_brainrot.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

}
