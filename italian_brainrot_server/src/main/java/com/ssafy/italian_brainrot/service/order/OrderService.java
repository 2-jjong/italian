package com.ssafy.italian_brainrot.service.order;

import com.ssafy.italian_brainrot.dto.order.OrderDTO;
import com.ssafy.italian_brainrot.dto.order.OrderInfoDTO;
import com.ssafy.italian_brainrot.dto.order.OrderDetailDTO;

import java.util.List;

public interface OrderService {

    /**
     * 새로운 Order를 생성한다. Order와 OrderDetail에 정보를 추가한다. [심화]User 테이블에 사용자의 Stamp 개수를
     * 업데이트 한다. [심화]Stamp 테이블에 Stamp 이력을 추가한다.
     *
     * @param orderDTO
     */
    public Boolean makeOrder(OrderDTO orderDTO);

    /**
     * orderId에 대한 Order를 반환한다. 이때 Order에 해당하는 OrderDetail에 대한 내용까지 반환한다.
     * OrderDetail의 내용은 id에 대한 내림차순으로 조회한다.
     *
     * 3 단계에서는 selectOrderTotalInfo를 사용하고, 이 메소드를 사용하지 않음.
     *
     * @param orderId
     * @return
     */
    public OrderDTO getOrderDetails(int orderId);

    /**
     * id에 해당하는 사용자의 Order 목록을 주문 번호의 내림차순으로 반환한다.
     *
     * @param userId
     * @return
     */
    public List<OrderDTO> getOrderByUser(String userId);

    /**
     * orderId에 대한 Order를 반환한다. 이때 Order에 해당하는 OrderDetail에 대한 내용까지 반환한다.
     * OrderDetail의 내용은 detail id의 오름차순으로 조회한다. 토탈금액, 상품명 등의 추가적인 정보가 담긴
     * OrderInfo객체를 리턴한다.
     *
     * @param orderId
     * @return
     */
    public OrderInfoDTO getOrderInfo(int orderId);

    /**
     * 최근 1개월의 주문 내역을 반환한다. 주문번호의 내림차순으로 조회된다. 주문번호로 조회된 order detail은 상세의 detail
     * id의 오름차순으로 조회된다. 관통 6단계에서 추가됨
     *
     * @param userId
     * @return
     */
    List<OrderDTO> getLastMonthOrder(String userId);

    /**
     * 최근 6개월의 주문 내역을 반환한다. 주문번호로 조회된 order detail은 상세의 detail id의 오름차순으로 조회된다. 관통
     * 6단계에서 추가됨
     *
     * @param userId
     * @return
     */
    List<OrderDTO> getLast6MonthOrder(String userId);

}
