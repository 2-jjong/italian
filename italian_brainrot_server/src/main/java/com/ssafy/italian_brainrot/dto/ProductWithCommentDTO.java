package com.ssafy.italian_brainrot.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductWithCommentDTO {
    private int id;
    private String name;
    private String type;
    private int price;
    private String img;

    private int commentCount;
    private int totalSells;
    private double averageStars;

    private List<CommentInfoDTO> comments;
}
