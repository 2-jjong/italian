package com.ssafy.italian_brainrot.service.comment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ssafy.italian_brainrot.dto.comment.CommentDTO;
import com.ssafy.italian_brainrot.entity.Comment;
import com.ssafy.italian_brainrot.mapper.CommentMapper;
import com.ssafy.italian_brainrot.repository.CommentRepository;

@Service
public class CommentServiceImpl implements CommentService {

	private static final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);

	@Autowired
	private CommentRepository repository;

	@Autowired
	private CommentMapper commentMapper;

	@Override
	public CommentDTO addComment(CommentDTO comment) {
		Comment newEntity = commentMapper.convertToComment(comment);
		Comment entity = repository.save(newEntity);
		CommentDTO dto = commentMapper.convertToCommentDTO(entity);

		logger.debug("댓글 등록 성공: userId={}, productId={}, commentId={}",
				comment.getUserId(), comment.getProductId(), dto.getId());
		return dto;
	}

	@Override
	public CommentDTO updateComment(CommentDTO comment) {
		// 기존 댓글 조회
		Comment existingComment = repository.findById(comment.getId()).orElse(null);
		if (existingComment == null) {
			logger.warn("댓글 수정 실패: 댓글을 찾을 수 없음 - commentId={}", comment.getId());
			return null;
		}

		// 권한 체크: 작성자 본인만 수정 가능
		if (!existingComment.getUserId().equals(comment.getUserId())) {
			logger.warn("댓글 수정 권한 없음: userId={}, commentUserId={}, commentId={}",
					comment.getUserId(), existingComment.getUserId(), comment.getId());
			return null;
		}

		Comment updatedEntity = commentMapper.convertToComment(comment);
		Comment entity = repository.save(updatedEntity);
		CommentDTO dto = commentMapper.convertToCommentDTO(entity);

		logger.debug("댓글 수정 성공: userId={}, commentId={}", comment.getUserId(), dto.getId());
		return dto;
	}

	@Override
	public boolean removeComment(Integer commentId, String userId) {
		// 기존 댓글 조회
		Comment existingComment = repository.findById(commentId).orElse(null);
		if (existingComment == null) {
			logger.warn("댓글 삭제 실패: 댓글을 찾을 수 없음 - commentId={}", commentId);
			return false;
		}

		// 권한 체크: 작성자 본인만 삭제 가능
		if (!existingComment.getUserId().equals(userId)) {
			logger.warn("댓글 삭제 권한 없음: userId={}, commentUserId={}, commentId={}",
					userId, existingComment.getUserId(), commentId);
			return false;
		}

		repository.deleteById(commentId);
		boolean isSuccess = !repository.existsById(commentId);

		if (isSuccess) {
			logger.debug("댓글 삭제 성공: userId={}, commentId={}", userId, commentId);
		} else {
			logger.error("댓글 삭제 실패: userId={}, commentId={}", userId, commentId);
		}

		return isSuccess;
	}

	@Override
	public CommentDTO selectComment(Integer id) {
		Comment entity = repository.findById(id).orElse(null);
		if (entity == null) {
			return null;
		}
		return commentMapper.convertToCommentDTO(entity);
	}
}