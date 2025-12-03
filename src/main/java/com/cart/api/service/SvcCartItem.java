package com.cart.api.service;

import com.cart.api.commons.dto.ApiResponse;
import com.cart.api.dto.DtoCartItemIn;
import com.cart.api.dto.DtoCartItemOut;

import java.util.List;

public interface SvcCartItem {

    /**
     * Agrega un producto al carrito o actualiza la cantidad si ya existe
     * @param in DTO con product_id y quantity
     * @param clientId ID del cliente obtenido del token JWT
     * @return ApiResponse con mensaje de confirmación
     */
    ApiResponse addToCart(DtoCartItemIn in, String clientId);

    /**
     * Obtiene todos los items del carrito de un cliente
     * @param clientId ID del cliente obtenido del token JWT
     * @return Lista de items del carrito con información del producto
     */
    List<DtoCartItemOut> getCartItems(String clientId);

    /**
     * Elimina un item específico del carrito
     * @param id ID del cart_item a eliminar
     * @param clientId ID del cliente obtenido del token JWT
     * @return ApiResponse con mensaje de confirmación
     */
    ApiResponse deleteCartItem(Integer id, String clientId);

    /**
     * Vacía completamente el carrito de un cliente
     * @param clientId ID del cliente obtenido del token JWT
     * @return ApiResponse con mensaje de confirmación
     */
    ApiResponse clearCart(String clientId);

    /**
     * Actualiza la cantidad de un item específico en el carrito
     * @param cartItemId ID del item en la tabla cart_item
     * @param newQuantity La nueva cantidad total deseada para este producto
     * @param clientId ID del cliente obtenido del token JWT
     * @return ApiResponse con mensaje de confirmación
     */
    ApiResponse updateCartItemQuantity(Integer cartItemId, Integer newQuantity, String clientId);

}