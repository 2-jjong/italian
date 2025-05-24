package com.ssafy.italian_brainrot.service.recipe;

import com.ssafy.italian_brainrot.dto.recipe.RecipeResponseDTO;

public interface RecipeService {

    RecipeResponseDTO craftCard(String userId, int recipeId);

}