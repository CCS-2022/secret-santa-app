package com.ccs.SecretSantaApp.controller;

import com.ccs.SecretSantaApp.dao.SecretSantaFriendship;
import com.ccs.SecretSantaApp.dao.SecretSantaGroup;
import com.ccs.SecretSantaApp.dao.SecretSantaUser;
import com.ccs.SecretSantaApp.exception.EntityNotCreated;
import com.ccs.SecretSantaApp.service.SecretSantaGroupService;
import com.ccs.SecretSantaApp.service.SecretSantaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Maps all operations related to user.
 * E.G. : view user's group, view user's friends, add friends and remove friends, etc...
 */
@RestController
@RequestMapping("/secret-santa/user")
public class UserController {

    @Autowired
    private SecretSantaUserService userService;
    @Autowired
    private SecretSantaGroupService secretSantaGroupService;

    @GetMapping("/friends")
    public ResponseEntity<List<SecretSantaUser>> getUserFriends(@AuthenticationPrincipal Jwt source){
        String userId = source.getClaimAsString("sub");
        return new ResponseEntity<>(userService.getUserFriends(userId), HttpStatus.OK);
    }

    @GetMapping("/groups")
    public ResponseEntity<List<SecretSantaGroup>> getUserGroups(@AuthenticationPrincipal Jwt source){
        String userId = source.getClaimAsString("sub");
        return new ResponseEntity<>(secretSantaGroupService.getAllGroupsByGroupMember(userId),
                HttpStatus.OK);
    }

    @GetMapping("/view-friend-requests")
    public ResponseEntity<List<SecretSantaFriendship>> getFriendshipRequests(@AuthenticationPrincipal Jwt source){
        return new ResponseEntity<>(secretSantaGroupService.getFriendshipRequests(source.getClaimAsString("sub")),
                HttpStatus.OK);
    }
    @GetMapping("/search-users")
    public ResponseEntity<List<SecretSantaUser>> getUser(@AuthenticationPrincipal Jwt jwt,
                                                         @RequestParam String name){
        return new ResponseEntity<>(userService.getUsers(name), HttpStatus.OK);
    }

    @PostMapping("/friend-request")
    public ResponseEntity<SecretSantaFriendship> sendFriendRequest(@AuthenticationPrincipal Jwt source,
                                                                   @RequestBody SecretSantaFriendship friendRequest) throws EntityNotCreated {
        friendRequest.setRequester(source.getClaimAsString("sub"));
        return new ResponseEntity<SecretSantaFriendship>(userService.sendFriendRequest(friendRequest),
                HttpStatus.CREATED);
    }

    @PostMapping("/process-request")
    public ResponseEntity<SecretSantaFriendship> processFriendshipRequest(@AuthenticationPrincipal Jwt source,
                                                                         @RequestBody SecretSantaFriendship friendRequest) throws Exception {
        if(!source.getClaimAsString("sub").equals(friendRequest.getRecipient()))
            throw new Exception("User identity is not valid");
        return new ResponseEntity<>(userService.processFriendshipRequest(friendRequest),
                HttpStatus.OK);

    }

    @PostMapping("/remove-friend")
    public ResponseEntity<SecretSantaFriendship> removeFriendship(@AuthenticationPrincipal Jwt source,
                                                                  @RequestBody SecretSantaUser friend){
        return new ResponseEntity<>(userService.removeFriend(source.getClaimAsString("sub"), friend),
                HttpStatus.OK);
    }

}
