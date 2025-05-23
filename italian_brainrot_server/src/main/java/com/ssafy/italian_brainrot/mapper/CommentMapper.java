package com.ssafy.italian_brainrot.mapper;

import org.springframework.stereotype.Component;

import com.ssafy.italian_brainrot.dto.CommentDTO;
import com.ssafy.italian_brainrot.entity.Comment;

@Component
public class CommentMapper {
	public CommentDTO convert(Comment entity) {
		CommentDTO dto = new CommentDTO(entity.getId(), entity.getUserId(), entity.getProductId(), entity.getRating(),
				entity.getComment());
		return dto;
	}

	public Comment convert(CommentDTO dto) {
		Comment entity = new Comment(dto.getId(), dto.getUserId(), dto.getProductId(), dto.getRating(),
				dto.getComment());
		return entity;
	}
}
