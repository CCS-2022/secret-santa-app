package com.ccs.secretsantaapp.service;

import com.ccs.secretsantaapp.dao.SecretSantaFriendship;
import com.ccs.secretsantaapp.dao.SecretSantaItem;
import com.ccs.secretsantaapp.dao.SecretSantaUser;
import com.ccs.secretsantaapp.repository.SecretSantaFriendshipRepository;
import com.ccs.secretsantaapp.repository.SecretSantaItemRepository;
import com.ccs.secretsantaapp.repository.SecretSantaUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.naming.CannotProceedException;
import java.util.List;
import java.util.Optional;

@Service
public class SecretSantaItemService {

    @Autowired
    private SecretSantaFriendshipRepository secretSantaFriendshipRepository;
    @Autowired
    private EmailSenderService emailSenderService;
    @Autowired
    private SecretSantaItemRepository secretSantaItemRepository;
    @Autowired
    private SecretSantaUserRepository secretSantaUserRepository;

    public List<SecretSantaItem> getWishListByUser(String userId, String friendId) throws CannotProceedException {
        // Check if friendship exists
        Optional<SecretSantaFriendship> friendship = secretSantaFriendshipRepository
                .getFriendshipByPartiesId(userId, friendId, true);

        // If friendship does not exist, error out
        if(friendship.isPresent()){
            return secretSantaItemRepository.getUserWishList(friendId);
        } else throw new CannotProceedException();
    }

    public HttpStatus getWishListByEmail(String userId, String friendId) {
        // Check if friendship exists
        Optional<SecretSantaFriendship> friendship = secretSantaFriendshipRepository
                .getFriendshipByPartiesId(userId, friendId, true);

        if(friendship.isPresent()){
            StringBuilder message = new StringBuilder();
            Optional<SecretSantaUser> friend = secretSantaUserRepository.findById(friendId);
            if(friend.isPresent()) {
                message.append("Here is your requested wishlist! \n" + "Wishlist owner: ")
                        .append(friend.get().getFirstName())
                        .append(" ")
                        .append(friend.get().getLastName())
                        .append("\n\n");

                List<SecretSantaItem> wishlist = secretSantaItemRepository.getUserWishList(friendId);

                for (SecretSantaItem item : wishlist) {
                    message.append("Item name: ")
                            .append(item.getName())
                            .append("\n");
                    message.append("URL: ")
                            .append(item.getItemUrl())
                            .append("\n\n");
                }
                emailSenderService.sendEmailToUser(userId, message.toString());
                return HttpStatus.OK;
            }
        }
        return HttpStatus.UNAUTHORIZED;
    }
}
