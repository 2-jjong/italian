package com.ssafy.italian_brainrot.dto.card;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharacterCardDTO extends CardDTO {
    private String grade;
    private String voicePath;
    private double expectedProbability;
    private Map<Integer, Integer> requiredResourceCards;
}
