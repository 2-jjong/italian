package com.ssafy.italian_brainrot.mapper;

import com.ssafy.italian_brainrot.dto.order.OrderDTO;
import com.ssafy.italian_brainrot.dto.order.OrderDetailDTO;
import com.ssafy.italian_brainrot.dto.order.OrderDetailInfoDTO;
import com.ssafy.italian_brainrot.dto.order.OrderInfoDTO;
import com.ssafy.italian_brainrot.entity.Order;
import com.ssafy.italian_brainrot.entity.OrderDetail;
import com.ssafy.italian_brainrot.entity.Product;
import com.ssafy.italian_brainrot.entity.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderMapper {

    public Order convertOrder(OrderDTO orderDTO) {
        return Order.builder()
                .user(User.builder().id(orderDTO.getUserId()).build())
                .build();
    }

    public OrderDetail convertOrderDetail(OrderDetailDTO orderDetailDTO, Order order) {
        return OrderDetail.builder()
                .order(order)
                .product(Product.builder().id(orderDetailDTO.getProductId()).build())
                .quantity(orderDetailDTO.getQuantity())
                .build();
    }

    public OrderDTO convertOrderDTO(Order order) {
        return OrderDTO.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .details(convertOrderDetailDTOList(order.getDetails()))
                .build();
    }

    public OrderInfoDTO convertOrderInfoDTO(Order order) {
        return OrderInfoDTO.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .details(convertOrderDetailInfoDTOList(order.getDetails()))
                .build();
    }

    public List<OrderDTO> convertOrderDTOList(List<Order> orders) {
        List<OrderDTO> orderDTOList = new ArrayList<>();
        for(Order order : orders) {
            OrderDTO dto = convertOrderDTO(order);
            orderDTOList.add(dto);
        }

        return orderDTOList;
    }

    public List<OrderInfoDTO> convertOrderInfoDTOList(List<Order> orders) {
        List<OrderInfoDTO> orderInfoDTOList = new ArrayList<>();
        for(Order order : orders) {
            OrderInfoDTO dto = convertOrderInfoDTO(order);
            orderInfoDTOList.add(dto);
        }

        return orderInfoDTOList;
    }

    private List<OrderDetailDTO> convertOrderDetailDTOList(List<OrderDetail> orderDetailList) {
        List<OrderDetailDTO> orderDetailDTOList = new ArrayList<>();
        for (OrderDetail orderDetail : orderDetailList) {
            OrderDetailDTO dto = OrderDetailDTO.builder()
                    .id((int) orderDetail.getId())
                    .orderId(orderDetail.getOrder().getId())
                    .productId(orderDetail.getProduct().getId())
                    .quantity(orderDetail.getQuantity())
                    .build();
            orderDetailDTOList.add(dto);
        }

        return orderDetailDTOList;
    }

    private List<OrderDetailInfoDTO> convertOrderDetailInfoDTOList(List<OrderDetail> orderDetailList) {
        List<OrderDetailInfoDTO> orderDetailInfoDTOList = new ArrayList<>();
        for (OrderDetail orderDetail : orderDetailList) {
            OrderDetailInfoDTO dto = OrderDetailInfoDTO.builder()
                    .id((int) orderDetail.getId())
                    .orderId(orderDetail.getOrder().getId())
                    .productId(orderDetail.getProduct().getId())
                    .quantity(orderDetail.getQuantity())
                    .img(orderDetail.getProduct().getImg())
                    .name(orderDetail.getProduct().getName())
                    .type(orderDetail.getProduct().getType())
                    .unitPrice(orderDetail.getProduct().getPrice())
                    .sumPrice(orderDetail.getProduct().getPrice() * orderDetail.getQuantity())
                    .build();
            orderDetailInfoDTOList.add(dto);
        }

        return orderDetailInfoDTOList;
    }

}
