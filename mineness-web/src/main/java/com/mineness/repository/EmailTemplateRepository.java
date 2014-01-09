package com.mineness.repository;

import com.mineness.domain.document.EmailTemplate;
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
public interface EmailTemplateRepository extends PagingAndSortingRepository<EmailTemplate, ObjectId> {

}
