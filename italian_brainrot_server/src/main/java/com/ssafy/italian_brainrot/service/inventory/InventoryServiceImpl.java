package com.ssafy.italian_brainrot.service.inventory;

import com.ssafy.italian_brainrot.dto.inventory.InventoryDTO;
import com.ssafy.italian_brainrot.entity.*;
import com.ssafy.italian_brainrot.enumerate.InventoryItemType;
import com.ssafy.italian_brainrot.mapper.InventoryMapper;
import com.ssafy.italian_brainrot.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;
    private final UserRepository userRepository;
    private final ResourceCardRepository resourceCardRepository;
    private final CharacterCardRepository characterCardRepository;
    private final Logger log = LoggerFactory.getLogger(InventoryServiceImpl.class);

    private final Random random = new Random();

    public InventoryServiceImpl(InventoryRepository inventoryRepository,
                                InventoryMapper inventoryMapper,
                                UserRepository userRepository,
                                ResourceCardRepository resourceCardRepository,
                                CharacterCardRepository characterCardRepository) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryMapper = inventoryMapper;
        this.userRepository = userRepository;
        this.resourceCardRepository = resourceCardRepository;
        this.characterCardRepository = characterCardRepository;
    }

    @Override
    public List<InventoryDTO> getInventoryItemList(String userId) {
        // id 순서로 정렬하여 조회 (1~ 재료, 1001~ 캐릭터, 2001~ 카드팩)
        List<Inventory> inventoryList = inventoryRepository.findByUserIdOrderByCardIdAsc(userId);
        return inventoryList.stream()
                .map(inventoryMapper::convertToInventoryDTO)
                .toList();
    }

    @Override
    @Transactional
    public Boolean InsertItemToInventory(String userId, int productId, int quantity, InventoryItemType type) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return false;
            }

            Optional<Inventory> existingInventory = inventoryRepository.findByUserIdAndCardId(userId, productId);

            if (existingInventory.isPresent()) {
                Inventory inventory = existingInventory.get();
                inventory.setQuantity(inventory.getQuantity() + quantity);
                inventoryRepository.save(inventory);
            } else {
                Card card = getCardById(productId, type);
                if (card == null) {
                    return false;
                }

                Inventory entity = Inventory.builder()
                        .user(user)
                        .type(type)
                        .quantity(quantity)
                        .card(card)
                        .build();

                inventoryRepository.save(entity);
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public Boolean updateItemQuantity(String userId, int cardId, int quantity) {
        try {
            Optional<Inventory> inventoryOpt = inventoryRepository.findByUserIdAndCardId(userId, cardId);
            if (inventoryOpt.isEmpty()) {
                return false;
            }

            Inventory inventory = inventoryOpt.get();
            int newQuantity = inventory.getQuantity() + quantity;

            if (newQuantity <= 0) {
                inventoryRepository.delete(inventory);
            } else {
                inventory.setQuantity(newQuantity);
                inventoryRepository.save(inventory);
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public Boolean removeItem(String userId, int cardId) {
        try {
            Optional<Inventory> inventoryOpt = inventoryRepository.findByUserIdAndCardId(userId, cardId);
            if (inventoryOpt.isEmpty()) {
                return false;
            }

            inventoryRepository.delete(inventoryOpt.get());

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public void openCardPack(String userId, int packId, InventoryItemType type) {
        try {
            // TODO: 카드팩 오픈 구현

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
            log.error("카드팩 개봉 중 오류 발생: userId={}, packId={}, type={}", userId, packId, type, e);
        }
    }

    /**
     * 재료 카드팩 개봉 (10장 랜덤 + 캐릭터 카드 확률)
     */
    private void openResourceCardPack(String userId) {
        // 10장 재료 카드 랜덤 추가
        for (int i = 0; i < 10; i++) {
            int randomResourceCardId = getRandomResourceCardId();
            InsertItemToInventory(userId, randomResourceCardId, 1, InventoryItemType.RESOURCE_CARD);
        }

        // 캐릭터 카드 확률 (예: 10% 확률)
        if (random.nextInt(100) < 10) {
            int randomCharacterCardId = getRandomCharacterCardId();
            InsertItemToInventory(userId, randomCharacterCardId, 1, InventoryItemType.CHARACTER_CARD);
            log.debug("보너스 캐릭터 카드 획득: userId={}, cardId={}", userId, randomCharacterCardId);
        }

        log.debug("재료 카드팩 개봉 완료: userId={}", userId);
    }

    /**
     * 캐릭터 카드팩 개봉 (1장 랜덤)
     */
    private void openCharacterCardPack(String userId) {
        int randomCharacterCardId = getRandomCharacterCardId();
        InsertItemToInventory(userId, randomCharacterCardId, 1, InventoryItemType.CHARACTER_CARD);
        log.debug("캐릭터 카드팩 개봉 완료: userId={}, cardId={}", userId, randomCharacterCardId);
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