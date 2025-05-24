
package com.ssafy.italian_brainrot.controller;

import com.ssafy.italian_brainrot.dto.inventory.InventoryDTO;
import com.ssafy.italian_brainrot.service.inventory.InventoryService;
import com.ssafy.italian_brainrot.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);

    private final InventoryService inventoryService;
    private final CookieUtil cookieUtil;

    public InventoryController(InventoryService inventoryService, CookieUtil cookieUtil) {
        this.inventoryService = inventoryService;
        this.cookieUtil = cookieUtil;
    }

    /**
     * 인벤토리 아이템 전체 조회
     * GET /inventory
     */
    @GetMapping
    public ResponseEntity<List<InventoryDTO>> getInventoryItems(HttpServletRequest request) {
        // Interceptor에서 이미 인증 체크했으므로 userId 추출
        String userId = cookieUtil.getUserIdFromRequest(request);

        List<InventoryDTO> inventoryItems = inventoryService.getInventoryItemList(userId);

        logger.debug("인벤토리 조회 완료: userId={}, 아이템 수={}", userId, inventoryItems.size());

        return ResponseEntity.ok(inventoryItems);
    }
}