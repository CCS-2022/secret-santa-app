package com.ccs.SecretSantaApp.repository;

import com.ccs.SecretSantaApp.dao.SecretSantaNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecretSantaNotificationRepository extends JpaRepository<SecretSantaNotification, Long> {
}
