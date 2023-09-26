package com.ccs.SecretSantaApp.service;

import com.ccs.SecretSantaApp.dao.SecretSantaFriendship;
import com.ccs.SecretSantaApp.dao.SecretSantaGroup;
import com.ccs.SecretSantaApp.dao.SecretSantaGroupMember;
import com.ccs.SecretSantaApp.dao.SecretSantaUser;
import com.ccs.SecretSantaApp.repository.SecretSantaFriendshipRepository;
import com.ccs.SecretSantaApp.repository.SecretSantaGroupMemberRepository;
import com.ccs.SecretSantaApp.repository.SecretSantaGroupRepository;
import com.ccs.SecretSantaApp.repository.SecretSantaUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class SecretSantaGroupService {

    @Autowired
    private SecretSantaGroupRepository secretSantaGroupRepository;
    @Autowired
    private SecretSantaGroupMemberRepository secretSantaGroupMemberRepository;
    @Autowired
    private SecretSantaFriendshipRepository secretSantaFriendshipRepository;
    @Autowired
    private SecretSantaUserRepository secretSantaUserRepository;
    @Autowired
    private PairGenerator pairGenerator;
    @Autowired
    private EmailSenderService emailSenderService;

    public List<SecretSantaGroup> getAllGroupsByCreatorId(String userId) {
        return secretSantaGroupRepository.findByCreatorId(userId);
    }

    public List<SecretSantaGroup> getAllGroupsByGroupMember(String userId) {
        return secretSantaGroupRepository.findByMemberId(userId);
    }

    public SecretSantaGroup createGroup(SecretSantaGroup santaGroupRequest) {
        // Extract santaGroup from request
        SecretSantaGroup santaGroup = getSecretSantaGroupFromRequest(santaGroupRequest);

        Date createdDate = Date.valueOf(LocalDate.now());
        santaGroup.setDateCreated(createdDate);

        SecretSantaGroup secretSantaGroup = secretSantaGroupRepository.save(santaGroup);
        SecretSantaGroupMember secretSantaGroupMember = new SecretSantaGroupMember();

        // Add group creator as first group member and admin
        secretSantaGroupMember.setGroupId(secretSantaGroup.getGroupId());
        secretSantaGroupMember.setUserId(secretSantaGroup.getCreatorId());
        secretSantaGroupMember.setAdmin(true);
        saveGroupMember(secretSantaGroupMember);

        // Adding remainder members if any
        for(String memberId : santaGroupRequest.getMemberIds()){
            // check if the user exists and if it is friends with creator
            Optional<SecretSantaFriendship> secretSantaFriendship =
                    secretSantaFriendshipRepository.getFriendshipByPartiesId(santaGroup.getCreatorId(),
                            memberId,
                            true);

            if(secretSantaFriendship.isEmpty()) continue;

            secretSantaGroupMember = new SecretSantaGroupMember();
            secretSantaGroupMember.setAdmin(false);
            secretSantaGroupMember.setUserId(memberId);
            secretSantaGroupMember.setGroupId(secretSantaGroup.getGroupId());
            saveGroupMember(secretSantaGroupMember);
        }

        return secretSantaGroup;
    }

    public Optional<SecretSantaGroup> findGroupById(Long groupId){
        return secretSantaGroupRepository.findById(groupId);
    }

    public SecretSantaGroupMember saveGroupMember(SecretSantaGroupMember secretSantaGroupMember){
        return secretSantaGroupMemberRepository.save(secretSantaGroupMember);
    }

    private SecretSantaGroup getSecretSantaGroupFromRequest(SecretSantaGroup santaGroupRequest){
        SecretSantaGroup secretSantaGroup = new SecretSantaGroup();
        secretSantaGroup.setGroupName(santaGroupRequest.getGroupName());
        secretSantaGroup.setGroupName(santaGroupRequest.getGroupName());
        secretSantaGroup.setCreatorId(santaGroupRequest.getCreatorId());
        secretSantaGroup.setDateCreated(santaGroupRequest.getDateCreated());

        return secretSantaGroup;
    }

    public List<SecretSantaFriendship> getFriendshipRequests(String sub) {
      return secretSantaFriendshipRepository.findAllRequestsByUserId(sub);
    }

    public SecretSantaGroup addMembersToGroup(String sub, SecretSantaGroup santaGroupRequest) throws Exception {

        // Check if person adding members is an admin
        if(!secretSantaGroupMemberRepository.isAdmin(sub, santaGroupRequest.getGroupId())){
            throw new Exception("Cannot process request");
        }

        // Get group if it exists
        Optional<SecretSantaGroup> group = secretSantaGroupRepository.findById(santaGroupRequest.getGroupId());
        if(group.isEmpty()) throw new Exception("Cannot process request");


        for(String id : santaGroupRequest.getMemberIds()){
            Optional<SecretSantaUser> user = secretSantaUserRepository.findById(id);

            // Check if user exists
            if(user.isPresent()){
                Optional<SecretSantaGroupMember> member = secretSantaGroupMemberRepository.
                        findByUserIdAndGroupId(user.get().getUserId(),
                                santaGroupRequest.getGroupId());

                if(member.isEmpty()){
                    SecretSantaGroupMember groupMember = new SecretSantaGroupMember();
                    groupMember.setUserId(id);
                    groupMember.setGroupId(santaGroupRequest.getGroupId());
                    groupMember.setAdmin(false);
                    secretSantaGroupMemberRepository.save(groupMember);
                }
            }
        }
        return group.get();
    }

    public List<SecretSantaUser> getGroupMembers(Long id) {
        return secretSantaGroupMemberRepository.getGroupMembers(id);
    }

    public void shuffleGroup(Long groupId){
        ArrayList<SecretSantaUser> users = (ArrayList<SecretSantaUser>) secretSantaGroupMemberRepository.getGroupMembers(groupId);
        HashMap<SecretSantaUser, SecretSantaUser> pairs = pairGenerator.generatePairs(users);

        emailSenderService.sendEmails(pairs);
    }
}
