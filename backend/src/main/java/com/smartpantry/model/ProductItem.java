package com.smartpantry.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "tracked_products")
public class ProductItem {

    @Id
    private String id;
    private String userId; // User ka mobile number
    private String productName;
    private String category; // Dairy, Bakery, Pharmacy etc.
    private int units; // Multi-pack counter
    private LocalDate expiryDate;
    private String usageTip; // AI recipe/safety suggestion

    private String status; // Active, Expiring Soon, Consumed_On_Time
    private int alertStage;

    private LocalDateTime createdAt;

    public ProductItem(){
        this.status = "ACTIVE";
        this.alertStage = 0;
        this.createdAt = LocalDateTime.now();
    }

    public ProductItem(String userId, String productName, String category, int units, LocalDate expiryDate, String usageTip){
        this.userId = userId;
        this.productName = productName;
        this.category = category;
        this.units = units;
        this.expiryDate = expiryDate;
        this.usageTip = usageTip;
        this.status = "ACTIVE";
        this.alertStage = 0;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getUsageTip() {
        return usageTip;
    }

    public void setUsageTip(String usageTip) {
        this.usageTip = usageTip;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getAlertStage() {
        return alertStage;
    }

    public void setAlertStage(int alertStage) {
        this.alertStage = alertStage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
