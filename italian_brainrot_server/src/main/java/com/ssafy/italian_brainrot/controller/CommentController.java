package com.ssafy.italian_brainrot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.italian_brainrot.dto.CommentDTO;
import com.ssafy.italian_brainrot.service.CommentService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/comment")
@CrossOrigin("*")
public class CommentController {
	private static final Logger log = LoggerFactory.getLogger(CommentController.class);

	private final CommentService service;

	public CommentController(CommentService service) {
		this.service = service;
	}

	@PostMapping
	@Operation(summary = "comment 객체를 추가한다. 성공하면 true를 리턴한다. 평점은 1.0 ~ 5.0만 허용. 벗어면 false를 리턴한다.", description = "id는 자동생성되는 comment의 id이므로 입력해도 무시된다. \r\n"
			+ "아래는 'test' 사용자가 1번 상품에 5점과 comment를 입력한 모습임.\r\n" + "{\r\n" + "  \"comment\": \"It's Good\",\r\n"
			+ "  \"id\": 0,\r\n" + "  \"productId\": 1,\r\n" + "  \"rating\": 5,\r\n" + "  \"userId\": \"id 01\"\r\n"
			+ "}")
	public boolean insertComment(@RequestBody CommentDTO comment) {
		CommentDTO dto = service.addComment(comment);
		return (dto.getId() > 0);
	}

	@PutMapping
	@Operation(summary = "comment 객체를 수정한다. 성공하면 true를 리턴한다. 평점은 1.0 ~ 5.0만 허용. 벗어면 false를 리턴한다.")
	public boolean updateComment(@RequestBody CommentDTO comment) {
		CommentDTO dto = service.updateComment(comment);
		return (dto.getId() > 0);
	}

	@DeleteMapping("/{commentId}")
	@Operation(summary = "{id}에 해당하는 사용자 정보를 삭제한다. 성공하면 true를 리턴한다.")
	public boolean deleteComment(@PathVariable("commentId") String commentId) {
		int id = Integer.parseInt(commentId);
		log.debug(id + "");
		boolean isSuccess = service.removeComment(id);
		return isSuccess;
	}
}
