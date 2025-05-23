package com.ssafy.italian_brainrot.dto;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentInfoDTO {
    private int id;
    private String userId;
    private int productId;
    private double rating;
    private String comment;
    private String userName;
}
