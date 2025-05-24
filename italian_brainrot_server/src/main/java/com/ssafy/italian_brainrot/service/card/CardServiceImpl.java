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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CardServiceImpl implements CardService {

    private final ResourceCardRepository resourceCardRepository;
    private final CharacterCardRepository characterCardRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final CardMapper cardMapper;
    private final Logger logger = LoggerFactory.getLogger(CardServiceImpl.class);

    public CardServiceImpl(ResourceCardRepository resourceCardRepository,
                           CharacterCardRepository characterCardRepository,
                           RecipeIngredientRepository recipeIngredientRepository,
                           CardMapper cardMapper) {
        this.resourceCardRepository = resourceCardRepository;
        this.characterCardRepository = characterCardRepository;
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.cardMapper = cardMapper;
    }

    @Override
    public ResourceCardDTO getResourceCard(int id) {
        try {
            ResourceCard entity = resourceCardRepository.findById(id).orElse(null);
            if (entity == null) {
                return null;
            }

            List<Integer> craftableCharacterCardIds = getCraftableCharacterCardIds(id);

            return cardMapper.convertToResourceCardDTO(entity, craftableCharacterCardIds);
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
                return null;
            }

            Map<Integer, Integer> requiredResourceCards = getRequiredResourceCards(id);

            return cardMapper.convertToCharacterCardDTO(entity, requiredResourceCards);
        } catch (Exception e) {
            logger.error("캐릭터 카드 조회 중 오류 발생: id={}", id, e);
            return null;
        }
    }

    @Override
    public List<ResourceCardDTO> getAllResourceCards() {
        try {
            List<ResourceCard> entities = resourceCardRepository.findAll();
            return cardMapper.convertToResourceCardDTOList(entities);
        } catch (Exception e) {
            logger.error("전체 리소스 카드 조회 중 오류 발생", e);
            return null;
        }
    }

    @Override
    public List<CharacterCardDTO> getAllCharacterCards() {
        try {
            List<CharacterCard> entities = characterCardRepository.findAll();
            return cardMapper.convertToCharacterCardDTOList(entities);
        } catch (Exception e) {
            logger.error("전체 캐릭터 카드 조회 중 오류 발생", e);
            return null;
        }
    }

    private List<Integer> getCraftableCharacterCardIds(Integer resourceCardId) {
        try {
            List<RecipeIngredient> recipeIngredients = recipeIngredientRepository.findByResourceCardId(resourceCardId);

            return recipeIngredients.stream()
                    .map(ingredient -> ingredient.getRecipe().getId())
                    .distinct()
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.warn("조합 가능한 캐릭터 카드 조회 실패: resourceCardId={}", resourceCardId, e);
            return null;
        }
    }

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
            return null;
        }
    }
}