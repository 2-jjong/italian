package com.ssafy.italian_brainrot.entity;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "t_recipe")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Recipe {
    @Id
    private Integer id;

    @Column(name = "trial_count", nullable = false)
    private Integer trialCount = 0;

    @Column(name = "success_count", nullable = false)
    private Integer successCount = 0;

    @Column(name = "expected_probability", nullable = false)
    private Float expectedProbability;

    @Column(name = "actual_probability", nullable = false)
    private Float actualProbability;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private CharacterCard characterCard;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
    private List<RecipeIngredient> ingredients = new ArrayList<>();
}