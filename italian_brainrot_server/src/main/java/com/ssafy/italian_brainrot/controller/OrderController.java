package com.ssafy.italian_brainrot.controller;

import com.ssafy.italian_brainrot.dto.order.OrderDTO;
import com.ssafy.italian_brainrot.dto.order.OrderDetailDTO;
import com.ssafy.italian_brainrot.dto.order.OrderInfoDTO;
import com.ssafy.italian_brainrot.service.order.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public int makeOrder(@RequestBody OrderDTO orderDTO) {
        if (orderDTO.getDetails() == null || orderDTO.getDetails().isEmpty()) {
            return -1;
        }

        if(orderDTO.getDetails().size() > 5)
            return -2;

        for (OrderDetailDTO detailDTO : orderDTO.getDetails()) {
            if (detailDTO.getQuantity() < 0 || detailDTO.getQuantity() > 10)
                return -3;
        }

        orderService.makeOrder(orderDTO);

        return orderDTO.getId();
    }

    @GetMapping("/{orderId}")
    public OrderInfoDTO getOrderInfo(@PathVariable int orderId) {
        return orderService.getOrderInfo(orderId);
    }

    @GetMapping("/byUser")
    public List<OrderDTO> getLastMonthOrder(String id) {
        return orderService.getLastMonthOrder(id);
    }

    @GetMapping("/byUserIn6Months")
    public List<OrderDTO> getLast6MonthOrder(String id) {
        return orderService.getLast6MonthOrder(id);
    }

}
