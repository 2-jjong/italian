package com.ssafy.italian_brainrot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.italian_brainrot.dto.comment.CommentRequestDTO;
import com.ssafy.italian_brainrot.service.comment.CommentService;
import com.ssafy.italian_brainrot.util.CookieUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/comment")
public class CommentController {
	private final CommentService commentService;
	private final CookieUtil cookieUtil;
	private final Logger log = LoggerFactory.getLogger(CommentController.class);

	public CommentController(CommentService commentService, CookieUtil cookieUtil) {
		this.commentService = commentService;
		this.cookieUtil = cookieUtil;
	}

	@PostMapping("")
	public Boolean insertComment(@RequestBody CommentRequestDTO comment,
												 HttpServletRequest request) {
		String userId = cookieUtil.getUserIdFromRequest(request);
		comment.setUserId(userId);

		return commentService.insertComment(comment);
    }

	@PutMapping("")
	public Boolean updateComment(@RequestBody CommentRequestDTO comment,
												 HttpServletRequest request) {
		String userId = cookieUtil.getUserIdFromRequest(request);
		comment.setUserId(userId);

		return commentService.updateComment(comment);
	}

	@DeleteMapping("/{commentId}")
	public Boolean deleteComment(@PathVariable("commentId") int commentId,
												 HttpServletRequest request) {
		String userId = cookieUtil.getUserIdFromRequest(request);

		return commentService.removeComment(commentId, userId);
	}
}