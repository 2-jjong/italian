package com.ssafy.italian_brainrot.entity;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "t_battle")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Battle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 100, nullable = false)
    private String userid1;

    @Column(length = 100)
    private String userid2;

    @ManyToOne
    @JoinColumn(name = "user1Card", nullable = false)
    private Card user1Card;

    @ManyToOne
    @JoinColumn(name = "user2Card")
    private Card user2Card;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BattleState state = BattleState.WAITING;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Builder.Default
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum BattleState {
        WAITING, RUNNING, CANCEL, USER1, USER2
    }

}