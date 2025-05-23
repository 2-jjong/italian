package com.ssafy.italian_brainrot.dto.order;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailDTO {
    private int id;
    private int orderId;
    private int productId;
    private int quantity;
}
