package com.ssafy.italian_brainrot.service.order;

import com.ssafy.italian_brainrot.dto.order.OrderDTO;

import java.util.List;

public interface OrderService {

    /**
     * 새로운 Order를 생성한다.
     * - 사용자 포인트 확인 및 차감
     * - Order와 OrderDetail 정보 추가
     * - 인벤토리에 아이템 추가
     * - User 테이블에 사용자의 Stamp 개수 업데이트
     * - Stamp 테이블에 Stamp 이력 추가
     *
     * @param orderDTO 주문 정보 (userId 포함)
     * @return 주문 생성 성공 여부
     */
    Boolean makeOrder(OrderDTO orderDTO);

    /**
     * 사용자의 주문 내역을 기간별로 조회한다.
     *
     * @param userId 사용자 ID
     * @param recentMonths 조회 기간 (개월), null이면 전체 조회
     * @return 주문 내역 목록 (주문번호 내림차순)
     */
    List<OrderDTO> getOrderHistory(String userId, Integer recentMonths);
}