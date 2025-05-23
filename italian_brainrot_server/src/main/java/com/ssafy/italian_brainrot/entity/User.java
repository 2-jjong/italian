package com.ssafy.italian_brainrot.entity;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Table(name = "t_user")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class User {
    @Id
    @Column(length = 100)
    private String id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 100, nullable = false)
    private String pass;

    @Builder.Default
    @Column(columnDefinition = "integer default 0")
    private int stamps = 0;

    @Builder.Default
    @Column(columnDefinition = "integer default 0")
    private int point = 0;

    @Column(name = "fcm_token", length = 100)
    private String fcmToken;

    public void updateStamps(int stamps){
        this.stamps += stamps;
    }
}