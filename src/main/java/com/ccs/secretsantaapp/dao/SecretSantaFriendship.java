package com.ccs.secretsantaapp.dao;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.sql.Date;

@Entity
@Data
@NoArgsConstructor
@Table(name = "friendship")
public class SecretSantaFriendship{
    @Id
    @Column(name = "friendship_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "friendship_id_seq")
    @SequenceGenerator(name = "friendship_id_seq", sequenceName = "friendship_id_seq", allocationSize = 1)
    private Long friendshipId;
    private String requester;
    @Transient
    private String requesterFirstName;
    @Transient
    private String requesterLastName;
    private String recipient;
    private Boolean status;
    @Column(name = "date_requested")
    private Date dateRequested;
    @Column(name = "date_processed")
    private Date dateProcessed;

}
