package com.ssafy.italian_brainrot.dto.user;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class UserResponseDTO {
    private String id;
    private String name;
    private GradeDTO grade;
    private int point;
}