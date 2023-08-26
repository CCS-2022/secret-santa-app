package com.ccs.SecretSantaApp.dao;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "group_member")
public class SecretSantaGroupMember {

    @Id
    @Column(name = "group_member_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "group_member_id_seq")
    @SequenceGenerator(name = "group_member_id_seq", sequenceName = "group_member_id_seq", allocationSize = 1)
    private Long groupMemberId;

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "admin")
    private Boolean admin;
}
