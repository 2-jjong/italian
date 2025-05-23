package com.ssafy.italian_brainrot.service.comment;

import com.ssafy.italian_brainrot.dto.comment.CommentDTO;

public interface CommentService {

	CommentDTO addComment(CommentDTO comment);

	CommentDTO selectComment(Integer id);

	CommentDTO updateComment(CommentDTO comment);

	boolean removeComment(Integer id);
}
