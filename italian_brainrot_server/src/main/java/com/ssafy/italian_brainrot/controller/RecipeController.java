package com.ssafy.italian_brainrot.controller;

import com.ssafy.italian_brainrot.dto.recipe.RecipeRequestDTO;
import com.ssafy.italian_brainrot.dto.recipe.RecipeResponseDTO;
import com.ssafy.italian_brainrot.service.recipe.RecipeService;
import com.ssafy.italian_brainrot.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recipe")
public class RecipeController {

    private static final Logger logger = LoggerFactory.getLogger(RecipeController.class);

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private CookieUtil cookieUtil;

    /**
     * 카드 합성
     * POST /recipe
     */
    @PostMapping
    public ResponseEntity<RecipeResponseDTO> craftCard(@RequestBody RecipeRequestDTO request,
                                                       HttpServletRequest httpRequest) {
        // Interceptor에서 이미 인증 체크했으므로 userId 추출
        String userId = cookieUtil.getUserIdFromRequest(httpRequest);

        logger.debug("카드 합성 요청: userId={}, recipeId={}", userId, request.getRecipeId());

        RecipeResponseDTO response = recipeService.craftCard(userId, request.getRecipeId());

        if (response.isSuccess()) {
            logger.debug("카드 합성 성공: userId={}, recipeId={}, cardId={}",
                    userId, request.getRecipeId(),
                    response.getCard() != null ? response.getCard().getId() : "null");
        } else {
            logger.debug("카드 합성 실패: userId={}, recipeId={}", userId, request.getRecipeId());
        }

        return ResponseEntity.ok(response);
    }
}