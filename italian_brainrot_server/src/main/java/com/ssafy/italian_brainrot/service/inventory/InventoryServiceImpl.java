package com.ssafy.italian_brainrot.service.inventory;

import com.ssafy.italian_brainrot.dto.card.CharacterCardDTO;
import com.ssafy.italian_brainrot.dto.card.ResourceCardDTO;
import com.ssafy.italian_brainrot.dto.inventory.InventoryDTO;
import com.ssafy.italian_brainrot.dto.inventory.PackOpenResponseDTO;
import com.ssafy.italian_brainrot.entity.*;
import com.ssafy.italian_brainrot.enumerate.InventoryItemType;
import com.ssafy.italian_brainrot.mapper.CardMapper;
import com.ssafy.italian_brainrot.mapper.InventoryMapper;
import com.ssafy.italian_brainrot.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;
    private final UserRepository userRepository;
    private final ResourceCardRepository resourceCardRepository;
    private final CharacterCardRepository characterCardRepository;
    private final CardMapper cardMapper;
    private final Logger log = LoggerFactory.getLogger(InventoryServiceImpl.class);

    private final Random random = new Random();

    public InventoryServiceImpl(InventoryRepository inventoryRepository,
                                InventoryMapper inventoryMapper,
                                UserRepository userRepository,
                                ResourceCardRepository resourceCardRepository,
                                CharacterCardRepository characterCardRepository,
                                CardMapper cardMapper) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryMapper = inventoryMapper;
        this.userRepository = userRepository;
        this.resourceCardRepository = resourceCardRepository;
        this.characterCardRepository = characterCardRepository;
        this.cardMapper = cardMapper;
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
    public PackOpenResponseDTO openCardPack(String userId, int packId) {
        try {
            if (packId == 2001) {
                return openResourceCardPack(userId);
            } else if (packId == 2002) {
                return openCharacterCardPack(userId);
            } else {
                throw new IllegalArgumentException("지원하지 않는 카드팩입니다: " + packId);
            }
        } catch (Exception e) {
            log.error("카드팩 개봉 중 오류 발생: userId={}, packId={}", userId, packId, e);
            return PackOpenResponseDTO.builder()
                    .resourceCards(null)
                    .characterCard(null)
                    .build();
        }
    }

    /**
     * ResourceCard 랜덤팩 개봉 (id=2001)
     * - 기본: ResourceCard 10장
     * - 20% 확률로 CharacterCard 1장 추가 (ResourceCard 9장 + CharacterCard 1장)
     */
    private PackOpenResponseDTO openResourceCardPack(String userId) {
        List<ResourceCardDTO> resourceCards = new ArrayList<>();
        CharacterCardDTO characterCard = null;

        // 20% 확률로 캐릭터 카드 뽑기 여부 결정
        boolean hasCharacterCard = random.nextInt(100) < 20;
        int resourceCardCount = hasCharacterCard ? 9 : 10;

        // ResourceCard 뽑기
        List<ResourceCard> allResourceCards = resourceCardRepository.findAll();
        for (int i = 0; i < resourceCardCount; i++) {
            ResourceCard drawnCard = drawResourceCardByProbability(allResourceCards);
            if (drawnCard != null) {
                // 인벤토리에 추가
                InsertItemToInventory(userId, drawnCard.getId(), 1, InventoryItemType.RESOURCE_CARD);

                // DTO 변환 후 결과에 추가
                ResourceCardDTO dto = cardMapper.convertToResourceCardDTOSimple(drawnCard);
                resourceCards.add(dto);
            }
        }

        // CharacterCard 뽑기 (20% 확률)
        if (hasCharacterCard) {
            CharacterCard drawnCharacterCard = drawCharacterCardForResourcePack();
            if (drawnCharacterCard != null) {
                // 인벤토리에 추가
                InsertItemToInventory(userId, drawnCharacterCard.getId(), 1, InventoryItemType.CHARACTER_CARD);

                // DTO 변환
                characterCard = cardMapper.convertToCharacterCardDTOSimple(drawnCharacterCard);
            }
        }

        return PackOpenResponseDTO.builder()
                .resourceCards(resourceCards)
                .characterCard(characterCard)
                .build();
    }

    /**
     * CharacterCard 랜덤팩 개봉 (id=2002)
     * - CharacterCard 1장만 뽑기
     * - 등급 확률: B(5%), C(20%), D(30%), E(45%)
     */
    private PackOpenResponseDTO openCharacterCardPack(String userId) {
        CharacterCard drawnCard = drawCharacterCardForCharacterPack();
        CharacterCardDTO characterCard = null;

        if (drawnCard != null) {
            // 인벤토리에 추가
            InsertItemToInventory(userId, drawnCard.getId(), 1, InventoryItemType.CHARACTER_CARD);

            // DTO 변환
            characterCard = cardMapper.convertToCharacterCardDTOSimple(drawnCard);
        }

        log.debug("CharacterCard 팩 개봉 완료: userId={}, cardId={}, grade={}",
                userId, drawnCard != null ? drawnCard.getId() : null,
                drawnCard != null ? drawnCard.getGrade() : null);

        return PackOpenResponseDTO.builder()
                .resourceCards(new ArrayList<>())
                .characterCard(characterCard)
                .build();
    }

    /**
     * expectedProbability 기반으로 ResourceCard 뽑기
     */
    private ResourceCard drawResourceCardByProbability(List<ResourceCard> allCards) {
        if (allCards.isEmpty()) return null;

        // 누적 확률 계산
        double totalProbability = allCards.stream()
                .mapToDouble(ResourceCard::getExpectedProbability)
                .sum();

        double randomValue = random.nextDouble() * totalProbability;
        double cumulativeProbability = 0.0;

        for (ResourceCard card : allCards) {
            cumulativeProbability += card.getExpectedProbability();
            if (randomValue <= cumulativeProbability) {
                return card;
            }
        }

        // 예외 상황 - 마지막 카드 반환
        return allCards.get(allCards.size() - 1);
    }

    /**
     * ResourceCard 팩에서 CharacterCard 뽑기 (C:20%, D:30%, E:50%)
     */
    private CharacterCard drawCharacterCardForResourcePack() {
        String selectedGrade = selectGradeForResourcePack();
        return drawCharacterCardByGrade(selectedGrade);
    }

    /**
     * CharacterCard 팩에서 CharacterCard 뽑기 (B:5%, C:20%, D:30%, E:45%)
     */
    private CharacterCard drawCharacterCardForCharacterPack() {
        String selectedGrade = selectGradeForCharacterPack();
        return drawCharacterCardByGrade(selectedGrade);
    }

    /**
     * ResourceCard 팩용 등급 선택 (C:20%, D:30%, E:50%)
     */
    private String selectGradeForResourcePack() {
        int roll = random.nextInt(100);
        if (roll < 20) return "C";
        else if (roll < 50) return "D";
        else return "E";
    }

    /**
     * CharacterCard 팩용 등급 선택 (B:5%, C:20%, D:30%, E:45%)
     */
    private String selectGradeForCharacterPack() {
        int roll = random.nextInt(100);
        if (roll < 5) return "B";
        else if (roll < 25) return "C";
        else if (roll < 55) return "D";
        else return "E";
    }

    /**
     * 지정된 등급의 CharacterCard 중에서 랜덤 선택
     */
    private CharacterCard drawCharacterCardByGrade(String grade) {
        List<CharacterCard> cardsOfGrade = characterCardRepository.findAll().stream()
                .filter(card -> grade.equalsIgnoreCase(card.getGrade()))
                .toList();

        if (cardsOfGrade.isEmpty()) {
            log.warn("등급 {}에 해당하는 캐릭터 카드가 없습니다.", grade);
            return null;
        }

        int randomIndex = random.nextInt(cardsOfGrade.size());
        return cardsOfGrade.get(randomIndex);
    }

    private Card getCardById(int cardId, InventoryItemType type) {
        return switch (type) {
            case RESOURCE_CARD -> resourceCardRepository.findById(cardId).orElse(null);
            case CHARACTER_CARD -> characterCardRepository.findById(cardId).orElse(null);
            default -> null;
        };
    }
}