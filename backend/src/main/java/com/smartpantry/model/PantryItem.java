package com.smartpantry.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;


@Document(collection = "pantry_items")
public class PantryItem {

    @Id
    private String id;
    private String name;
    private String userEmail;
    private LocalDate expiryDate;
    private  int quantity;

    public PantryItem() {}

    public PantryItem(String name, String userEmail, LocalDate expiryDate, int quantity){
        this.name = name;
        this.userEmail = userEmail;
        this.expiryDate = expiryDate;
        this.quantity = quantity;
    }

    // Getter and Setters
    public String getId(){
        return id;
    }
    public void setId(String id){
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
