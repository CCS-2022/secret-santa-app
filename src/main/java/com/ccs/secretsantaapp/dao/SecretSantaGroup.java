package com.ccs.secretsantaapp.dao;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;

@Entity
@Data
@Table(name = "secret_santa_group")
@NoArgsConstructor
public class SecretSantaGroup {

    @Id
    @Column(name = "group_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "group_id_seq")
    @SequenceGenerator(name = "group_id_seq", sequenceName = "group_id_seq", allocationSize = 1)
    private Long groupId;

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "date_created")
    private Date dateCreated;

    @Column(name = "creator_id")
    private String creatorId;

    @Transient
    private List<String> memberIds;
}
