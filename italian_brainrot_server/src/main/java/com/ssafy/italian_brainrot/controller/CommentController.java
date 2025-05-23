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

import com.ssafy.italian_brainrot.dto.comment.CommentDTO;
import com.ssafy.italian_brainrot.service.comment.CommentService;

@RestController
@RequestMapping("/comment")
public class CommentController {
	private static final Logger log = LoggerFactory.getLogger(CommentController.class);

	private final CommentService service;

	public CommentController(CommentService service) {
		this.service = service;
	}

	@PostMapping
	public boolean insertComment(@RequestBody CommentDTO comment) {
		CommentDTO dto = service.addComment(comment);
		return (dto.getId() > 0);
	}

	@PutMapping
	public boolean updateComment(@RequestBody CommentDTO comment) {
		CommentDTO dto = service.updateComment(comment);
		return (dto.getId() > 0);
	}

	@DeleteMapping("/{commentId}")
	public boolean deleteComment(@PathVariable("commentId") String commentId) {
		int id = Integer.parseInt(commentId);
		log.debug(id + "");
		boolean isSuccess = service.removeComment(id);
		return isSuccess;
	}
}
