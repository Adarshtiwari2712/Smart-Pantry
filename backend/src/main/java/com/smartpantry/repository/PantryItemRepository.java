package com.smartpantry.repository;

import com.smartpantry.model.PantryItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface PantryItemRepository extends MongoRepository<PantryItem, String> {

    // Data range ke beech expire hone wale items find krega
    List<PantryItem> findByExpiryDateBetween(LocalDate startDate, LocalDate endDate);
}
