package com.ccs.secretsantaapp.repository;

import com.ccs.secretsantaapp.dao.SecretSantaNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecretSantaNotificationRepository extends JpaRepository<SecretSantaNotification, Long> {
}
