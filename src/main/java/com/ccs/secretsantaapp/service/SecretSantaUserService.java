package com.ccs.secretsantaapp.service;

import com.ccs.secretsantaapp.dao.SecretSantaFriendship;
import com.ccs.secretsantaapp.dao.SecretSantaUser;
import com.ccs.secretsantaapp.exception.EntityNotCreated;
import com.ccs.secretsantaapp.repository.SecretSantaFriendshipRepository;
import com.ccs.secretsantaapp.repository.SecretSantaUserRepository;
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

    private final Keycloak KEYCLOAK;
    private final String REALM;
    @Autowired
    public SecretSantaUserService(@Value("${keycloak.server-url}") String serverUrl,
                                  @Value("${keycloak.realm}") String realm,
                                  @Value("${keycloak.client-id}") String clientId,
                                  @Value("${keycloak.client-secret}") String clientSecret,
                                  @Value("${keycloak.grant-type}") String grantType){
        this.REALM = realm;
        this.KEYCLOAK = KeycloakBuilder.builder()
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
                UserRepresentation friendRepresentation = KEYCLOAK.realm(REALM).users().get(friendId).toRepresentation();
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
        return friendRequest;
    }

    public SecretSantaFriendship processFriendshipRequest(SecretSantaFriendship friendRequest) throws EntityNotCreated {
        Optional<SecretSantaFriendship> friendship = friendshipRepository.findById(friendRequest.getFriendshipId());
        if (friendship.isEmpty()){
            throw new EntityNotCreated("Friendship does not exist!");
        }

        // update accepted date only if the status is true which means accepted
        if (Boolean.TRUE.equals(friendRequest.getStatus())) {
            friendship.get().setDateProcessed(Date.valueOf(LocalDate.now()));
            friendship.get().setStatus(friendRequest.getStatus());
            return friendshipRepository.save(friendship.get());
        }else{
            friendshipRepository.deleteById(friendship.get().getFriendshipId());
            friendship.get().setStatus(false);
            return friendship.get();
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
