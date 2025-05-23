package com.ssafy.italian_brainrot.dto.card;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CardDTO {
    private int id;
    private String name;
    private String content;
    private String imagePath;
}
