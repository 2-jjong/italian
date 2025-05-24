package com.ssafy.italian_brainrot.controller;

import com.ssafy.italian_brainrot.dto.card.CharacterCardDTO;
import com.ssafy.italian_brainrot.dto.card.ResourceCardDTO;
import com.ssafy.italian_brainrot.service.card.CardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/card")
public class CardController {

    private final CardService cardService;
    private static final Logger log = LoggerFactory.getLogger(CardController.class);

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping("/resource/{resourceCardId}")
    public ResourceCardDTO getResourceCard(@PathVariable("resourceCardId") int resourceCardId) {
        return cardService.getResourceCard(resourceCardId);
    }

    @GetMapping("/character/{characterCardId}")
    public CharacterCardDTO getCharacterCard(@PathVariable("characterCardId") int characterCardId) {
        return cardService.getCharacterCard(characterCardId);
    }

    @GetMapping("/resource")
    public List<ResourceCardDTO> getAllResourceCards() {
        return cardService.getAllResourceCards();
    }

    @GetMapping("/character")
    public List<CharacterCardDTO> getAllCharacterCards() {
        return cardService.getAllCharacterCards();
    }

}