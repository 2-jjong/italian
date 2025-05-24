package com.ssafy.italian_brainrot.service.order;

import com.ssafy.italian_brainrot.dto.order.OrderDTO;
import com.ssafy.italian_brainrot.dto.order.OrderDetailDTO;
import com.ssafy.italian_brainrot.entity.*;
import com.ssafy.italian_brainrot.mapper.OrderMapper;
import com.ssafy.italian_brainrot.repository.*;
import com.ssafy.italian_brainrot.service.inventory.InventoryService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final StampRepository stampRepository;
    private final UserRepository userRepository;
    private final InventoryService inventoryService; // InventoryService 사용
    private final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderMapper orderMapper,
                            StampRepository stampRepository,
                            UserRepository userRepository,
                            InventoryService inventoryService) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.stampRepository = stampRepository;
        this.userRepository = userRepository;
        this.inventoryService = inventoryService;
    }

    @Transactional
    @Override
    public Boolean insertOrder(OrderDTO orderDTO) {
        try {
            // 1. 사용자 조회
            User user = userRepository.findById(orderDTO.getUserId()).orElse(null);
            if (user == null) {
                log.warn("주문 실패: 사용자를 찾을 수 없음 - userId: {}", orderDTO.getUserId());
                return false;
            }

            // 2. 포인트 확인
            if (user.getPoint() < orderDTO.getTotalPrice()) {
                log.warn("주문 실패: 포인트 부족 - userId: {}, 보유: {}, 필요: {}",
                        orderDTO.getUserId(), user.getPoint(), orderDTO.getTotalPrice());
                return false;
            }

            // 3. 포인트 차감
            user.setPoint(user.getPoint() - orderDTO.getTotalPrice());

            // 4. Order 생성
            Order order = orderMapper.convertToOrder(orderDTO);

            // 5. OrderDetail 생성 및 인벤토리 추가
            List<OrderDetail> orderDetailList = new ArrayList<>();
            int stampSum = 0;

            for (OrderDetailDTO dto : orderDTO.getDetails()) {
                OrderDetail orderDetail = orderMapper.convertToOrderDetail(dto, order);
                orderDetailList.add(orderDetail);
                stampSum += orderDetail.getQuantity();

                // 6. InventoryService를 통해 인벤토리에 아이템 추가
                boolean isSuccess = inventoryService.InsertItemToInventory(orderDTO.getUserId(), dto.getProductId(), dto.getQuantity(), dto.getType());
                if(!isSuccess){
                    return false;
                }
            }

            order.setDetails(orderDetailList);
            order = orderRepository.save(order);

            // 7. Stamp 생성
            Stamp stamp = Stamp.builder()
                    .order(order)
                    .user(user)
                    .quantity(stampSum)
                    .build();
            stampRepository.save(stamp);

            // 8. 사용자 스탬프 업데이트
            user.updateStamps(stampSum);
            userRepository.save(user);

            log.debug("주문 생성 성공 - userId: {}, orderId: {}, totalPrice: {}, stamps: {}",
                    orderDTO.getUserId(), order.getId(), orderDTO.getTotalPrice(), stampSum);

            return true;
        } catch (Exception e) {
            log.error("주문 생성 중 오류 발생: ", e);
            return false;
        }
    }

    @Override
    public List<OrderDTO> getOrderHistory(String userId, Integer recentMonths) {
        List<Order> orders;

        if (recentMonths == null) {
            orders = orderRepository.findByUserIdOrderByTimeStampDesc(userId);
            log.debug("전체 주문 내역 조회 - userId: {}, 결과: {}건", userId, orders.size());
        } else {
            LocalDateTime startDate = LocalDateTime.now().minusMonths(recentMonths);
            orders = orderRepository.findByUserIdAndRecentMonths(userId, startDate);
            log.debug("{}개월 주문 내역 조회 - userId: {}, 결과: {}건", recentMonths, userId, orders.size());
        }

        return orderMapper.convertToOrderDTOList(orders);
    }
}