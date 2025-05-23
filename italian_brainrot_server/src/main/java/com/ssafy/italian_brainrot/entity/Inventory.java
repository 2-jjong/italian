package com.ssafy.italian_brainrot.entity;

import com.ssafy.italian_brainrot.enumerate.InventoryItemType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "t_inventory")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InventoryItemType state = InventoryItemType.CHARACTER_CARD;

    @Column(nullable = false)
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "card_id")
    private Card card;
}
