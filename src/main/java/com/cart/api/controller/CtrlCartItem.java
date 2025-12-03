package com.cart.api.controller;

import com.cart.api.commons.dto.ApiResponse;
import com.cart.api.dto.DtoCartItemIn;
import com.cart.api.dto.DtoCartItemOut;
import com.cart.api.service.SvcCartItem;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Cart", description = "Operaciones relacionadas con el carrito de compras")
@RestController
@RequestMapping("/cart-item")
public class CtrlCartItem {

    @Autowired
    private SvcCartItem svc;

    /**
     * Agregar un producto al carrito de compras
     * POST /cart-item
     */
    @Operation(summary = "Agregar producto al carrito",
            description = "Agrega un producto al carrito o actualiza la cantidad si ya existe")
    @PostMapping
    public ResponseEntity<ApiResponse> addToCart(
            @Valid @RequestBody DtoCartItemIn in,
            Authentication authentication) {

        String clientId = authentication.getName(); // Obtener el username del JWT
        return new ResponseEntity<>(svc.addToCart(in, clientId), HttpStatus.CREATED);
    }

    /**
     * Consultar los productos del carrito
     * GET /cart-item
     */
    @Operation(summary = "Consultar productos del carrito",
            description = "Devuelve todos los productos agregados en el carrito del cliente")
    @GetMapping
    public ResponseEntity<List<DtoCartItemOut>> getCartItems(Authentication authentication) {
        String clientId = authentication.getName();
        return new ResponseEntity<>(svc.getCartItems(clientId), HttpStatus.OK);
    }

    /**
     * Eliminar un artículo del carrito
     * DELETE /cart-item/{id}
     */
    @Operation(summary = "Eliminar un artículo del carrito",
            description = "Elimina un artículo específico del carrito de compras")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCartItem(
            @PathVariable Integer id,
            Authentication authentication) {

        String clientId = authentication.getName();
        return new ResponseEntity<>(svc.deleteCartItem(id, clientId), HttpStatus.OK);
    }

    /**
     * Vaciar el carrito completo
     * DELETE /cart-item
     */
    @Operation(summary = "Vaciar el carrito",
            description = "Elimina todos los artículos del carrito de compras")
    @DeleteMapping
    public ResponseEntity<ApiResponse> clearCart(Authentication authentication) {
        String clientId = authentication.getName();
        return new ResponseEntity<>(svc.clearCart(clientId), HttpStatus.OK);
    }
}