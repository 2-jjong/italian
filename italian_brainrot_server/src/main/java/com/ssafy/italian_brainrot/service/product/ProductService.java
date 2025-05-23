package com.ssafy.italian_brainrot.service.product;

import com.ssafy.italian_brainrot.dto.product.ProductDTO;
import com.ssafy.italian_brainrot.dto.product.ProductWithCommentDTO;

import java.util.List;


public interface ProductService {
    List<ProductDTO> getProductList();

    ProductWithCommentDTO selectWithComment(Integer productId);
}
