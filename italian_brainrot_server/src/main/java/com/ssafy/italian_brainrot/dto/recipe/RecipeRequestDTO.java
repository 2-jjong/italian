
package com.ssafy.italian_brainrot.dto.recipe;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeRequestDTO {
    private int recipeId; // 합성할 레시피 ID
}