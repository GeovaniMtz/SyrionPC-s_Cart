package com.cart.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DtoCartItemOut {

    @JsonProperty("cart_item_id")
    private Integer cartItemId;

    @JsonProperty("product_id")
    private Integer productId;

    @JsonProperty("product")
    private String product;

    @JsonProperty("price")
    private Float price;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("subtotal")
    private Float subtotal;

    // Constructor
    public DtoCartItemOut(Integer cartItemId, Integer productId, String product,
                          Float price, Integer quantity) {
        this.cartItemId = cartItemId;
        this.productId = productId;
        this.product = product;
        this.price = price;
        this.quantity = quantity;
        this.subtotal = price * quantity;
    }

    // Getters y Setters
    public Integer getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(Integer cartItemId) {
        this.cartItemId = cartItemId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Float getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Float subtotal) {
        this.subtotal = subtotal;
    }
}
