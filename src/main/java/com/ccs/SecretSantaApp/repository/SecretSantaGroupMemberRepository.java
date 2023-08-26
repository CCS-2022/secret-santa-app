package com.ccs.SecretSantaApp.repository;

import com.ccs.SecretSantaApp.dao.SecretSantaGroupMember;
import com.ccs.SecretSantaApp.dao.SecretSantaUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SecretSantaGroupMemberRepository extends JpaRepository<SecretSantaGroupMember, Long> {
    @Query(value = "SELECT admin FROM SecretSantaGroupMember m " +
                   "WHERE m.userId = :userId AND m.groupId = :groupId")
    Boolean isAdmin(@Param("userId") String userId,
                    @Param("groupId") Long groupId);

    @Query(value = "SELECT userId FROM SecretSantaGroupMember m " +
            "WHERE m.groupId = :groupId")
    List<String> getAllMembersByGroupId(@Param("groupId") Long groupId);

    @Query(value = "SELECT gm FROM SecretSantaGroupMember gm " +
            "WHERE gm.groupId = :groupId " +
            "AND gm.userId = :userId " +
            "AND admin = true")
    Optional<SecretSantaGroupMember> findByUserIdAndGroupId(String userId, Long groupId);

    @Query(value = "SELECT u FROM SecretSantaUser u " +
            "JOIN SecretSantaGroupMember gm ON u.userId = gm.userId " +
            "WHERE gm.groupId = :groupId")
    List<SecretSantaUser> getGroupMembers(@Param("groupId") Long groupId);
}
