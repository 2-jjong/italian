package com.ssafy.italian_brainrot.entity;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class RecipeIngredientId implements Serializable {
    private Integer resourceCard;
    private Integer recipe;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecipeIngredientId that = (RecipeIngredientId) o;
        return Objects.equals(resourceCard, that.resourceCard) && Objects.equals(recipe, that.recipe);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resourceCard, recipe);
    }

}