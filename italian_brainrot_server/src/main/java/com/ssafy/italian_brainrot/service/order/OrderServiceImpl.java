package com.ssafy.italian_brainrot.service.order;

import com.ssafy.italian_brainrot.dto.order.OrderDTO;
import com.ssafy.italian_brainrot.dto.order.OrderDetailDTO;
import com.ssafy.italian_brainrot.dto.order.OrderInfoDTO;
import com.ssafy.italian_brainrot.entity.Order;
import com.ssafy.italian_brainrot.entity.OrderDetail;
import com.ssafy.italian_brainrot.entity.Stamp;
import com.ssafy.italian_brainrot.entity.User;
import com.ssafy.italian_brainrot.mapper.OrderMapper;
import com.ssafy.italian_brainrot.repository.OrderRepository;
import com.ssafy.italian_brainrot.repository.StampRepository;
import com.ssafy.italian_brainrot.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final StampRepository stampRepository;
    private final UserRepository userRepository;
    private final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderMapper orderMapper,
                            StampRepository stampRepository,
                            UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.stampRepository = stampRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public Boolean makeOrder(OrderDTO orderDTO) {
        try {
            Order order = orderMapper.convertOrder(orderDTO);

            List<OrderDetail> orderDetailList = new ArrayList<>();
            int stampSum = 0;
            for (OrderDetailDTO dto : orderDTO.getDetails()) {
                OrderDetail orderDetail = orderMapper.convertOrderDetail(dto, order);
                orderDetailList.add(orderDetail);
                stampSum += orderDetail.getQuantity();
            }

            order.setDetails(orderDetailList);
            order = orderRepository.save(order);

            Stamp stamp = Stamp.builder()
                    .order(order)
                    .user(order.getUser())
                    .quantity(stampSum)
                    .build();

            stampRepository.save(stamp);

            User user = order.getUser();
            user.updateStamps(stampSum);
            userRepository.save(user);

            return true;
        } catch (Exception e) {
            log.error("주문 생성 중 오류 발생: ", e);
            return false;
        }
    }

    @Override
    public OrderDTO getOrderDetails(int orderId) {
        return null;
    }

    @Override
    public List<OrderDTO> getOrderByUser(String userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orderMapper.convertOrderDTOList(orders);
    }

    @Override
    public OrderInfoDTO getOrderInfo(int orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);

        if (order == null) {
            return null;
        }

        return orderMapper.convertOrderInfoDTO(order);
    }

    @Override
    public List<OrderDTO> getLastMonthOrder(String userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orderMapper.convertOrderDTOList(orders);
    }

    @Override
    public List<OrderDTO> getLast6MonthOrder(String userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orderMapper.convertOrderDTOList(orders);
    }
}
