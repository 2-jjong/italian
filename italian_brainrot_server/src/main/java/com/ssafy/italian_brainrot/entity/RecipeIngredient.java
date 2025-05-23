package com.ssafy.italian_brainrot.entity;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Table(name = "t_recipe_ingredient")
@IdClass(RecipeIngredientId.class)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class RecipeIngredient {
    @Id
    @ManyToOne
    @JoinColumn(name = "resourceCard_id")
    private ResourceCard resourceCard;

    @Id
    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    @Column(nullable = false)
    private Integer quantity;
}
