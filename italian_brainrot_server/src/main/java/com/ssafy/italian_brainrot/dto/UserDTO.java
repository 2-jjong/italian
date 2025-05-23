package com.ssafy.italian_brainrot.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String id;
    private String name;
    private String pass;
    private Integer stamps;
    private List<StampDTO> stampList = new ArrayList<>();
}