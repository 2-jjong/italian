package com.ssafy.italian_brainrot.mapper;

import com.ssafy.italian_brainrot.dto.order.OrderDTO;
import com.ssafy.italian_brainrot.dto.order.OrderDetailDTO;
import com.ssafy.italian_brainrot.entity.Order;
import com.ssafy.italian_brainrot.entity.OrderDetail;
import com.ssafy.italian_brainrot.entity.Product;
import com.ssafy.italian_brainrot.entity.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderMapper {
    public Order convertToOrder(OrderDTO orderDTO) {
        return Order.builder()
                .user(User.builder().id(orderDTO.getUserId()).build())
                .totalPrice(orderDTO.getTotalPrice())
                .timeStamp(orderDTO.getTimeStamp())
                .build();
    }

    public OrderDetail convertToOrderDetail(OrderDetailDTO orderDetailDTO, Order order) {
        return OrderDetail.builder()
                .order(order)
                .product(Product.builder().id(orderDetailDTO.getProductId()).build())
                .quantity(orderDetailDTO.getQuantity())
                .build();
    }

    public OrderDTO convertToOrderDTO(Order order) {
        return OrderDTO.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .details(convertToOrderDetailDTOList(order.getDetails()))
                .totalPrice(order.getTotalPrice())
                .timeStamp(order.getTimeStamp())
                .build();
    }

    public List<OrderDTO> convertToOrderDTOList(List<Order> orders) {
        List<OrderDTO> orderDTOList = new ArrayList<>();
        for(Order order : orders) {
            OrderDTO dto = convertToOrderDTO(order);
            orderDTOList.add(dto);
        }
        return orderDTOList;
    }

    private List<OrderDetailDTO> convertToOrderDetailDTOList(List<OrderDetail> orderDetailList) {
        List<OrderDetailDTO> orderDetailDTOList = new ArrayList<>();
        for (OrderDetail orderDetail : orderDetailList) {
            OrderDetailDTO dto = OrderDetailDTO.builder()
                    .id((int) orderDetail.getId())
                    .orderId(orderDetail.getOrder().getId())
                    .productId(orderDetail.getProduct().getId())
                    .quantity(orderDetail.getQuantity())
                    .type(orderDetail.getProduct().getType())
                    .build();
            orderDetailDTOList.add(dto);
        }
        return orderDetailDTOList;
    }
}