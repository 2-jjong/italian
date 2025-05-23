package com.ssafy.italian_brainrot.dto.product;

import com.ssafy.italian_brainrot.dto.CommentInfoDTO;
import com.ssafy.italian_brainrot.enumerate.InventoryItemType;
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
    private InventoryItemType type;
    private int price;
    private String img;

    private int commentCount;
    private int totalSells;
    private double averageStars;

    private List<CommentInfoDTO> comments;
}
