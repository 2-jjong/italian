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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
    private final Random random = new Random();

    /**
     * GPT API를 호출하여 카드 배틀 결과 생성
     */
    public BattleResult processBattle(String userId1, String userId2, Card user1Card, Card user2Card) {
        try {
            // GPT API 호출
            String battleContent = callGptApi(userId1, userId2, user1Card, user2Card);

            // 승자 결정 (50:50 확률)
            BattleState winner = random.nextBoolean() ? BattleState.USER1 : BattleState.USER2;

            logger.debug("GPT 배틀 완료: {} vs {}, 승자: {}", userId1, userId2, winner);

            return new BattleResult(winner, battleContent);

        } catch (Exception e) {
            logger.error("GPT 배틀 처리 중 오류 발생: {} vs {}", userId1, userId2, e);

            // 실패 시 기본 결과 반환
            BattleState winner = random.nextBoolean() ? BattleState.USER1 : BattleState.USER2;
            String defaultContent = String.format("%s의 %s vs %s의 %s - 치열한 배틀 끝에 %s이(가) 승리했습니다!",
                    userId1, user1Card.getName(),
                    userId2, user2Card.getName(),
                    winner == BattleState.USER1 ? userId1 : userId2);

            return new BattleResult(winner, defaultContent);
        }
    }

    /**
     * OpenAI GPT API 호출
     */
    private String callGptApi(String userId1, String userId2, Card user1Card, Card user2Card) {
        try {
            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            // 요청 본문 생성
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", List.of(
                    Map.of("role", "system", "content", "당신은 카드 게임의 배틀 해설자입니다. 두 카드의 배틀 상황을 재미있고 생생하게 묘사해주세요."),
                    Map.of("role", "user", "content", String.format(
                            "%s의 카드 '%s' vs %s의 카드 '%s' 배틀을 150자 이내로 흥미롭게 묘사해주세요.",
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

                logger.debug("GPT API 호출 성공: {}", content);
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

    /**
     * 배틀 결과 클래스
     */
    public static class BattleResult {
        private final BattleState winner;
        private final String content;

        public BattleResult(BattleState winner, String content) {
            this.winner = winner;
            this.content = content;
        }

        public BattleState getWinner() {
            return winner;
        }

        public String getContent() {
            return content;
        }
    }
}