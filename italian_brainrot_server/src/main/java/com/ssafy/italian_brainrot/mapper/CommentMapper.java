package com.ssafy.italian_brainrot.mapper;

import com.ssafy.italian_brainrot.dto.comment.CommentInfoDTO;
import com.ssafy.italian_brainrot.dto.comment.CommentRequestDTO;
import org.springframework.stereotype.Component;

import com.ssafy.italian_brainrot.dto.comment.CommentDTO;
import com.ssafy.italian_brainrot.entity.Comment;

@Component
public class CommentMapper {
	public CommentDTO convertToCommentDTO(Comment entity) {
		CommentDTO dto = new CommentDTO(entity.getId(), entity.getUserId(), entity.getProductId(), entity.getRating(),
				entity.getComment());
		return dto;
	}

	public Comment convertToComment(CommentDTO dto) {
		Comment entity = new Comment(dto.getId(), dto.getUserId(), dto.getProductId(), dto.getRating(),
				dto.getComment());
		return entity;
	}

	public CommentInfoDTO convertToCommentInfo(Comment entity, String userName) {
		return CommentInfoDTO.builder()
				.id(entity.getId())
				.userId(entity.getUserId())
				.productId(entity.getProductId())
				.rating(entity.getRating())
				.comment(entity.getComment())
				.userName(userName)
				.build();
	}

}
