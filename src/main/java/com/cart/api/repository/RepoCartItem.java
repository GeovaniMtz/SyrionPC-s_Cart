package com.cart.api.repository;

import com.cart.api.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepoCartItem extends JpaRepository<CartItem, Integer> {

    // Consulta de carrito, ordenado por más reciente.
    List<CartItem> findByClientIdOrderByCreatedAtDesc(String clientId);

    //Buscar para actualizar cantidad.
    Optional<CartItem> findByClientIdAndProductId(String clientId, Integer productId);

    //Vaciar carrito (REQUIERE ANOTACIONES).
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM cart_item WHERE client_id = :clientId", nativeQuery = true)
    void deleteAllByClientId(@Param("clientId") String clientId);

    //Check de seguridad para DELETE /{id}
    boolean existsByIdAndClientId(Integer id, String clientId); // Spring lo resuelve implícitamente

}