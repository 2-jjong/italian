package com.ssafy.italian_brainrot.service.product;


import com.ssafy.italian_brainrot.dto.product.ProductDTO;
import com.ssafy.italian_brainrot.dto.product.ProductWithCommentDTO;
import com.ssafy.italian_brainrot.entity.Comment;
import com.ssafy.italian_brainrot.entity.OrderDetail;
import com.ssafy.italian_brainrot.entity.Product;
import com.ssafy.italian_brainrot.mapper.ProductMapper;
import com.ssafy.italian_brainrot.repository.CommentRepository;
import com.ssafy.italian_brainrot.repository.OrderDetailRepository;
import com.ssafy.italian_brainrot.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<ProductDTO> getProductList() {
        List<Product> entityList = productRepository.findAll();
        List<ProductDTO> dtoList = entityList.stream().map((product) -> productMapper.convert(product)).toList();
        return dtoList;
    }

    @Override
    public ProductWithCommentDTO selectWithComment(Integer productId) {
        Product product = productRepository.findById(productId).orElseThrow();
        List<Comment> commentList = commentRepository.findByProductId(productId);
        int totalCommentCount = commentList.size();
        double sumOfRating = commentList.stream().map(Comment::getRating).reduce(0.0, Double::sum);
        List<OrderDetail> orderDetailList = orderDetailRepository.findAllByProduct_Id(productId);
        int totalSells = orderDetailList.stream().map(OrderDetail::getQuantity).reduce(0, Integer::sum);
        double averageStars = 0.0;
        if (totalCommentCount > 0) {
            averageStars = sumOfRating / totalCommentCount;
        }
        ProductWithCommentDTO dto = ProductWithCommentDTO
                .builder()
                .id(product.getId())
                .name(product.getName())
                .type(product.getType())
                .price(product.getPrice())
                .img(product.getImg())
                .commentCount(totalCommentCount)
                .totalSells(totalSells)
                .averageStars(averageStars)
                .build();
        return dto;
    }
}
