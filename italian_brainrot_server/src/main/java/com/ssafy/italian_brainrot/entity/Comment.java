package com.ssafy.italian_brainrot.entity;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
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
    @GeneratedValue
    private int id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private int productId;

    @Column(nullable = false)
    private double rating;

    @Column(nullable = false)
    private String comment;
}
