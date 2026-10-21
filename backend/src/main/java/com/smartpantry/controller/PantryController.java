package com.smartpantry.controller;

import com.smartpantry.dto.ApiResponse;
import com.smartpantry.dto.ProductRequest;
import com.smartpantry.model.ProductItem;
import com.smartpantry.service.AiVisionService;
import com.smartpantry.service.ExpiryNotificationScheduler;
import com.smartpantry.service.PantryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pantry")
public class PantryController {

    private final PantryService pantryService;

    public PantryController(PantryService pantryService){
        this.pantryService = pantryService;
    }

    //Add Product: POST http://localhost:8084/api/pantry/add

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<ProductItem>> addProduct(@RequestBody ProductRequest request){
        ApiResponse<ProductItem> response = pantryService.addProduct(request);
        return ResponseEntity.ok(response);
    }

    // 2. Get User Pantry Items: GET http://localhost:8084/api/pantry/user/{userId}  <--- (MISSING THA)
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<ProductItem>>> getUserItems(@PathVariable String userId){
        ApiResponse<List<ProductItem>> response = pantryService.getActiveItems(userId);
        return ResponseEntity.ok(response);
    }

    //Get Active Items: Get http://localhost:8084/api/pantry/consume/{ProductId}
    @PutMapping("/consume/{productId}")
    public ResponseEntity<ApiResponse<ProductItem>> consumeUnit(@PathVariable String productId){
        ApiResponse<ProductItem> response = pantryService.consumeUnit(productId);
        if (response.isSuccess()){
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    @Autowired
    private AiVisionService aiVisionService;

    @PostMapping("/scan")
    public ResponseEntity<?> scanProductImage(@RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please upload a valid image file.");
        }

        try {
            String mimeType = file.getContentType() != null ? file.getContentType() : "image/jpeg";
            Map<String, String> details = aiVisionService.extractExpiryDetails(file.getBytes(), mimeType);

            if (details.containsKey("error")) {
                return ResponseEntity.internalServerError().body(details.get("error"));
            }

            return ResponseEntity.ok(details);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error reading file: " + e.getMessage());
        }
    }

    @Autowired
    private ExpiryNotificationScheduler expiryNotificationScheduler;

    @GetMapping("/test-expiry-alert")
    public ResponseEntity<String> testAlert() {
        expiryNotificationScheduler.checkAndSendExpiryAlerts();
        return ResponseEntity.ok("Expiry alerts triggered successfully! Check console & email.");
    }
}
