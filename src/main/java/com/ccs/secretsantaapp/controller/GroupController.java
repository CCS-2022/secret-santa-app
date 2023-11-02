package com.ccs.secretsantaapp.controller;


import com.ccs.secretsantaapp.dao.SecretSantaGroupMember;
import com.ccs.secretsantaapp.dao.SecretSantaUser;
import com.ccs.secretsantaapp.repository.SecretSantaGroupMemberRepository;
import com.ccs.secretsantaapp.repository.SecretSantaUserRepository;
import com.ccs.secretsantaapp.service.SecretSantaGroupService;
import com.ccs.secretsantaapp.dao.SecretSantaGroup;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Maps all operations related to the groups.
 * E.G. : Creating a group, Adding people to group, view group by id, etc...
 */
@RestController
@RequestMapping("/secret-santa/group")
public class GroupController {

    @Autowired
    private SecretSantaGroupService secretSantaGroupService;
    @Autowired
    private SecretSantaUserRepository secretSantaUserRepository;

    @Autowired
    private SecretSantaGroupMemberRepository secretSantaGroupMemberRepository;

    @GetMapping
    public ResponseEntity<List<SecretSantaUser>> getGroupMembers(@RequestParam Long id,
                                                                 @AuthenticationPrincipal Jwt source){
        Optional<SecretSantaGroupMember> user = secretSantaGroupMemberRepository.
                findByUserIdAndGroupId(source.getClaimAsString("sub"), id);
        if(user.isEmpty()) return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
        return new ResponseEntity<>(secretSantaGroupService.getGroupMembers(id), HttpStatus.OK);
    }

    @GetMapping("/shuffle")
    public ResponseEntity<HttpStatus> shuffleGroup(@AuthenticationPrincipal Jwt source,
                                                   @RequestParam Long groupId){
        Optional<SecretSantaGroupMember> user = secretSantaGroupMemberRepository.
                findByUserIdAndGroupId(source.getClaimAsString("sub"), groupId);
        if(user.isEmpty() || Boolean.FALSE.equals(user.get().getAdmin())) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        secretSantaGroupService.shuffleGroup(groupId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/delete")
    public ResponseEntity<HttpStatus> deleteGroup(@AuthenticationPrincipal Jwt source,
                                                   @RequestParam Long groupId){
        Optional<SecretSantaGroupMember> user = secretSantaGroupMemberRepository.
                findByUserIdAndGroupId(source.getClaimAsString("sub"), groupId);
        if(user.isEmpty() || Boolean.FALSE.equals(user.get().getAdmin())) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        secretSantaGroupService.deleteGroup(groupId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/admin-check")
    public ResponseEntity<Boolean> isAdmin(@AuthenticationPrincipal Jwt source,
                                           @RequestParam Long groupId,
                                           @RequestParam String userId){
        Optional<SecretSantaGroupMember> user = secretSantaGroupMemberRepository.
                findByUserIdAndGroupId(source.getClaimAsString("sub"), groupId);
        if(user.isEmpty()) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        user = secretSantaGroupMemberRepository.findByUserIdAndGroupId(userId, groupId);

        if(user.isEmpty()) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        return new ResponseEntity<>(user.get().getAdmin(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<SecretSantaGroup> createGroup(@RequestBody SecretSantaGroup santaGroupRequest,
                                                        @AuthenticationPrincipal Jwt source){
        String principalId = source.getClaimAsString("sub");
        if(!principalId.equals(santaGroupRequest.getCreatorId())){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(secretSantaGroupService.createGroup(santaGroupRequest), HttpStatus.CREATED);
    }

    @PostMapping("/add-members")
    public ResponseEntity<SecretSantaGroup> addMembersToGroup(@AuthenticationPrincipal Jwt source,
                                                              @RequestBody SecretSantaGroup santaGroupRequest) throws Exception {
        return new ResponseEntity<>(secretSantaGroupService.addMembersToGroup(source.getClaimAsString("sub"),
                santaGroupRequest), HttpStatus.OK);
    }


}
