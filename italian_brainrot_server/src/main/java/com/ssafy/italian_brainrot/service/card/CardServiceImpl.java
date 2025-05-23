package com.ssafy.italian_brainrot.service.card;

import com.ssafy.italian_brainrot.dto.card.CharacterCardDTO;
import com.ssafy.italian_brainrot.dto.card.ResourceCardDTO;
import com.ssafy.italian_brainrot.entity.ResourceCard;
import com.ssafy.italian_brainrot.repository.CharacterCardRepository;
import com.ssafy.italian_brainrot.repository.RecipeIngredientRepository;
import com.ssafy.italian_brainrot.repository.ResourceCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CardServiceImpl implements CardService {

    @Autowired
    private ResourceCardRepository resourceCardRepository;

    @Autowired
    private CharacterCardRepository characterCardRepository;

    @Autowired
    private RecipeIngredientRepository recipeIngredientRepository;

    @Override
    public ResourceCardDTO getResourceCard(int id) {
        ResourceCard entity = resourceCardRepository.findById(id).orElseThrow();
        

        return null;
    }

    @Override
    public ResourceCardDTO getAllResourceCard(int id) {
        return null;
    }

    @Override
    public CharacterCardDTO getCharacterCard(int id) {
        return null;
    }

    @Override
    public CharacterCardDTO getAllCharacterCard(int id) {
        return null;
    }
}
