package com.ccs.secretsantaapp.repository;

import com.ccs.secretsantaapp.dao.SecretSantaGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SecretSantaGroupRepository extends JpaRepository<SecretSantaGroup, Long> {
    List<SecretSantaGroup> findByCreatorId(String id);

    @Query(value = "SELECT g FROM SecretSantaGroup g " +
            "JOIN SecretSantaGroupMember gm ON g.groupId = gm.groupId " +
            "WHERE gm.userId = :userId")
    List<SecretSantaGroup> findByMemberId(@Param("userId") String userId);
}
