package com.ssafy.italian_brainrot.dto.user;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class GradeDTO {
    private String title;      // 등급 이름
    private String img;        // 등급 이미지
    private int step;      // 등급 내 단계 (최고 등급일 경우 null)
    private int stepMax;       // 등급 내 최대 단계
    private int to;        // 다음 등급까지 남은 개수 (최고 등급일 경우 null)
    private int totalStamps;   // 스탬프 총 개수
}