package com.ccs.SecretSantaApp.repository;

import com.ccs.SecretSantaApp.dao.SecretSantaUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SecretSantaUserRepository extends JpaRepository<SecretSantaUser, String> {
    @Query(value = "SELECT u FROM SecretSantaUser u " +
            "WHERE LOWER(u.firstName) LIKE LOWER(concat('%', :name, '%')) " +
            "OR LOWER(u.lastName) LIKE LOWER(concat('%', :name, '%'))")
    List<SecretSantaUser> getUsers(@Param("name") String name);
}
