package com.ssafy.italian_brainrot.dto.order;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderInfoDTO {
    private int id;
    private String userId;
    private List<OrderDetailInfoDTO> details;
}
