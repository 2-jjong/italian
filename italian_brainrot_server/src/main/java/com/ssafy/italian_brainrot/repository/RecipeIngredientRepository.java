package com.ssafy.italian_brainrot.repository;

import com.ssafy.italian_brainrot.entity.RecipeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, Integer> {

    /**
     * 특정 리소스 카드가 사용되는 레시피들 조회
     */
    List<RecipeIngredient> findByResourceCardId(int resourceCardId);

    /**
     * 특정 레시피에 필요한 재료들 조회
     */
    List<RecipeIngredient> findByRecipeId(int recipeId);
}