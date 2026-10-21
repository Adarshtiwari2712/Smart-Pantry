package com.smartpantry.service;

import com.smartpantry.dto.ApiResponse;
import com.smartpantry.dto.ProductRequest;
import com.smartpantry.model.ProductItem;
import com.smartpantry.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PantryService {

    private final ProductRepository productRepository;

    public PantryService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // 1. Add Product (Ensure status is always ACTIVE)
    public ApiResponse<ProductItem> addProduct(ProductRequest request) {
        // Phone number sanitize karein agar zaroorat ho
        String sanitizedUserId = request.getUserId() != null ? request.getUserId().trim() : "";

        ProductItem item = new ProductItem(
                sanitizedUserId,
                request.getProductName(),
                request.getCategory(),
                request.getUnits(),
                request.getExpiryDate(),
                request.getUsageTip()
        );

        // Explicitly set ACTIVE status taaki query fail na ho
        item.setStatus("ACTIVE");

        ProductItem savedItem = productRepository.save(item);
        return ApiResponse.success("Product successfully add ho gya!", savedItem);
    }

    // 2. Fetch Active Items (Handle decoded + sign)
    public ApiResponse<List<ProductItem>> getActiveItems(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return ApiResponse.error("User ID provide nahi ki gayi!");
        }

        // Agar URL decode ne '+' ko space bana diya ho, use wapas '+' karein
        String cleanUserId = userId.trim();
        if (cleanUserId.startsWith("91") && !cleanUserId.startsWith("+91")) {
            cleanUserId = "+" + cleanUserId;
        }

        List<ProductItem> items = productRepository.findByUserIdAndStatus(cleanUserId, "ACTIVE");
        return ApiResponse.success("Active items fetched successfully", items);
    }

    // 3. Consume Unit Logic
    public ApiResponse<ProductItem> consumeUnit(String productId) {
        Optional<ProductItem> optionalItem = productRepository.findById(productId);

        if (optionalItem.isEmpty()) {
            return ApiResponse.error("Product nahi mila!");
        }

        ProductItem item = optionalItem.get();
        int remainingUnits = item.getUnits() - 1;

        if (remainingUnits <= 0) {
            item.setUnits(0);
            item.setStatus("CONSUMED_ON_TIME");
            productRepository.save(item);
            return ApiResponse.success("Product poora consume ho gya! (Restock suggested)", item);
        } else {
            item.setUnits(remainingUnits);
            ProductItem updatedItem = productRepository.save(item);
            return ApiResponse.success("1 unit consumed successfully", updatedItem);
        }
    }
}