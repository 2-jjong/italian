package com.ssafy.italian_brainrot.controller;

import com.ssafy.italian_brainrot.dto.inventory.InventoryDTO;
import com.ssafy.italian_brainrot.dto.inventory.PackOpenResponseDTO;
import com.ssafy.italian_brainrot.service.inventory.InventoryService;
import com.ssafy.italian_brainrot.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;
    private final CookieUtil cookieUtil;
    private final Logger log = LoggerFactory.getLogger(InventoryController.class);

    public InventoryController(InventoryService inventoryService, CookieUtil cookieUtil) {
        this.inventoryService = inventoryService;
        this.cookieUtil = cookieUtil;
    }

    @GetMapping("")
    public List<InventoryDTO> getInventoryItems(HttpServletRequest request) {
        String userId = cookieUtil.getUserIdFromRequest(request);

        return inventoryService.getInventoryItemList(userId);
    }

    @PostMapping("/pack")
    public PackOpenResponseDTO openCardPack(@RequestBody Map<String, Integer> request,
                                            HttpServletRequest httpRequest) {
        String userId = cookieUtil.getUserIdFromRequest(httpRequest);
        Integer packId = request.get("id");

        if (packId == null) {
            return null;
        }

        return inventoryService.openCardPack(userId, packId);
    }
}