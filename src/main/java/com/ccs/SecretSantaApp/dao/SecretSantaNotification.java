package com.ccs.SecretSantaApp.dao;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@Entity
@NoArgsConstructor
@Table(name = "notification")
public class SecretSantaNotification {
    @Id
    @Column(name = "notification_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_id_seq")
    @SequenceGenerator(name = "notification_id_seq", sequenceName = "notification_id_seq", allocationSize = 1)
    private Long notificationId;
    private String recipientId;
    private String title;
    private String body;
    private Date dateCreated;
}
