package com.ssafy.italian_brainrot.controller;

import com.ssafy.italian_brainrot.dto.order.OrderDTO;
import com.ssafy.italian_brainrot.service.order.OrderService;
import com.ssafy.italian_brainrot.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;
    private final CookieUtil cookieUtil;
    private final Logger log = LoggerFactory.getLogger(OrderController.class);

    public OrderController(OrderService orderService, CookieUtil cookieUtil) {
        this.orderService = orderService;
        this.cookieUtil = cookieUtil;
    }

    @PostMapping("")
    public Boolean insertOrder(@RequestBody OrderDTO orderDTO, HttpServletRequest request) {
        String userId = cookieUtil.getUserIdFromRequest(request);
        orderDTO.setUserId(userId);

        return orderService.insertOrder(orderDTO);
    }

    @GetMapping("/history")
    public List<OrderDTO> getOrderHistory(
            @RequestParam(value = "recentMonths", required = false) Integer recentMonths,
            HttpServletRequest request) {
        String userId = cookieUtil.getUserIdFromRequest(request);

        return orderService.getOrderHistory(userId, recentMonths);
    }

}