package com.ssafy.italian_brainrot.dto;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private int id;
    private String name;
    private String type;
    private int price;
    private String img;
}
