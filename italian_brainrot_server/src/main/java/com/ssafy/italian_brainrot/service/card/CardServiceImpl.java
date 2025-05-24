package com.ssafy.italian_brainrot.service.card;

import com.ssafy.italian_brainrot.dto.card.CharacterCardDTO;
import com.ssafy.italian_brainrot.dto.card.ResourceCardDTO;
import com.ssafy.italian_brainrot.entity.CharacterCard;
import com.ssafy.italian_brainrot.entity.RecipeIngredient;
import com.ssafy.italian_brainrot.entity.ResourceCard;
import com.ssafy.italian_brainrot.mapper.CardMapper;
import com.ssafy.italian_brainrot.repository.CharacterCardRepository;
import com.ssafy.italian_brainrot.repository.RecipeIngredientRepository;
import com.ssafy.italian_brainrot.repository.ResourceCardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CardServiceImpl implements CardService {

    private static final Logger logger = LoggerFactory.getLogger(CardServiceImpl.class);

    @Autowired
    private ResourceCardRepository resourceCardRepository;

    @Autowired
    private CharacterCardRepository characterCardRepository;

    @Autowired
    private RecipeIngredientRepository recipeIngredientRepository;

    @Autowired
    private CardMapper cardMapper;

    @Override
    public ResourceCardDTO getResourceCard(int id) {
        try {
            ResourceCard entity = resourceCardRepository.findById(id).orElse(null);
            if (entity == null) {
                logger.warn("리소스 카드를 찾을 수 없음: id={}", id);
                return null;
            }

            // 조합 가능한 캐릭터 카드 목록 조회 (Service에서 처리)
            List<Integer> craftableCharacterCardIds = getCraftableCharacterCardIds(id);

            ResourceCardDTO dto = cardMapper.convertResourceCardDTO(entity, craftableCharacterCardIds);
            logger.debug("리소스 카드 조회 성공: id={}, name={}, 조합 가능 카드 수={}",
                    id, dto.getName(), craftableCharacterCardIds.size());
            return dto;

        } catch (Exception e) {
            logger.error("리소스 카드 조회 중 오류 발생: id={}", id, e);
            return null;
        }
    }

    @Override
    public CharacterCardDTO getCharacterCard(int id) {
        try {
            CharacterCard entity = characterCardRepository.findById(id).orElse(null);
            if (entity == null) {
                logger.warn("캐릭터 카드를 찾을 수 없음: id={}", id);
                return null;
            }

            // 필요한 재료 카드 목록 조회 (Service에서 처리)
            Map<Integer, Integer> requiredResourceCards = getRequiredResourceCards(id);

            CharacterCardDTO dto = cardMapper.convertCharacterCardDTO(entity, requiredResourceCards);
            logger.debug("캐릭터 카드 조회 성공: id={}, name={}, grade={}, 필요 재료 수={}",
                    id, dto.getName(), dto.getGrade(), requiredResourceCards.size());
            return dto;

        } catch (Exception e) {
            logger.error("캐릭터 카드 조회 중 오류 발생: id={}", id, e);
            return null;
        }
    }

    @Override
    public List<ResourceCardDTO> getAllResourceCards() {
        try {
            List<ResourceCard> entities = resourceCardRepository.findAll();
            List<ResourceCardDTO> dtoList = cardMapper.convertResourceCardDTOList(entities);

            logger.debug("전체 리소스 카드 조회 완료: {}개", dtoList.size());
            return dtoList;

        } catch (Exception e) {
            logger.error("전체 리소스 카드 조회 중 오류 발생", e);
            return List.of();
        }
    }

    @Override
    public List<CharacterCardDTO> getAllCharacterCards() {
        try {
            List<CharacterCard> entities = characterCardRepository.findAll();
            List<CharacterCardDTO> dtoList = cardMapper.convertCharacterCardDTOList(entities);

            logger.debug("전체 캐릭터 카드 조회 완료: {}개", dtoList.size());
            return dtoList;

        } catch (Exception e) {
            logger.error("전체 캐릭터 카드 조회 중 오류 발생", e);
            return List.of();
        }
    }

    /**
     * 해당 재료 카드로 조합 가능한 캐릭터 카드 ID 목록 조회
     */
    private List<Integer> getCraftableCharacterCardIds(Integer resourceCardId) {
        try {
            List<RecipeIngredient> recipeIngredients = recipeIngredientRepository
                    .findByResourceCardId(resourceCardId);

            return recipeIngredients.stream()
                    .map(ingredient -> ingredient.getRecipe().getId())
                    .distinct()
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.warn("조합 가능한 캐릭터 카드 조회 실패: resourceCardId={}", resourceCardId, e);
            return List.of();
        }
    }

    /**
     * 해당 캐릭터 카드를 만들기 위해 필요한 재료 카드와 수량 조회
     */
    private Map<Integer, Integer> getRequiredResourceCards(Integer characterCardId) {
        try {
            List<RecipeIngredient> recipeIngredients = recipeIngredientRepository
                    .findByRecipeId(characterCardId);

            return recipeIngredients.stream()
                    .collect(Collectors.toMap(
                            ingredient -> ingredient.getResourceCard().getId(),
                            RecipeIngredient::getQuantity
                    ));
        } catch (Exception e) {
            logger.warn("필요한 재료 카드 조회 실패: characterCardId={}", characterCardId, e);
            return Map.of();
        }
    }
}