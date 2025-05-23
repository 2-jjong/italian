package com.ssafy.italian_brainrot.dto;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeIngredientsDTO {
    private int resourceCardId;
    private int quantity;
    private int recipeId;
}
