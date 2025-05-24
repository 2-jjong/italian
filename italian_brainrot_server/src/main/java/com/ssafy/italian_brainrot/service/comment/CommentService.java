package com.ssafy.italian_brainrot.service.comment;

import com.ssafy.italian_brainrot.dto.comment.CommentDTO;

public interface CommentService {

	CommentDTO addComment(CommentDTO comment);

	CommentDTO updateComment(CommentDTO comment);

	boolean removeComment(Integer commentId, String userId);

	CommentDTO selectComment(Integer id);
}