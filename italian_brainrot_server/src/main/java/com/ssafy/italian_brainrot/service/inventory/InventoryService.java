package com.ssafy.italian_brainrot.service.inventory;

import com.ssafy.italian_brainrot.dto.inventory.InventoryDTO;
import com.ssafy.italian_brainrot.enumerate.InventoryItemType;

import java.util.List;

public interface InventoryService {

    /**
     * 사용자의 인벤토리 아이템 전체 조회 (id 순서로 정렬)
     *
     * @param userId 사용자 ID
     * @return 인벤토리 아이템 목록
     */
    List<InventoryDTO> getInventoryItemList(String userId);

    /**
     * 인벤토리에 아이템 추가 (주문 후 사용)
     *
     * @param userId 사용자 ID
     * @param productId 상품 ID
     * @param quantity 수량
     * @param type 아이템 타입
     */
    void addItemToInventory(String userId, int productId, int quantity, InventoryItemType type);

    /**
     * 인벤토리 아이템 수량 변경
     *
     * @param userId 사용자 ID
     * @param cardId 카드 ID
     * @param quantity 변경할 수량 (음수면 감소)
     * @return 변경 성공 여부
     */
    boolean updateItemQuantity(String userId, int cardId, int quantity);

    /**
     * 인벤토리 아이템 삭제
     *
     * @param userId 사용자 ID
     * @param cardId 카드 ID
     * @return 삭제 성공 여부
     */
    boolean removeItem(String userId, int cardId);

    /**
     * 카드팩 개봉 (내부 로직)
     * - 재료 카드팩: 10장 랜덤, 1장 캐릭터 카드 확률
     * - 캐릭터 카드팩: 1장 랜덤
     *
     * @param userId 사용자 ID
     * @param packId 카드팩 ID
     * @param type 카드팩 타입
     */
    void openCardPack(String userId, int packId, InventoryItemType type);
}