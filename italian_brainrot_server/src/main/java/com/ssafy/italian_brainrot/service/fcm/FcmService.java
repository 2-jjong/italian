package com.ssafy.italian_brainrot.service.fcm;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.ssafy.italian_brainrot.entity.User;
import com.ssafy.italian_brainrot.enumerate.BattleState;
import com.ssafy.italian_brainrot.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FcmService {

    private static final Logger logger = LoggerFactory.getLogger(FcmService.class);

    @Autowired
    private UserRepository userRepository;

    /**
     * 배틀 시작 알림 전송
     */
    public void sendBattleStartNotification(String userId1, String userId2, int battleId) {
        try {
            // User1에게 알림
            sendNotificationToUser(userId1, "배틀 시작!", "상대방이 배틀을 수락했습니다.",
                    Map.of("type", "battle_start", "battleId", String.valueOf(battleId)));

            // User2에게 알림
            sendNotificationToUser(userId2, "배틀 시작!", "배틀이 시작되었습니다.",
                    Map.of("type", "battle_start", "battleId", String.valueOf(battleId)));

            logger.debug("배틀 시작 알림 전송 완료: battleId={}, user1={}, user2={}", battleId, userId1, userId2);

        } catch (Exception e) {
            logger.error("배틀 시작 알림 전송 실패: battleId={}", battleId, e);
        }
    }

    /**
     * 배틀 결과 알림 전송
     */
    public void sendBattleResultNotification(String userId1, String userId2, int battleId, BattleState winner) {
        try {
            String winnerMessage = "축하합니다! 배틀에서 승리했습니다!";
            String loserMessage = "아쉽게도 배틀에서 패배했습니다.";

            Map<String, String> data = Map.of(
                    "type", "battle_result",
                    "battleId", String.valueOf(battleId),
                    "winner", winner.name()
            );

            if (winner == BattleState.USER1) {
                // User1 승리
                sendNotificationToUser(userId1, "배틀 승리!", winnerMessage, data);
                sendNotificationToUser(userId2, "배틀 결과", loserMessage, data);
            } else {
                // User2 승리
                sendNotificationToUser(userId1, "배틀 결과", loserMessage, data);
                sendNotificationToUser(userId2, "배틀 승리!", winnerMessage, data);
            }

            logger.debug("배틀 결과 알림 전송 완료: battleId={}, winner={}", battleId, winner);

        } catch (Exception e) {
            logger.error("배틀 결과 알림 전송 실패: battleId={}", battleId, e);
        }
    }

    /**
     * 특정 사용자에게 FCM 알림 전송
     */
    private void sendNotificationToUser(String userId, String title, String body, Map<String, String> data) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null || user.getFcmToken() == null || user.getFcmToken().trim().isEmpty()) {
                logger.warn("FCM 토큰이 없는 사용자: userId={}", userId);
                return;
            }

            // TODO: FCM API 호출 구현
            // Firebase Admin SDK를 사용하여 실제 푸시 알림 전송
            logger.debug("FCM 알림 전송 (Mock): userId={}, title={}, body={}, token={}",
                    userId, title, body, user.getFcmToken().substring(0, 10) + "...");

            Message message = Message.builder()
                    .setToken(user.getFcmToken())
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .putAllData(data)
                    .build();

            FirebaseMessaging.getInstance().send(message);

        } catch (Exception e) {
            logger.error("FCM 알림 전송 실패: userId={}", userId, e);
        }
    }
}