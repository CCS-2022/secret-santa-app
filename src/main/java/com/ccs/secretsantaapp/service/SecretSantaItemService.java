package com.ccs.secretsantaapp.service;

import com.ccs.secretsantaapp.dao.SecretSantaFriendship;
import com.ccs.secretsantaapp.dao.SecretSantaItem;
import com.ccs.secretsantaapp.repository.SecretSantaFriendshipRepository;
import com.ccs.secretsantaapp.repository.SecretSantaItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.CannotProceedException;
import java.util.List;
import java.util.Optional;

@Service
public class SecretSantaItemService {

    @Autowired
    private SecretSantaFriendshipRepository secretSantaFriendshipRepository;

    @Autowired
    private SecretSantaItemRepository secretSantaItemRepository;

    public List<SecretSantaItem> getWishListByUser(String userId, String friendId) throws CannotProceedException {
        // Check if friendship exists
        Optional<SecretSantaFriendship> friendship = secretSantaFriendshipRepository
                .getFriendshipByPartiesId(userId, friendId, true);

        if(friendship.isPresent()){
            return secretSantaItemRepository.getUserWishList(friendId);
        } else throw new CannotProceedException();
    }
}
