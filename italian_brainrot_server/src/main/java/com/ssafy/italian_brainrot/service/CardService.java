package com.ssafy.italian_brainrot.service;

import com.ssafy.italian_brainrot.entity.CharacterCard;
import com.ssafy.italian_brainrot.entity.ResourceCard;

public interface CardService {
    ResourceCard getResourceCard(int id);

    CharacterCard getCharacterCard(int id);


}
