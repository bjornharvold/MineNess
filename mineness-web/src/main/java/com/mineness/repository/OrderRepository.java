package com.mineness.repository;

import com.mineness.domain.document.Order;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Bjorn Harvold
 * Date: 4/9/13
 * Time: 10:59 PM
 * Responsibility:
 */
@Repository
public interface OrderRepository extends PagingAndSortingRepository<Order, ObjectId> {

    @Query(value = "{ 'rdrd' : ?0 }")
    Order findByOrderId(String orderId);

}
