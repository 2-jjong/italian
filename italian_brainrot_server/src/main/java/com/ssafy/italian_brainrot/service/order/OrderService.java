package com.ssafy.italian_brainrot.service.order;

import com.ssafy.italian_brainrot.dto.order.OrderDTO;

import java.util.List;

public interface OrderService {

    Boolean insertOrder(OrderDTO orderDTO);

    List<OrderDTO> getOrderHistory(String userId, Integer recentMonths);

}