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

    private final ProductService service;

    public ProductController(ProductService productService) {
        this.service = productService;
    }

    @GetMapping("")
    public List<ProductDTO> productlist() {
        List<ProductDTO> list = service.getProductList();
        return list;
    }

    @GetMapping("/{productId}")
    public ProductWithCommentDTO searchProductDetail(@PathVariable String productId) {
        int id = Integer.parseInt(productId);
        ProductWithCommentDTO productWithComment = service.selectWithComment(id);
        log.debug(productWithComment.toString());
        return productWithComment;
    }
}
