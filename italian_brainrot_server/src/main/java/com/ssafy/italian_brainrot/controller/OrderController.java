package com.ssafy.italian_brainrot.controller;

import com.ssafy.italian_brainrot.dto.order.OrderDTO;
import com.ssafy.italian_brainrot.service.order.OrderService;
import com.ssafy.italian_brainrot.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    public ResponseEntity<Integer> makeOrder(@RequestBody OrderDTO orderDTO, HttpServletRequest request) {
        String userId = cookieUtil.getUserIdFromRequest(request);
        orderDTO.setUserId(userId);

        if (orderDTO.getDetails() == null || orderDTO.getDetails().isEmpty()) {
            log.warn("주문 실패: 주문 상세가 없음 - userId: {}", userId);
            return ResponseEntity.badRequest().body(-1);
        }

        if (orderDTO.getDetails().size() > 5) {
            log.warn("주문 실패: 주문 상세 개수 초과 - userId: {}, count: {}", userId, orderDTO.getDetails().size());
            return ResponseEntity.badRequest().body(-2);
        }

        // 수량 유효성 검사
        for (var detail : orderDTO.getDetails()) {
            if (detail.getQuantity() < 0 || detail.getQuantity() > 10) {
                log.warn("주문 실패: 잘못된 수량 - userId: {}, quantity: {}", userId, detail.getQuantity());
                return ResponseEntity.badRequest().body(-3);
            }
        }

        // 주문 처리
        Boolean success = orderService.makeOrder(orderDTO);

        if (success) {
            log.debug("주문 성공 - userId: {}, totalPrice: {}", userId, orderDTO.getTotalPrice());
            return ResponseEntity.ok(orderDTO.getId());
        } else {
            log.warn("주문 실패 - userId: {}, totalPrice: {}", userId, orderDTO.getTotalPrice());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(-4);
        }
    }

    /**
     * 주문 내역 조회
     * GET /order/history?recentMonths={Int}
     */
    @GetMapping("/history")
    public ResponseEntity<List<OrderDTO>> getOrderHistory(
            @RequestParam(value = "recentMonths", required = false) Integer recentMonths,
            HttpServletRequest request) {

        // Interceptor에서 이미 인증 체크했으므로 userId 추출
        String userId = cookieUtil.getUserIdFromRequest(request);

        List<OrderDTO> orderHistory = orderService.getOrderHistory(userId, recentMonths);

        String period = recentMonths != null ? recentMonths + "개월" : "전체";
        log.debug("주문 내역 조회 완료 - userId: {}, 기간: {}, 결과: {}건", userId, period, orderHistory.size());

        return ResponseEntity.ok(orderHistory);
    }
}