
package com.ssafy.italian_brainrot.service.card;

import com.ssafy.italian_brainrot.dto.card.CharacterCardDTO;
import com.ssafy.italian_brainrot.dto.card.ResourceCardDTO;

import java.util.List;

public interface CardService {

    /**
     * 특정 리소스 카드 상세 조회
     *
     * @param id 리소스 카드 ID
     * @return 리소스 카드 상세 정보
     */
    ResourceCardDTO getResourceCard(int id);

    /**
     * 특정 캐릭터 카드 상세 조회
     *
     * @param id 캐릭터 카드 ID
     * @return 캐릭터 카드 상세 정보
     */
    CharacterCardDTO getCharacterCard(int id);

    /**
     * 전체 리소스 카드 조회
     *
     * @return 전체 리소스 카드 목록
     */
    List<ResourceCardDTO> getAllResourceCards();

    /**
     * 전체 캐릭터 카드 조회
     *
     * @return 전체 캐릭터 카드 목록
     */
    List<CharacterCardDTO> getAllCharacterCards();
}