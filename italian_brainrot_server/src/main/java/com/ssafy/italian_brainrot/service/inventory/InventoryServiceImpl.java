package com.ssafy.italian_brainrot.service.inventory;

import com.ssafy.italian_brainrot.dto.inventory.InventoryDTO;
import com.ssafy.italian_brainrot.entity.*;
import com.ssafy.italian_brainrot.enumerate.InventoryItemType;
import com.ssafy.italian_brainrot.mapper.InventoryMapper;
import com.ssafy.italian_brainrot.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class InventoryServiceImpl implements InventoryService {

    private static final Logger logger = LoggerFactory.getLogger(InventoryServiceImpl.class);

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private InventoryMapper inventoryMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ResourceCardRepository resourceCardRepository;

    @Autowired
    private CharacterCardRepository characterCardRepository;

    private final Random random = new Random();

    @Override
    public List<InventoryDTO> getInventoryItemList(String userId) {
        // id 순서로 정렬하여 조회 (1~ 재료, 1001~ 캐릭터, 2001~ 카드팩)
        List<Inventory> inventoryList = inventoryRepository.findByUserIdOrderByCardIdAsc(userId);
        List<InventoryDTO> dtoList = inventoryList.stream()
                .map(inventory -> inventoryMapper.convert(inventory))
                .toList();

        logger.debug("인벤토리 조회 완료: userId={}, 아이템 수={}", userId, dtoList.size());
        return dtoList;
    }

    @Override
    @Transactional
    public void addItemToInventory(String userId, int productId, int quantity, InventoryItemType type) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                logger.warn("인벤토리 추가 실패: 사용자를 찾을 수 없음 - userId={}", userId);
                return;
            }

            // 카드팩인 경우 개봉 처리
            if (type == InventoryItemType.PACK) {
                for (int i = 0; i < quantity; i++) {
                    openCardPack(userId, productId, type);
                }
                return;
            }

            // 기존 인벤토리 아이템 확인
            Optional<Inventory> existingInventory = inventoryRepository
                    .findByUserIdAndCardId(userId, productId);

            if (existingInventory.isPresent()) {
                // 기존 아이템 수량 증가
                Inventory inventory = existingInventory.get();
                inventory.setQuantity(inventory.getQuantity() + quantity);
                inventoryRepository.save(inventory);
                logger.debug("인벤토리 수량 증가: userId={}, cardId={}, 기존={}, 추가={}",
                        userId, productId, inventory.getQuantity() - quantity, quantity);
            } else {
                // 새 인벤토리 아이템 생성
                Card card = getCardById(productId, type);
                if (card == null) {
                    logger.warn("인벤토리 추가 실패: 카드를 찾을 수 없음 - cardId={}, type={}", productId, type);
                    return;
                }

                Inventory newInventory = Inventory.builder()
                        .user(user)
                        .type(type)
                        .quantity(quantity)
                        .card(card)
                        .build();

                inventoryRepository.save(newInventory);
                logger.debug("새 인벤토리 아이템 생성: userId={}, cardId={}, quantity={}, type={}",
                        userId, productId, quantity, type);
            }

        } catch (Exception e) {
            logger.error("인벤토리 추가 중 오류 발생: userId={}, productId={}, quantity={}, type={}",
                    userId, productId, quantity, type, e);
        }
    }

    @Override
    @Transactional
    public boolean updateItemQuantity(String userId, int cardId, int quantity) {
        try {
            Optional<Inventory> inventoryOpt = inventoryRepository.findByUserIdAndCardId(userId, cardId);
            if (inventoryOpt.isEmpty()) {
                logger.warn("인벤토리 수량 변경 실패: 아이템을 찾을 수 없음 - userId={}, cardId={}", userId, cardId);
                return false;
            }

            Inventory inventory = inventoryOpt.get();
            int newQuantity = inventory.getQuantity() + quantity;

            if (newQuantity <= 0) {
                // 수량이 0 이하면 아이템 삭제
                inventoryRepository.delete(inventory);
                logger.debug("인벤토리 아이템 삭제: userId={}, cardId={}, 최종 수량={}", userId, cardId, newQuantity);
            } else {
                // 수량 업데이트
                inventory.setQuantity(newQuantity);
                inventoryRepository.save(inventory);
                logger.debug("인벤토리 수량 변경: userId={}, cardId={}, 기존={}, 변경={}, 최종={}",
                        userId, cardId, inventory.getQuantity() - quantity, quantity, newQuantity);
            }

            return true;

        } catch (Exception e) {
            logger.error("인벤토리 수량 변경 중 오류 발생: userId={}, cardId={}, quantity={}",
                    userId, cardId, quantity, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean removeItem(String userId, int cardId) {
        try {
            Optional<Inventory> inventoryOpt = inventoryRepository.findByUserIdAndCardId(userId, cardId);
            if (inventoryOpt.isEmpty()) {
                logger.warn("인벤토리 삭제 실패: 아이템을 찾을 수 없음 - userId={}, cardId={}", userId, cardId);
                return false;
            }

            inventoryRepository.delete(inventoryOpt.get());
            logger.debug("인벤토리 아이템 삭제 완료: userId={}, cardId={}", userId, cardId);
            return true;

        } catch (Exception e) {
            logger.error("인벤토리 삭제 중 오류 발생: userId={}, cardId={}", userId, cardId, e);
            return false;
        }
    }

    @Override
    @Transactional
    public void openCardPack(String userId, int packId, InventoryItemType type) {
        try {
            if (type == InventoryItemType.PACK) {
                // 재료 카드팩: 10장 랜덤 + 1장 캐릭터 카드 확률
                if (packId >= 2001) { // 재료 카드팩으로 가정
                    openResourceCardPack(userId);
                } else {
                    // 캐릭터 카드팩: 1장 랜덤
                    openCharacterCardPack(userId);
                }
            }

        } catch (Exception e) {
            logger.error("카드팩 개봉 중 오류 발생: userId={}, packId={}, type={}", userId, packId, type, e);
        }
    }

    /**
     * 재료 카드팩 개봉 (10장 랜덤 + 캐릭터 카드 확률)
     */
    private void openResourceCardPack(String userId) {
        // 10장 재료 카드 랜덤 추가
        for (int i = 0; i < 10; i++) {
            int randomResourceCardId = getRandomResourceCardId();
            addItemToInventory(userId, randomResourceCardId, 1, InventoryItemType.RESOURCE_CARD);
        }

        // 캐릭터 카드 확률 (예: 10% 확률)
        if (random.nextInt(100) < 10) {
            int randomCharacterCardId = getRandomCharacterCardId();
            addItemToInventory(userId, randomCharacterCardId, 1, InventoryItemType.CHARACTER_CARD);
            logger.debug("보너스 캐릭터 카드 획득: userId={}, cardId={}", userId, randomCharacterCardId);
        }

        logger.debug("재료 카드팩 개봉 완료: userId={}", userId);
    }

    /**
     * 캐릭터 카드팩 개봉 (1장 랜덤)
     */
    private void openCharacterCardPack(String userId) {
        int randomCharacterCardId = getRandomCharacterCardId();
        addItemToInventory(userId, randomCharacterCardId, 1, InventoryItemType.CHARACTER_CARD);
        logger.debug("캐릭터 카드팩 개봉 완료: userId={}, cardId={}", userId, randomCharacterCardId);
    }

    /**
     * 카드 타입에 따라 카드 조회
     */
    private Card getCardById(int cardId, InventoryItemType type) {
        switch (type) {
            case RESOURCE_CARD:
                return resourceCardRepository.findById(cardId).orElse(null);
            case CHARACTER_CARD:
                return characterCardRepository.findById(cardId).orElse(null);
            default:
                return null;
        }
    }

    /**
     * 랜덤 재료 카드 ID 생성 (1~1000)
     */
    private int getRandomResourceCardId() {
        return random.nextInt(1000) + 1;
    }

    /**
     * 랜덤 캐릭터 카드 ID 생성 (1001~2000)
     */
    private int getRandomCharacterCardId() {
        return random.nextInt(1000) + 1001;
    }
}