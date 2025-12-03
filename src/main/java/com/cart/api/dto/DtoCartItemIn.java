package com.cart.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class DtoCartItemIn {

    @JsonProperty("product_id")
    @NotNull(message = "El product_id es obligatorio")
    private Integer productId;

    @JsonProperty("quantity")
    @NotNull(message = "La quantity es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer quantity;

    // Getters y Setters
    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {

        this.quantity = quantity;
    }
}