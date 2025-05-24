package com.ssafy.italian_brainrot.controller;

import com.ssafy.italian_brainrot.dto.product.ProductDTO;
import com.ssafy.italian_brainrot.dto.product.ProductWithCommentDTO;
import com.ssafy.italian_brainrot.service.product.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("")
    public List<ProductDTO> getProductlist() {
        return productService.getProductList();
    }

    @GetMapping("/{productId}")
    public ProductWithCommentDTO getProductWithComment(@PathVariable("productId") int productId) {
        return productService.getProductWithComment(productId);
    }
}
