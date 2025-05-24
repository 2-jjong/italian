package com.ssafy.italian_brainrot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.italian_brainrot.dto.comment.CommentDTO;
import com.ssafy.italian_brainrot.service.comment.CommentService;
import com.ssafy.italian_brainrot.util.CookieUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/comment")
public class CommentController {
	private static final Logger log = LoggerFactory.getLogger(CommentController.class);

	private final CommentService commentService;
	private final CookieUtil cookieUtil;

	public CommentController(CommentService commentService, CookieUtil cookieUtil) {
		this.commentService = commentService;
		this.cookieUtil = cookieUtil;
	}

	/**
	 * 댓글 등록
	 * POST /comment
	 */
	@PostMapping
	public ResponseEntity<Boolean> insertComment(@RequestBody CommentDTO comment,
												 HttpServletRequest request) {
		String userId = cookieUtil.getUserIdFromRequest(request);
		comment.setUserId(userId);

		CommentDTO dto = commentService.addComment(comment);
		boolean isSuccess = (dto != null && dto.getId() > 0);

		if (isSuccess) {
			log.debug("댓글 등록 성공: userId={}, productId={}", userId, comment.getProductId());
			return ResponseEntity.ok(true);
		} else {
			log.warn("댓글 등록 실패: userId={}, productId={}", userId, comment.getProductId());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
		}
	}

	/**
	 * 댓글 수정
	 * PUT /comment
	 */
	@PutMapping
	public ResponseEntity<Boolean> updateComment(@RequestBody CommentDTO comment,
												 HttpServletRequest request) {
		String userId = cookieUtil.getUserIdFromRequest(request);
		comment.setUserId(userId);

		CommentDTO dto = commentService.updateComment(comment);

		if (dto != null && dto.getId() > 0) {
			log.debug("댓글 수정 성공: userId={}, commentId={}", userId, comment.getId());
			return ResponseEntity.ok(true);
		} else {
			log.warn("댓글 수정 실패 또는 권한 없음: userId={}, commentId={}", userId, comment.getId());
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(false);
		}
	}

	/**
	 * 댓글 삭제
	 * DELETE /comment/{commentId}
	 */
	@DeleteMapping("/{commentId}")
	public ResponseEntity<Boolean> deleteComment(@PathVariable("commentId") String commentId,
												 HttpServletRequest request) {
		String userId = cookieUtil.getUserIdFromRequest(request);

		int id = Integer.parseInt(commentId);
		log.debug("댓글 삭제 요청: userId={}, commentId={}", userId, id);

		boolean isSuccess = commentService.removeComment(id, userId);

		if (isSuccess) {
			log.debug("댓글 삭제 성공: userId={}, commentId={}", userId, id);
			return ResponseEntity.ok(true);
		} else {
			log.warn("댓글 삭제 실패 또는 권한 없음: userId={}, commentId={}", userId, id);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(false);
		}
	}
}