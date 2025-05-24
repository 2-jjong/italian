package com.ssafy.italian_brainrot.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "t_character_card")
@PrimaryKeyJoinColumn(name = "id")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
public class CharacterCard extends Card {
    @Column(length = 10, nullable = false)
    private String grade;

    @Column
    private String voice;

    @Column(name = "expected_probability", nullable = false)
    private Float expectedProbability;

    @OneToOne(mappedBy = "characterCard", cascade = CascadeType.ALL)
    private Recipe recipe;
}