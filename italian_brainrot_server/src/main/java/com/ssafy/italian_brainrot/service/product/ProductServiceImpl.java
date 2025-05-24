package com.ssafy.italian_brainrot.service.product;


import com.ssafy.italian_brainrot.dto.comment.CommentResponseDTO;
import com.ssafy.italian_brainrot.dto.product.ProductDTO;
import com.ssafy.italian_brainrot.dto.product.ProductWithCommentDTO;
import com.ssafy.italian_brainrot.entity.Comment;
import com.ssafy.italian_brainrot.entity.OrderDetail;
import com.ssafy.italian_brainrot.entity.Product;
import com.ssafy.italian_brainrot.mapper.CommentMapper;
import com.ssafy.italian_brainrot.mapper.ProductMapper;
import com.ssafy.italian_brainrot.repository.CommentRepository;
import com.ssafy.italian_brainrot.repository.OrderDetailRepository;
import com.ssafy.italian_brainrot.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final CommentRepository commentRepository;

    private final OrderDetailRepository orderDetailRepository;

    private final ProductMapper productMapper;

    private final CommentMapper commentMapper;

    public ProductServiceImpl(ProductRepository productRepository,
                              CommentRepository commentRepository,
                              OrderDetailRepository orderDetailRepository,
                              ProductMapper productMapper,
                              CommentMapper commentMapper) {
        this.productRepository = productRepository;
        this.commentRepository = commentRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.productMapper = productMapper;
        this.commentMapper = commentMapper;
    }

    @Override
    public List<ProductDTO> getProductList() {
        List<Product> entityList = productRepository.findAll();
        return entityList.stream().map(productMapper::convertToProductDTO).toList();
    }

    @Override
    public ProductWithCommentDTO getProductWithComment(Integer productId) {
        Product product = productRepository.findById(productId).orElseThrow();

        List<Comment> commentList = commentRepository.findByProductId(productId);
        int totalCommentCount = commentList.size();
        double sumOfRating = commentList.stream().map(Comment::getRating).reduce(0.0, Double::sum);

        List<OrderDetail> orderDetailList = orderDetailRepository.findAllByProductId(productId);
        int totalSells = orderDetailList.stream().map(OrderDetail::getQuantity).reduce(0, Integer::sum);

        double averageStars = 0.0;
        if (totalCommentCount > 0) {
            averageStars = sumOfRating / totalCommentCount;
        }

        List<CommentResponseDTO> commentInfoList = commentList.stream()
                .map(commentMapper::convertToCommentResponseDTO)
                .collect(Collectors.toList());

        return ProductWithCommentDTO
                .builder()
                .id(product.getId())
                .name(product.getName())
                .type(product.getType())
                .price(product.getPrice())
                .img(product.getImg())
                .commentCount(totalCommentCount)
                .totalSells(totalSells)
                .averageStars(averageStars)
                .comments(commentInfoList)
                .build();
    }
}
