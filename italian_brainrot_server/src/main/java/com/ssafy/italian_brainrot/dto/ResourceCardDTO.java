package com.ssafy.italian_brainrot.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceCardDTO extends CardDTO {
    private int product_id;
    private double expected_probability;
    private List<Integer> craftableCharacterCardIds;
}
