package com.ssafy.italian_brainrot.service.comment;

import com.ssafy.italian_brainrot.dto.comment.CommentRequestDTO;

public interface CommentService {

	Boolean insertComment(CommentRequestDTO commentRequestDTO);

	Boolean updateComment(CommentRequestDTO commentRequestDTO);

	Boolean removeComment(Integer commentId, String userId);

}