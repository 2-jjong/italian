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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryService inventoryService;
    private final CardMapper cardMapper;
    private final Logger log = LoggerFactory.getLogger(RecipeServiceImpl.class);

    private final Random random = new Random();

    public RecipeServiceImpl(RecipeRepository recipeRepository,
                             RecipeIngredientRepository recipeIngredientRepository,
                             InventoryRepository inventoryRepository,
                             InventoryService inventoryService,
                             CardMapper cardMapper) {
        this.recipeRepository = recipeRepository;
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.inventoryRepository = inventoryRepository;
        this.inventoryService = inventoryService;
        this.cardMapper = cardMapper;
    }

    @Override
    @Transactional
    public RecipeResponseDTO craftCard(String userId, int recipeId) {
        try {
            // 1. 레시피 조회
            Recipe recipe = recipeRepository.findById(recipeId).orElse(null);
            if (recipe == null) {
                return RecipeResponseDTO.builder()
                        .isSuccess(false)
                        .card(null)
                        .build();
            }

            // 2. 필요한 재료 카드 조회
            List<RecipeIngredient> requiredIngredients = recipeIngredientRepository.findByRecipeId(recipeId);
            if (requiredIngredients.isEmpty()) {
                return RecipeResponseDTO.builder()
                        .isSuccess(false)
                        .card(null)
                        .build();
            }

            // 3. 사용자 인벤토리에서 재료 확인
            if (!hasRequiredMaterials(userId, requiredIngredients)) {
                return RecipeResponseDTO.builder()
                        .isSuccess(false)
                        .card(null)
                        .build();
            }

            // 4. 재료 소모
            if (!consumeMaterials(userId, requiredIngredients)) {
                return RecipeResponseDTO.builder()
                        .isSuccess(false)
                        .card(null)
                        .build();
            }

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
                    boolean isSuccess = inventoryService.InsertItemToInventory(userId, characterCard.getId(), 1, InventoryItemType.CHARACTER_CARD);

                    if(!isSuccess){
                        return RecipeResponseDTO.builder()
                                .isSuccess(false)
                                .card(null)
                                .build();
                    }

                    // CharacterCardDTO 생성
                    Map<Integer, Integer> requiredResourceCards = requiredIngredients.stream()
                            .collect(Collectors.toMap(
                                    ingredient -> ingredient.getResourceCard().getId(),
                                    RecipeIngredient::getQuantity));
                    characterCardDTO = cardMapper.convertToCharacterCardDTO(characterCard, requiredResourceCards);
                }
            }

            return RecipeResponseDTO.builder()
                    .isSuccess(craftSuccess)
                    .card(characterCardDTO)
                    .build();
        } catch (Exception e) {
            log.error("합성 처리 중 오류 발생: userId={}, recipeId={}", userId, recipeId, e);
            return RecipeResponseDTO.builder()
                    .isSuccess(false)
                    .card(null)
                    .build();
        }
    }

    private boolean hasRequiredMaterials(String userId, List<RecipeIngredient> requiredIngredients) {
        for (RecipeIngredient ingredient : requiredIngredients) {
            int resourceCardId = ingredient.getResourceCard().getId();
            int requiredQuantity = ingredient.getQuantity();

            Inventory inventory = inventoryRepository
                    .findByUserIdAndCardId(userId, resourceCardId)
                    .orElse(null);

            if (inventory == null || inventory.getQuantity() < requiredQuantity) {
                return false;
            }
        }

        return true;
    }

    private boolean consumeMaterials(String userId, List<RecipeIngredient> requiredIngredients) {
        for (RecipeIngredient ingredient : requiredIngredients) {
            int resourceCardId = ingredient.getResourceCard().getId();
            int consumeQuantity = -ingredient.getQuantity();

            boolean isSuccess = inventoryService.updateItemQuantity(userId, resourceCardId, consumeQuantity);

            if (!isSuccess) {
                return false;
            }
        }

        return true;
    }
}