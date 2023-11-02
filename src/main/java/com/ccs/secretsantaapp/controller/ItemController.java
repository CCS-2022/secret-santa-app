package com.ccs.secretsantaapp.controller;

import com.ccs.secretsantaapp.dao.SecretSantaItem;
import com.ccs.secretsantaapp.exception.EntityNotCreated;
import com.ccs.secretsantaapp.repository.SecretSantaItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import javax.naming.CannotProceedException;
import java.util.List;

@RestController
@RequestMapping("/secret-santa/item")
public class ItemController {
    @Autowired
    private SecretSantaItemRepository secretSantaItemRepository;

    @GetMapping
    public ResponseEntity<List<SecretSantaItem>> getWishList(@AuthenticationPrincipal Jwt source){
        return new ResponseEntity<>(secretSantaItemRepository.getUserWishList(source.getClaimAsString("sub")),
                HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<List<SecretSantaItem>> saveItems(@AuthenticationPrincipal Jwt source,
                                                    @RequestBody List<SecretSantaItem> items){
        for(SecretSantaItem i : items){
            i.setUserId(source.getClaimAsString("sub"));
        }
        return new ResponseEntity<>(secretSantaItemRepository.saveAll(items), HttpStatus.OK);
    }

    @PostMapping("/update")
    public ResponseEntity<SecretSantaItem> updateItem(@AuthenticationPrincipal Jwt source,
                                                      @RequestBody SecretSantaItem item) throws Exception {
        if(!item.getUserId().equals(source.getClaimAsString("sub"))){
            throw new EntityNotCreated("Could not update");
        }
        secretSantaItemRepository.updateItem(item.getItemId(), item.getName(),
                item.getItemUrl(), item.getGroupId(), item.getUserId());
        return new ResponseEntity<>(item, HttpStatus.OK);

    }

    @PostMapping("/remove")
    public ResponseEntity<SecretSantaItem> removeItem(@AuthenticationPrincipal Jwt source,
                                                      @RequestBody SecretSantaItem item) throws CannotProceedException {
        if(!item.getUserId().equals(source.getClaimAsString("sub"))){
            throw new CannotProceedException("Could not remove");
        }
        secretSantaItemRepository.deleteById(item.getItemId());
        return new ResponseEntity<>(item, HttpStatus.OK);
    }
}
