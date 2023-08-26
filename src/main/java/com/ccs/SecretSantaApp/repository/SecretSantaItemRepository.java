package com.ccs.SecretSantaApp.repository;

import com.ccs.SecretSantaApp.dao.SecretSantaItem;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Meta;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface SecretSantaItemRepository extends JpaRepository<SecretSantaItem, Long> {

    @Query(value = "SELECT i FROM SecretSantaItem i " +
            "WHERE i.userId = :userId")
    List<SecretSantaItem> getUserWishList(@Param("userId") String userId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE SecretSantaItem i " +
            "SET name = :itemName, " +
            "itemUrl = :itemUrl, " +
            "groupId = :groupId " +
            "WHERE userId = :userId " +
            "AND itemId = :itemId")
    void updateItem(@Param("itemId") Long itemId,
                    @Param("itemName") String itemName,
                    @Param("itemUrl") String itemUrl,
                    @Param("groupId") Long groupId,
                    @Param("userId") String userId);
}
