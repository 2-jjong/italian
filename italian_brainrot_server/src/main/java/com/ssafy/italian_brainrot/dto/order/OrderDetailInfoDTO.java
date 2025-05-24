package com.ssafy.italian_brainrot.dto.order;

import com.ssafy.italian_brainrot.enumerate.InventoryItemType;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailInfoDTO {
    private int id;
    private int orderId;
    private int productId;
    private int quantity;

    private String img; // 상품이미지
    private String name;// 상품명
    private InventoryItemType type; // 상품 종류
    private int unitPrice; // 상품가격
    private int sumPrice; // quantity * 상품가격
}
