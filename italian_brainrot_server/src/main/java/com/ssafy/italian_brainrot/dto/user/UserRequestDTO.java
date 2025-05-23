package com.ssafy.italian_brainrot.dto.user;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class UserRequestDTO {
    private String id;
    private String name;
    private String pass;
}
