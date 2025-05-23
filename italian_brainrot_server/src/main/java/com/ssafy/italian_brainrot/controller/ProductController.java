package com.ssafy.italian_brainrot.controller;

import com.ssafy.italian_brainrot.dto.ProductDTO;
import com.ssafy.italian_brainrot.dto.ProductWithCommentDTO;
import com.ssafy.italian_brainrot.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
@CrossOrigin("*")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    private final ProductService service;

    public ProductController(ProductService productService) {
        this.service = productService;
    }

    @GetMapping("")
    @Operation(summary = "전체 상품 목록을 반환한다.")
    public List<ProductDTO> productlist() {
        List<ProductDTO> list = service.getProductList();
        return list;
    }

    @GetMapping("/{productId}")
    @Operation(summary = "{productId}에 해당하는 상품의 정보를 comment와 함께 반환한다.이 기능은 상품의 comment를 조회할 때 사용된다.")
    public ProductWithCommentDTO searchProductDetail(@PathVariable String productId) {
        int id = Integer.parseInt(productId);
        ProductWithCommentDTO productWithComment = service.selectWithComment(id);
        log.debug(productWithComment.toString());
        return productWithComment;
    }
}
