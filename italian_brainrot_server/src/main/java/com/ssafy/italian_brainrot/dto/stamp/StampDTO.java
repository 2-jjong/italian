package com.ssafy.italian_brainrot.dto.stamp;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StampDTO {
    private int id;
    private String userId;
    private int orderId;
    private int quantity;
}

