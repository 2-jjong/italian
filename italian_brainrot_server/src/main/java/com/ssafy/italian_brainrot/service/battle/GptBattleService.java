package com.ssafy.italian_brainrot.service.battle;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.italian_brainrot.entity.Card;
import com.ssafy.italian_brainrot.enumerate.BattleState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.lang.constant.Constable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GptBattleService {

    private static final Logger logger = LoggerFactory.getLogger(GptBattleService.class);

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    @Value("${openai.model}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Constable> processBattle(String userId1, String userId2, Card user1Card, Card user2Card) {
        try {
            /*
                TODO: GPT 반환 결과 승자를 정해서 GPT 호출?
             */

            // 승자 결정
            BattleState winner = BattleState.USER1;

            // GPT API 호출
            String battleContent = callGptApi(userId1, userId2, user1Card, user2Card);

            return Map.of("winner", winner, "content", battleContent);
        } catch (Exception e) {
            logger.error("GPT 배틀 처리 중 오류 발생: {} vs {}", userId1, userId2, e);
            return null;
        }
    }

    private String callGptApi(String userId1, String userId2, Card user1Card, Card user2Card) {
        try {
            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            // TODO: Italian Brainrot의 게임 설정에 따라 요청 본문 수정
            // 요청 본문 생성
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", List.of(
                    Map.of("role", "system", "content", "당신은 게임의 배틀 해설자입니다. 두 캐릭터의 배틀 상황을 재미있고 생생하게 묘사해주세요."),
                    Map.of("role", "user", "content", String.format(
                            "%s의 캐릭터 '%s' vs %s의 캐릭터 '%s' 배틀을 150자 이내로 흥미롭게 묘사해주세요.",
                            userId1, user1Card.getName(), userId2, user2Card.getName()))
            ));
            requestBody.put("max_tokens", 200);
            requestBody.put("temperature", 0.8);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            // API 호출
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                String content = jsonNode.path("choices").get(0).path("message").path("content").asText();

                return content.trim();
            } else {
                logger.warn("GPT API 호출 실패: {}", response.getStatusCode());
                throw new RuntimeException("GPT API 호출 실패");
            }

        } catch (Exception e) {
            logger.error("GPT API 호출 중 오류 발생", e);
            throw new RuntimeException("GPT API 호출 오류", e);
        }
    }

}