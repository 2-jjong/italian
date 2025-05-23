package com.ssafy.italian_brainrot.dto;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
	private int id;
	private String userId;
	private int productId;
	private double rating;
	private String comment;
}