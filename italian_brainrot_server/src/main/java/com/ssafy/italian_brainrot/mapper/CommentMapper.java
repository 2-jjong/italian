package com.ssafy.italian_brainrot.mapper;

import com.ssafy.italian_brainrot.dto.comment.CommentResponseDTO;
import com.ssafy.italian_brainrot.entity.User;
import org.springframework.stereotype.Component;

import com.ssafy.italian_brainrot.dto.comment.CommentRequestDTO;
import com.ssafy.italian_brainrot.entity.Comment;

@Component
public class CommentMapper {
	public Comment convertToComment(CommentRequestDTO dto) {
		return Comment.builder()
				.id(dto.getId())
				.user(User.builder().id(dto.getUserId()).build())
				.productId(dto.getProductId())
				.rating(dto.getRating())
				.comment(dto.getComment())
				.build();
	}

	public CommentResponseDTO convertToCommentResponseDTO(Comment entity) {
		return CommentResponseDTO.builder()
				.id(entity.getId())
				.userId(entity.getUser().getId())
				.userName(entity.getUser().getName())
				.productId(entity.getProductId())
				.rating(entity.getRating())
				.comment(entity.getComment())
				.build();
	}
}
