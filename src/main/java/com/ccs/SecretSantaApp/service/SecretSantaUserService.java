package com.ccs.SecretSantaApp.service;

import com.ccs.SecretSantaApp.dao.SecretSantaFriendship;
import com.ccs.SecretSantaApp.dao.SecretSantaNotification;
import com.ccs.SecretSantaApp.dao.SecretSantaUser;
import com.ccs.SecretSantaApp.exception.EntityNotCreated;
import com.ccs.SecretSantaApp.repository.SecretSantaFriendshipRepository;
import com.ccs.SecretSantaApp.repository.SecretSantaNotificationRepository;
import com.ccs.SecretSantaApp.repository.SecretSantaUserRepository;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.ws.rs.NotFoundException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SecretSantaUserService {
    @Autowired
    SecretSantaFriendshipRepository friendshipRepository;
    @Autowired
    SecretSantaUserRepository secretSantaUserRepository;
    @Autowired
    SecretSantaNotificationRepository notificationRepository;
    private final Keycloak keycloak;
    private final String REALM;
    @Autowired
    public SecretSantaUserService(@Value("${keycloak.server-url}") String serverUrl,
                                  @Value("${keycloak.realm}") String realm,
                                  @Value("${keycloak.client-id}") String clientId,
                                  @Value("${keycloak.client-secret}") String clientSecret,
                                  @Value("${keycloak.grant-type}") String grantType){
        this.REALM = realm;
        this.keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(REALM)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType(grantType)
                .build();
    }

    public List<SecretSantaUser> getUserFriends(String userId) {
        List<SecretSantaFriendship> friendships = friendshipRepository.getAllFriendsForUser(userId);
        List<SecretSantaUser> friends = new ArrayList<>();

        if (!friendships.isEmpty()) {
            for (SecretSantaFriendship friendship : friendships) {
                String friendId = (friendship.getRequester().equals(userId)) ?
                        friendship.getRecipient() : friendship.getRequester();
                UserRepresentation friendRepresentation = keycloak.realm(REALM).users().get(friendId).toRepresentation();
                SecretSantaUser friend = new SecretSantaUser();
                friend.setUserId(friendRepresentation.getId());
                friend.setFirstName(friendRepresentation.getFirstName());
                friend.setLastName(friendRepresentation.getLastName());

                friends.add(friend);
            }
        }
        return friends;
    }

    public SecretSantaFriendship sendFriendRequest(SecretSantaFriendship friendRequest) throws EntityNotCreated {
        // check if friendship request already exists
        if(friendshipRepository.getFriendshipRequests(friendRequest.getRequester(),
                friendRequest.getRecipient()).isPresent())
            throw new EntityNotCreated("Friendship request already exists.");

        friendRequest.setDateRequested(Date.valueOf(LocalDate.now()));
        friendRequest.setDateProcessed(null);
        friendRequest.setStatus(null);
        friendshipRepository.save(friendRequest);

        // REPLACE THIS - Maybe create method for sending notifications in a different service?
        // Create notification
        SecretSantaNotification notification = new SecretSantaNotification(); // Since we have a user table now, we can also use that and relieve keycloak server from handling more requests
        UserRepresentation requestor = keycloak
                .realm(REALM)
                .users()
                .get(friendRequest.getRequester())
                .toRepresentation();
        notification.setRecipientId(friendRequest.getRecipient());
        notification.setTitle("Friend request!");
        notification.setBody(requestor.getFirstName() + " " + requestor.getLastName()
        + " would like to become your friend!");
        notification.setDateCreated(friendRequest.getDateRequested());
        notification.setRecipientId(friendRequest.getRecipient());
        notificationRepository.save(notification);

        return friendRequest;
    }

    public SecretSantaFriendship processFriendshipRequest(SecretSantaFriendship friendRequest) throws Exception {
        Optional<SecretSantaFriendship> friendship = friendshipRepository.findById(friendRequest.getFriendshipId());
        if (friendship.isEmpty()){
            throw new Exception("Friendship does not exist!");
        }

        // update accepted date only if the status is true which means accepted
        if (friendRequest.getStatus()) {
            friendship.get().setDateProcessed(Date.valueOf(LocalDate.now()));
            friendship.get().setStatus(friendRequest.getStatus());
            return friendshipRepository.save(friendship.get());
        }else{
            friendshipRepository.deleteById(friendship.get().getFriendshipId());
            return null;
        }
    }

    public SecretSantaFriendship removeFriend(String user, SecretSantaUser friend) {
        Optional<SecretSantaFriendship> friendship = friendshipRepository.getFriendshipByPartiesId(user,
                friend.getUserId(),
                true);

        if(friendship.isEmpty()){
            throw new NotFoundException("Friendship not found");
        }else{
            friendshipRepository.deleteById(friendship.get().getFriendshipId());
        }
        return friendship.get();
    }

    public List<SecretSantaUser> getUsers(String name) {
        return secretSantaUserRepository.getUsers(name);
    }
}
