package com.ssafy.italian_brainrot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ssafy.italian_brainrot.dto.CommentDTO;
import com.ssafy.italian_brainrot.entity.Comment;
import com.ssafy.italian_brainrot.mapper.CommentMapper;
import com.ssafy.italian_brainrot.repository.CommentRepository;

@Service
public class CommentServiceImpl implements CommentService {

	@Autowired
	private CommentRepository repository;

	@Autowired
	private CommentMapper mapper;

	@Override
	public CommentDTO addComment(CommentDTO comment) {
		Comment newEntity = mapper.convert(comment);
		Comment entity = repository.save(newEntity);
		CommentDTO dto = mapper.convert(entity);
		return dto;
	}

	@Override
	public CommentDTO selectComment(Integer id) {
		Comment entity = repository.findById(id).orElseThrow();
		CommentDTO dto = mapper.convert(entity);
		return dto;
	}

	@Override
	public CommentDTO updateComment(CommentDTO comment) {
		Comment updatedEntity = mapper.convert(comment);
		Comment entity = repository.save(updatedEntity);
		CommentDTO dto = mapper.convert(entity);
		return dto;
	}

	@Override
	public boolean removeComment(Integer id) {
		repository.deleteById(id);
		boolean isSuccess = !repository.existsById(id);
		return isSuccess;
	}

}
