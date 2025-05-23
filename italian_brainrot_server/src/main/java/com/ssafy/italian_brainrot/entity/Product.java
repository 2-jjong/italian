package com.ssafy.italian_brainrot.entity;

import com.ssafy.italian_brainrot.enumerate.InventoryItemType;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Table(name = "t_product")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InventoryItemType type = InventoryItemType.RESOURCE_CARD;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private String img;
}
