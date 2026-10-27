
package com.smartpantry.repository;

import com.smartpantry.model.ProductItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProductRepository
        extends MongoRepository<ProductItem, String> {

    // Fetch active products belonging to a particular user
    List<ProductItem> findByUserIdAndStatus(
            String userId,
            String status
    );

    // Fetch active products expiring on or before a given date
    List<ProductItem> findByStatusAndExpiryDateLessThanEqual(
            String status,
            LocalDate date
    );

    // Find products by their current reminder stage
    List<ProductItem> findByStatusAndAlertStage(
            String status,
            int alertStage
    );

    // Find products with an exact expiry date and reminder stage
    List<ProductItem> findByStatusAndExpiryDateAndAlertStage(
            String status,
            LocalDate expiryDate,
            int alertStage
    );
}
