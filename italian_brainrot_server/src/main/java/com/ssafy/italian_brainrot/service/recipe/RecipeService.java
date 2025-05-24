package com.ssafy.italian_brainrot.service.recipe;

import com.ssafy.italian_brainrot.dto.recipe.RecipeResponseDTO;

public interface RecipeService {

    /**
     * 카드 합성 실행
     * - 사용자 인벤토리에서 필요한 재료 카드 확인
     * - 합성 확률에 따라 성공/실패 결정
     * - 재료 카드 소모 및 결과 처리
     * - 합성 통계 업데이트
     *
     * @param userId 사용자 ID
     * @param recipeId 합성할 레시피 ID
     * @return 합성 결과 (성공 여부 + 획득 카드)
     */
    RecipeResponseDTO craftCard(String userId, int recipeId);
}