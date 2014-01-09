package com.mineness.repository;

import com.mineness.domain.document.PurchasedItem;
import org.bson.types.ObjectId;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Bjorn Harvold
 * Date: 4/9/13
 * Time: 10:59 PM
 * Responsibility:
 */
@Repository
public interface PurchasedItemRepository extends PagingAndSortingRepository<PurchasedItem, ObjectId> {

}
