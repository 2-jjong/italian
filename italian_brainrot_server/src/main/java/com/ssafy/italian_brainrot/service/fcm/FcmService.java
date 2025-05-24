package com.ssafy.italian_brainrot.service.fcm;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.ssafy.italian_brainrot.entity.User;
import com.ssafy.italian_brainrot.enumerate.BattleState;
import com.ssafy.italian_brainrot.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FcmService {

    private final UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(FcmService.class);

    public FcmService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void sendBattleStartNotification(String userId1, String userId2, int battleId) {
        try {
            sendNotificationToUser(userId1, "배틀 시작!", "상대방이 배틀을 수락했습니다.",
                    Map.of("type", "battle_start", "battleId", String.valueOf(battleId)));

            sendNotificationToUser(userId2, "배틀 시작!", "배틀이 시작되었습니다.",
                    Map.of("type", "battle_start", "battleId", String.valueOf(battleId)));
        } catch (Exception e) {
            logger.error("배틀 시작 알림 전송 실패: battleId={}", battleId, e);
        }
    }

    public void sendBattleResultNotification(String userId1, String userId2, int battleId, BattleState winner) {
        try {
            String winnerMessage = "축하합니다! 배틀에서 승리했습니다!";
            String loserMessage = "아쉽게도 배틀에서 패배했습니다.";

            Map<String, String> data = Map.of(
                    "type", "battle_result",
                    "battleId", String.valueOf(battleId),
                    "winner", winner.name());

            if (winner == BattleState.USER1) {
                // User1 승리
                sendNotificationToUser(userId1, "배틀 승리!", winnerMessage, data);
                sendNotificationToUser(userId2, "배틀 결과", loserMessage, data);
            } else {
                // User2 승리
                sendNotificationToUser(userId1, "배틀 결과", loserMessage, data);
                sendNotificationToUser(userId2, "배틀 승리!", winnerMessage, data);
            }
        } catch (Exception e) {
            logger.error("배틀 결과 알림 전송 실패: battleId={}", battleId, e);
        }
    }

    private void sendNotificationToUser(String userId, String title, String body, Map<String, String> data) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null || user.getFcmToken() == null || user.getFcmToken().trim().isEmpty()) {
                logger.warn("FCM 토큰이 없는 사용자: userId={}", userId);
                return;
            }

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