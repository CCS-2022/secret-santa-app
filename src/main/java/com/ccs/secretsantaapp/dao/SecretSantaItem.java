package com.ccs.secretsantaapp.dao;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "item")
@NoArgsConstructor
public class SecretSantaItem {
    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_id_seq")
    @SequenceGenerator(name = "item_id_seq", sequenceName = "item_id_seq", allocationSize = 1)
    private Long itemId;
    private String name;
    private String itemUrl;
    private Long groupId;
    private String userId;
}
