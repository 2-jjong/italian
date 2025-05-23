package com.ssafy.italian_brainrot.dto.product;

import com.ssafy.italian_brainrot.enumerate.InventoryItemType;
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
    private InventoryItemType type;
    private int price;
    private String img;
}
