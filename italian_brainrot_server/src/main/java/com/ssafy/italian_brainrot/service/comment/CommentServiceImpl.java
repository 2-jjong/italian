package com.ssafy.italian_brainrot.service.comment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ssafy.italian_brainrot.dto.comment.CommentRequestDTO;
import com.ssafy.italian_brainrot.entity.Comment;
import com.ssafy.italian_brainrot.mapper.CommentMapper;
import com.ssafy.italian_brainrot.repository.CommentRepository;

@Service
public class CommentServiceImpl implements CommentService {

	private final CommentRepository commentRepository;
	private final CommentMapper commentMapper;
	private final Logger log = LoggerFactory.getLogger(CommentServiceImpl.class);

	public CommentServiceImpl(CommentRepository commentRepository,
							  CommentMapper commentMapper) {
		this.commentRepository = commentRepository;
		this.commentMapper = commentMapper;
	}

	@Override
	public Boolean insertComment(CommentRequestDTO commentRequestDTO) {
		Comment entity = commentMapper.convertToComment(commentRequestDTO);
		entity = commentRepository.save(entity);
        return entity.getId() > 0;
	}

	@Override
	public Boolean updateComment(CommentRequestDTO commentRequestDTO) {
		Comment existingComment = commentRepository.findById(commentRequestDTO.getId()).orElse(null);
		if (existingComment == null) {
			return false;
		}

		if (!existingComment.getUser().getId().equals(commentRequestDTO.getUserId())) {
			return false;
		}

		Comment entity = commentMapper.convertToComment(commentRequestDTO);
		entity = commentRepository.save(entity);

		return entity.getId() > 0;
	}

	@Override
	public Boolean removeComment(Integer commentId, String userId) {
		Comment existingComment = commentRepository.findById(commentId).orElse(null);
		if (existingComment == null) {
			return false;
		}

		if (!existingComment.getUser().getId().equals(userId)) {
			return false;
		}

		commentRepository.deleteById(commentId);

        return !commentRepository.existsById(commentId);
	}

}