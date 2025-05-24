package com.ssafy.italian_brainrot.dto.inventory;

import com.ssafy.italian_brainrot.dto.card.CharacterCardDTO;
import com.ssafy.italian_brainrot.dto.card.ResourceCardDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackOpenResponseDTO {
    private List<ResourceCardDTO> resourceCards;
    private CharacterCardDTO characterCard;
}