package com.ssafy.italian_brainrot.service.battle;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.italian_brainrot.entity.Card;
import com.ssafy.italian_brainrot.entity.CharacterCard;
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
import java.util.Random;

@Service
public class GptBattleService {
    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    @Value("${openai.model}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Logger log = LoggerFactory.getLogger(GptBattleService.class);
    private final Random random = new Random();

    // 등급 매핑 (낮은 숫자가 높은 등급)
    private final Map<String, Integer> gradeToNumber = Map.of(
            "A", 1, "B", 2, "C", 3, "D", 4, "E", 5
    );

    public Map<String, Constable> processBattle(String userId1, String userId2, Card user1Card, Card user2Card) {
        try {
            CharacterCard char1 = (CharacterCard) user1Card;
            CharacterCard char2 = (CharacterCard) user2Card;

            // 승자 결정
            BattleState winner = determineWinner(char1, char2);

            // 승자 정보
            String winnerUserId = (winner == BattleState.USER1) ? userId1 : userId2;
            String winnerCardName = (winner == BattleState.USER1) ? char1.getName() : char2.getName();
            String loserUserId = (winner == BattleState.USER1) ? userId2 : userId1;
            String loserCardName = (winner == BattleState.USER1) ? char2.getName() : char1.getName();

            // GPT API 호출
            String battleContent = callGptApi(winnerUserId, winnerCardName, loserUserId, loserCardName);

            return Map.of("winner", winner, "content", battleContent);
        } catch (Exception e) {
            log.error("GPT 배틀 처리 중 오류 발생: {} vs {}", userId1, userId2, e);
            return null;
        }
    }

    private BattleState determineWinner(CharacterCard char1, CharacterCard char2) {
        int grade1 = gradeToNumber.getOrDefault(char1.getGrade().toUpperCase(), 5); // 기본값 E등급
        int grade2 = gradeToNumber.getOrDefault(char2.getGrade().toUpperCase(), 5);

        int gradeDiff = Math.abs(grade1 - grade2);

        if (gradeDiff >= 2) {
            return (grade1 < grade2) ? BattleState.USER1 : BattleState.USER2;
        } else if (gradeDiff == 1) {
            boolean higherGradeWins = random.nextFloat() < 0.7f;
            if (higherGradeWins) {
                return (grade1 < grade2) ? BattleState.USER1 : BattleState.USER2;
            } else {
                return (grade1 > grade2) ? BattleState.USER1 : BattleState.USER2;
            }
        } else {
            return random.nextBoolean() ? BattleState.USER1 : BattleState.USER2;
        }
    }

    private String callGptApi(String winnerUserId, String winnerCardName, String loserUserId, String loserCardName) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            String prompt = String.format(
                    "%s의 캐릭터 '%s'가 %s의 캐릭터 '%s'와의 배틀에서 승리했습니다. " +
                            "이 결과를 바탕으로 흥미진진한 배틀 과정을 200자 이내로 생생하게 묘사해주세요. " +
                            "전장 배경과 승부의 과정, 결정적인 순간을 포함해서 서술해주세요.",
                    winnerUserId, winnerCardName, loserUserId, loserCardName
            );

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", List.of(
                    Map.of("role", "system", "content",
                            "당신은 Italian Brainrot이라는 트렌드의 전문가입니다. " +
                                    "주어진 결과를 바탕으로 박진감 넘치는 배틀 스토리를 만들어주세요. " +
                                    "캐릭터의 특성을 고려하여 현실적이면서도 흥미로운 전개를 그려주세요. " +
                                    "응답은 마크다운이나 특수 문자 없이 순수한 한국어 줄글로만 작성해주세요."),
                    Map.of("role", "user", "content", prompt)
            ));
            requestBody.put("max_tokens", 200);
            requestBody.put("temperature", 0.8);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            // API 호출
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                String content = jsonNode.path("choices").get(0).path("message").path("content").asText();

                log.debug("GPT 배틀 결과 생성 완료: winner={}, content length={}",
                        winnerUserId, content.length());

                return content.trim();
            } else {
                log.warn("GPT API 호출 실패: {}", response.getStatusCode());
                throw new RuntimeException("GPT API 호출 실패");
            }

        } catch (Exception e) {
            log.error("GPT API 호출 중 오류 발생", e);
            throw new RuntimeException("GPT API 호출 오류", e);
        }
    }
}