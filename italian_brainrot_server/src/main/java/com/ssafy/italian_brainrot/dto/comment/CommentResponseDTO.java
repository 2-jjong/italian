package com.ssafy.italian_brainrot.dto.comment;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDTO {
    private int id;
    private String userId;
    private String userName;
    private int productId;
    private double rating;
    private String comment;
}
