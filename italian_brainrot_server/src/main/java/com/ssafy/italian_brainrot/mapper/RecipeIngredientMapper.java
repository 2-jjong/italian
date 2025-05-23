package com.ssafy.italian_brainrot.mapper;

import com.ssafy.italian_brainrot.dto.recipe.RecipeIngredientDTO;
import com.ssafy.italian_brainrot.entity.Recipe;
import com.ssafy.italian_brainrot.entity.RecipeIngredient;
import com.ssafy.italian_brainrot.entity.ResourceCard;
import org.springframework.stereotype.Component;

@Component
public class RecipeIngredientMapper {
    public RecipeIngredientDTO convert(RecipeIngredient entity) {
        RecipeIngredientDTO dto = RecipeIngredientDTO
                .builder()
                .recipeId(entity.getRecipe().getId())
                .resourceCardId(entity.getResourceCard().getId())
                .quantity(entity.getQuantity())
                .build();
        return dto;
    }

    public RecipeIngredient convert(RecipeIngredientDTO dto) {
        ResourceCard resourceCard = new ResourceCard();
        resourceCard.setId(dto.getResourceCardId());
        RecipeIngredient entity = RecipeIngredient
                .builder()
                .recipe(Recipe.builder().id(dto.getRecipeId()).build())
                .resourceCard(resourceCard)
                .quantity(dto.getQuantity())
                .build();
        return entity;
    }
}
