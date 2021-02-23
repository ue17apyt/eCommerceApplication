package com.example.demo.model.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ModifyCartRequest {

    @JsonProperty
    private String username;

    @JsonProperty
    private long itemId;

    @JsonProperty
    private int quantity;

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getItemId() {
        return this.itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}