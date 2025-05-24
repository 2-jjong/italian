package com.ssafy.italian_brainrot.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "t_comment")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private int productId;

    @Column(nullable = false)
    private double rating;

    @Column(nullable = false)
    private String comment;
}
