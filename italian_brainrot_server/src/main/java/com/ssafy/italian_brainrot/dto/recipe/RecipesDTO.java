package com.ssafy.italian_brainrot.dto.recipe;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipesDTO {
    private int id;
    private int trialCount;
    private int successCount;
    private float expectedProbability;
    private float actualProbability;
}
