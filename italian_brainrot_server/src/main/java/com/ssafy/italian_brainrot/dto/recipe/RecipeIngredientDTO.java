package com.ssafy.italian_brainrot.dto.recipe;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeIngredientDTO {
    private int recipeId;
    private int resourceCardId;
    private int quantity;
}
