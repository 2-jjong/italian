package com.ssafy.italian_brainrot.service.recipe;

import com.ssafy.italian_brainrot.dto.card.CharacterCardDTO;
import com.ssafy.italian_brainrot.dto.recipe.RecipeResponseDTO;
import com.ssafy.italian_brainrot.entity.*;
import com.ssafy.italian_brainrot.enumerate.InventoryItemType;
import com.ssafy.italian_brainrot.mapper.CardMapper;
import com.ssafy.italian_brainrot.repository.*;
import com.ssafy.italian_brainrot.service.inventory.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class RecipeServiceImpl implements RecipeService {

    private static final Logger logger = LoggerFactory.getLogger(RecipeServiceImpl.class);

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private RecipeIngredientRepository recipeIngredientRepository;

    @Autowired
    private CharacterCardRepository characterCardRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private CardMapper cardMapper;

    private final Random random = new Random();

    @Override
    @Transactional
    public RecipeResponseDTO craftCard(String userId, int recipeId) {
        try {
            // 1. 레시피 조회
            Recipe recipe = recipeRepository.findById(recipeId).orElse(null);
            if (recipe == null) {
                logger.warn("합성 실패: 레시피를 찾을 수 없음 - recipeId={}", recipeId);
                return RecipeResponseDTO.builder()
                        .isSuccess(false)
                        .card(null)
                        .build();
            }

            // 2. 필요한 재료 카드 조회
            List<RecipeIngredient> requiredIngredients = recipeIngredientRepository.findByRecipeId(recipeId);
            if (requiredIngredients.isEmpty()) {
                logger.warn("합성 실패: 레시피 재료 정보가 없음 - recipeId={}", recipeId);
                return RecipeResponseDTO.builder()
                        .isSuccess(false)
                        .card(null)
                        .build();
            }

            // 3. 사용자 인벤토리에서 재료 확인
            if (!hasRequiredMaterials(userId, requiredIngredients)) {
                logger.warn("합성 실패: 필요한 재료 부족 - userId={}, recipeId={}", userId, recipeId);
                return RecipeResponseDTO.builder()
                        .isSuccess(false)
                        .card(null)
                        .build();
            }

            // 4. 재료 소모 (합성 시도 시 무조건 소모)
            consumeMaterials(userId, requiredIngredients);

            // 5. 합성 확률 계산
            boolean craftSuccess = random.nextFloat() < recipe.getExpectedProbability();

            // 6. 통계 업데이트
            recipe.setTrialCount(recipe.getTrialCount() + 1);
            if (craftSuccess) {
                recipe.setSuccessCount(recipe.getSuccessCount() + 1);
            }
            recipe.setActualProbability((float) recipe.getSuccessCount() / recipe.getTrialCount());
            recipeRepository.save(recipe);

            CharacterCardDTO characterCardDTO = null;

            if (craftSuccess) {
                // 7. 합성 성공 시 캐릭터 카드 지급
                CharacterCard characterCard = recipe.getCharacterCard();
                if (characterCard != null) {
                    // 인벤토리에 캐릭터 카드 추가
                    inventoryService.addItemToInventory(userId, characterCard.getId(), 1, InventoryItemType.CHARACTER_CARD);

                    // CharacterCardDTO 생성 (추가 정보 포함)
                    Map<Integer, Integer> requiredResourceCards = requiredIngredients.stream()
                            .collect(Collectors.toMap(
                                    ingredient -> ingredient.getResourceCard().getId(),
                                    RecipeIngredient::getQuantity
                            ));
                    characterCardDTO = cardMapper.convertCharacterCardDTO(characterCard, requiredResourceCards);

                    logger.debug("합성 성공: userId={}, recipeId={}, characterCardId={}",
                            userId, recipeId, characterCard.getId());
                }
            } else {
                logger.debug("합성 실패: userId={}, recipeId={}, 확률={}",
                        userId, recipeId, recipe.getExpectedProbability());
            }

            return RecipeResponseDTO.builder()
                    .isSuccess(craftSuccess)
                    .card(characterCardDTO)
                    .build();

        } catch (Exception e) {
            logger.error("합성 처리 중 오류 발생: userId={}, recipeId={}", userId, recipeId, e);
            return RecipeResponseDTO.builder()
                    .isSuccess(false)
                    .card(null)
                    .build();
        }
    }

    /**
     * 사용자가 필요한 재료를 보유하고 있는지 확인
     */
    private boolean hasRequiredMaterials(String userId, List<RecipeIngredient> requiredIngredients) {
        for (RecipeIngredient ingredient : requiredIngredients) {
            int resourceCardId = ingredient.getResourceCard().getId();
            int requiredQuantity = ingredient.getQuantity();

            // 사용자 인벤토리에서 해당 재료 카드 조회
            Inventory inventory = inventoryRepository
                    .findByUserIdAndCardId(userId, resourceCardId)
                    .orElse(null);

            if (inventory == null || inventory.getQuantity() < requiredQuantity) {
                logger.debug("재료 부족: userId={}, resourceCardId={}, 필요={}, 보유={}",
                        userId, resourceCardId, requiredQuantity,
                        inventory != null ? inventory.getQuantity() : 0);
                return false;
            }
        }
        return true;
    }

    /**
     * 합성에 필요한 재료 소모
     */
    private void consumeMaterials(String userId, List<RecipeIngredient> requiredIngredients) {
        for (RecipeIngredient ingredient : requiredIngredients) {
            int resourceCardId = ingredient.getResourceCard().getId();
            int consumeQuantity = -ingredient.getQuantity(); // 음수로 차감

            boolean success = inventoryService.updateItemQuantity(userId, resourceCardId, consumeQuantity);
            if (!success) {
                logger.error("재료 소모 실패: userId={}, resourceCardId={}, quantity={}",
                        userId, resourceCardId, consumeQuantity);
                throw new RuntimeException("재료 소모 중 오류 발생");
            }

            logger.debug("재료 소모 완료: userId={}, resourceCardId={}, quantity={}",
                    userId, resourceCardId, Math.abs(consumeQuantity));
        }
    }
}