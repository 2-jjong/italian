package com.ssafy.italian_brainrot.dto.order;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private int id;
    private String userId;
    private List<OrderDetailDTO> details;
    private int totalPrice;
    private LocalDateTime timeStamp;
}