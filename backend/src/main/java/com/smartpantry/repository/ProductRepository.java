package com.smartpantry.repository;

import com.smartpantry.model.ProductItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface ProductRepository extends MongoRepository<ProductItem, String> {

    //ek specific user ke active products fetch krne ke liye (Dashboard ke liye)
    List<ProductItem> findByUserIdAndStatus(String userId, String status);

    //cron scheduler ke liye sare active products jo expire hone wale hain
    List<ProductItem> findByStatusAndExpiryDateLessThanEqual(String status, LocalDate date);

    List<ProductItem> findByStatusAndAlertStage(String status, int alertStage);

}
