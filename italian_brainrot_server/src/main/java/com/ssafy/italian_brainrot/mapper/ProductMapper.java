package com.ssafy.italian_brainrot.mapper;

import com.ssafy.italian_brainrot.dto.product.ProductDTO;
import com.ssafy.italian_brainrot.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public ProductDTO convertToProductDTO(Product entity) {
        return ProductDTO
                .builder()
                .id(entity.getId())
                .name(entity.getName())
                .type(entity.getType())
                .price(entity.getPrice())
                .img(entity.getImg())
                .build();
    }
}
