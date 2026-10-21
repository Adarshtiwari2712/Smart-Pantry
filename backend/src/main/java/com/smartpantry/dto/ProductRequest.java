package com.smartpantry.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class ProductRequest {

    @NotBlank(message = "User phone number is required")
    private String userId;

    @NotBlank(message = "Product name is required")
    private String productName;

    @NotBlank(message = "Category is required")
    private String category;

    @Min(value = 1, message = "Minimum units must be at least 1")
    private int units;

    @NotNull(message = "Expiry date is required")
    private LocalDate expiryDate;

    private String usageTip;

    public ProductRequest() {}

    public ProductRequest(String userId, String productName, String category, int units, LocalDate expiryDate, String usageTip) {
        this.userId = userId;
        this.productName = productName;
        this.category = category;
        this.units = units;
        this.expiryDate = expiryDate;
        this.usageTip = usageTip;
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
}