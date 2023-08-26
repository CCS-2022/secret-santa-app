package com.ccs.SecretSantaApp.dao;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "secret_santa_user")
public class SecretSantaUser {
    @Id
    @Column(name = "user_id")
    private String userId;
    private String firstName;
    private String lastName;
}
