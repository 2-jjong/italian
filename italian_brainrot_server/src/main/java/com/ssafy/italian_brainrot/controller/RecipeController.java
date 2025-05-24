package com.ssafy.italian_brainrot.controller;

import com.ssafy.italian_brainrot.dto.recipe.RecipeRequestDTO;
import com.ssafy.italian_brainrot.dto.recipe.RecipeResponseDTO;
import com.ssafy.italian_brainrot.service.recipe.RecipeService;
import com.ssafy.italian_brainrot.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recipe")
public class RecipeController {
    private final RecipeService recipeService;
    private final CookieUtil cookieUtil;
    private final Logger log = LoggerFactory.getLogger(RecipeController.class);

    public RecipeController(RecipeService recipeService,
                            CookieUtil cookieUtil) {
        this.recipeService = recipeService;
        this.cookieUtil = cookieUtil;
    }

    @PostMapping("")
    public RecipeResponseDTO craftCard(@RequestBody RecipeRequestDTO request,
                                                       HttpServletRequest httpRequest) {
        String userId = cookieUtil.getUserIdFromRequest(httpRequest);

        return recipeService.craftCard(userId, request.getRecipeId());
    }
}