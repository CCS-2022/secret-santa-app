package com.ccs.SecretSantaApp.repository;

import com.ccs.SecretSantaApp.dao.SecretSantaFriendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SecretSantaFriendshipRepository extends JpaRepository<SecretSantaFriendship, Long> {

    // Retrieves friendship or friendship request between two users
    @Query(value = "SELECT f FROM SecretSantaFriendship f " +
            "WHERE (f.requester = :requesterId AND f.recipient = :recipientId AND status = :status) " +
            "OR (f.requester = :recipientId AND f.recipient = :requesterId AND status = :status)")
    Optional<SecretSantaFriendship> getFriendshipByPartiesId(@Param("requesterId") String requesterId,
                                                            @Param("recipientId") String recipientId,
                                                             @Param("status") boolean status);

    // Retrieves all valid friendships for a particular user
    @Query(value = "SELECT f FROM SecretSantaFriendship f " +
            "WHERE (f.requester = :userId OR f.recipient = :userId) AND status = 'true'")
    List<SecretSantaFriendship> getAllFriendsForUser(@Param("userId") String userId);

    // Retrieves friendship or friendship request between two users regardless of status
    @Query(value = "SELECT f FROM SecretSantaFriendship f " +
            "WHERE (f.requester = :requesterId AND f.recipient = :recipientId) " +
            "OR (f.requester = :recipientId AND f.recipient = :requesterId)")
    Optional<SecretSantaFriendship> getFriendshipRequests(@Param("requesterId") String requesterId,
                                                             @Param("recipientId") String recipientId);

    @Query(value = "SELECT u.firstName, u.lastName, f FROM SecretSantaFriendship f " +
            "JOIN SecretSantaUser u ON f.requester = u.userId " +
            "WHERE f.recipient = :recipientId " +
            "AND f.status is null")
    List<Object[]> findAllRequestsByUserId(@Param("recipientId") String recipientId);
}
