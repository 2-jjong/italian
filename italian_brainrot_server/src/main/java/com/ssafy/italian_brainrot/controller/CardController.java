package com.ssafy.italian_brainrot.controller;

import com.ssafy.italian_brainrot.dto.card.CharacterCardDTO;
import com.ssafy.italian_brainrot.dto.card.ResourceCardDTO;
import com.ssafy.italian_brainrot.service.card.CardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/card")
public class CardController {

    private static final Logger logger = LoggerFactory.getLogger(CardController.class);
    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    /**
     * 특정 리소스 카드 상세 조회
     * GET /card/resource/{resourceCardId}
     */
    @GetMapping("/resource/{resourceCardId}")
    public ResponseEntity<ResourceCardDTO> getResourceCard(@PathVariable int resourceCardId) {
        ResourceCardDTO resourceCard = cardService.getResourceCard(resourceCardId);

        if (resourceCard != null) {
            logger.debug("리소스 카드 조회 성공: id={}", resourceCardId);
            return ResponseEntity.ok(resourceCard);
        } else {
            logger.warn("리소스 카드 조회 실패: id={}", resourceCardId);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 특정 캐릭터 카드 상세 조회
     * GET /card/character/{characterCardId}
     */
    @GetMapping("/character/{characterCardId}")
    public ResponseEntity<CharacterCardDTO> getCharacterCard(@PathVariable int characterCardId) {
        CharacterCardDTO characterCard = cardService.getCharacterCard(characterCardId);

        if (characterCard != null) {
            logger.debug("캐릭터 카드 조회 성공: id={}", characterCardId);
            return ResponseEntity.ok(characterCard);
        } else {
            logger.warn("캐릭터 카드 조회 실패: id={}", characterCardId);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 전체 리소스 카드 조회
     * GET /card/resource
     */
    @GetMapping("/resource")
    public ResponseEntity<List<ResourceCardDTO>> getAllResourceCards() {
        List<ResourceCardDTO> resourceCards = cardService.getAllResourceCards();

        logger.debug("전체 리소스 카드 조회 완료: {}개", resourceCards.size());
        return ResponseEntity.ok(resourceCards);
    }

    /**
     * 전체 캐릭터 카드 조회
     * GET /card/character
     */
    @GetMapping("/character")
    public ResponseEntity<List<CharacterCardDTO>> getAllCharacterCards() {
        List<CharacterCardDTO> characterCards = cardService.getAllCharacterCards();

        logger.debug("전체 캐릭터 카드 조회 완료: {}개", characterCards.size());
        return ResponseEntity.ok(characterCards);
    }
}