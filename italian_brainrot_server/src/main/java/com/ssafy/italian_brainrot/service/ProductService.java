package com.ssafy.italian_brainrot.service;

import com.ssafy.italian_brainrot.dto.ProductDTO;
import com.ssafy.italian_brainrot.dto.ProductWithCommentDTO;

import java.util.List;


public interface ProductService {
    List<ProductDTO> getProductList();

    ProductWithCommentDTO selectWithComment(Integer productId);
}
