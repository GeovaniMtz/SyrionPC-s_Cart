package com.cart.api.service;

import com.cart.api.commons.dto.ApiResponse;
import com.cart.api.dto.DtoCartItemIn;
import com.cart.api.dto.DtoCartItemOut;
import com.cart.api.dto.DtoProductInfo; // DTO auxiliar (ver punto 2)
import com.cart.api.entity.CartItem;
import com.cart.api.exception.ApiException;
import com.cart.api.repository.RepoCartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SvcCartItemImp implements SvcCartItem {

    @Autowired
    private RepoCartItem repo;

    @Autowired
    private RestTemplate restTemplate;

    // Obtiene la URL base del servicio de productos desde application.properties
    @Value("${product.service.url}")
    private String productServiceUrl;

    @Override
    public ApiResponse addToCart(DtoCartItemIn in, String clientId) {
        try {
            // 1. Validar que el producto existe y obtener su información (precio, stock, estado)
            DtoProductInfo productInfo = getProductInfo(in.getProductId());

            // 2. Validar que el producto esté activo
            if (productInfo.getStatus() != 1) {
                throw new ApiException(HttpStatus.BAD_REQUEST,
                        "El producto no está disponible para la venta");
            }

            // 3. Buscar si el artículo ya existe para el cliente
            Optional<CartItem> existingItemOpt = repo.findByClientIdAndProductId(clientId, in.getProductId());

            Integer newQuantity = in.getQuantity();
            Integer finalQuantity = newQuantity;

            CartItem itemToSave = existingItemOpt.orElseGet(CartItem::new);

            if (existingItemOpt.isPresent()) {
                // Si existe: calculamos la cantidad total (requerimiento 1.50)
                finalQuantity = existingItemOpt.get().getQuantity() + newQuantity;
            } else {
                // Si es nuevo: configuramos el nuevo item
                itemToSave.setClientId(clientId);
                itemToSave.setProductId(in.getProductId());
            }

            // 4. Validar que la CANTIDAD TOTAL no exceda el stock [REQ. 1.49]
            if (finalQuantity > productInfo.getStock()) {
                // Excepción con detalles del stock disponible
                throw new ApiException(HttpStatus.BAD_REQUEST,
                        "Stock insuficiente. Stock disponible: " + productInfo.getStock() +
                                ". Cantidad en el carrito si se agrega: " + finalQuantity);
            }

            // 5. Persistencia
            itemToSave.setQuantity(finalQuantity);
            repo.save(itemToSave);

            return new ApiResponse("Producto " + productInfo.getProduct() + " agregado al carrito. Cantidad total: " + finalQuantity);

        } catch (ApiException e) {
            // Propagar excepciones de negocio (producto no encontrado, stock insuficiente, etc.)
            throw e;
        } catch (DataAccessException e) {
            // Capturar errores de BD
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error de base de datos al guardar el item: " + e.getMessage());
        }
    }

    @Override
    public List<DtoCartItemOut> getCartItems(String clientId) {
        // Lógica para el GET /cart-item [REQ. 1.2 y 1.51]
        List<CartItem> cartItems = repo.findByClientIdOrderByCreatedAtDesc(clientId);
        List<DtoCartItemOut> outList = new ArrayList<>();

        // Se recorre la lista del carrito y se consulta el API Product para obtener nombre y precio
        for (CartItem item : cartItems) {
            DtoProductInfo productInfo = getProductInfo(item.getProductId());

            // Crear el DTO de salida con los datos combinados
            outList.add(new DtoCartItemOut(
                    item.getId(),
                    item.getProductId(),
                    productInfo.getProduct(), // Nombre del producto [REQ. 1.51]
                    productInfo.getPrice(), // Costo unitario [REQ. 1.51]
                    item.getQuantity()
            ));
        }

        return outList;
    }

    @Override
    public ApiResponse deleteCartItem(Integer id, String clientId) {
        // Lógica para el DELETE /cart-item/{id} [REQ. 1.3]
        if (!repo.existsByIdAndClientId(id, clientId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Item de carrito no encontrado o no pertenece al cliente");
        }

        try {
            repo.deleteById(id);
            return new ApiResponse("Item de carrito eliminado");
        } catch (DataAccessException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error de base de datos al eliminar el item: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse clearCart(String clientId) {
        // Lógica para el DELETE /cart-item (vaciar carrito) [REQ. 1.4]
        try {
            repo.deleteAllByClientId(clientId); // Uso de tu método @Modifying/@Transactional
            return new ApiResponse("El carrito ha sido vaciado");

        } catch (DataAccessException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error de base de datos al vaciar el carrito: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse updateCartItemQuantity(Integer cartItemId, Integer quantityToSubtract, String clientId) {

        // Validar la cantidad a restar
        if (quantityToSubtract == null || quantityToSubtract <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "La cantidad a restar debe ser al menos 1.");
        }

        // Buscar el item y validar que pertenezca al cliente
        Optional<CartItem> itemOpt = repo.findById(cartItemId);

        if (itemOpt.isEmpty() || !itemOpt.get().getClientId().equals(clientId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Item de carrito no encontrado o no pertenece al cliente");
        }

        CartItem itemToUpdate = itemOpt.get();
        Integer existingQuantity = itemToUpdate.getQuantity();

        // Calcular la nueva cantidad total (EXISTENTE - CANTIDAD_A_RESTAR)
        Integer newTotalQuantity = existingQuantity - quantityToSubtract;

        // Si el total es 0 o negativo, eliminamos el item (DELETE)
        if (newTotalQuantity <= 0) {
            repo.deleteById(cartItemId);
            return new ApiResponse("Producto eliminado completamente del carrito.");
        }

        // Si la nueva cantidad es positiva, guardamos la actualización
        itemToUpdate.setQuantity(newTotalQuantity);
        repo.save(itemToUpdate);

        return new ApiResponse("Se restaron " + quantityToSubtract + " unidades. Cantidad restante: " + newTotalQuantity);
    }

    // Método auxiliar para obtener información de un producto desde la API Product
    private DtoProductInfo getProductInfo(Integer productId) {
        try {
            String url = productServiceUrl + "/product/" + productId;

            // Se asume que el API Product tiene un endpoint que devuelve la información
            DtoProductInfo productInfo = restTemplate.getForObject(url, DtoProductInfo.class);

            if (productInfo == null) {
                // Si el producto no se encuentra (404), restTemplate lanza una excepción,
                // pero lo manejo para ser explícito.
                throw new ApiException(HttpStatus.NOT_FOUND,
                        "El producto con ID " + productId + " no existe");
            }

            return productInfo;
        } catch (Exception e) {
            // Captura las excepciones de RestTemplate
            throw new ApiException(HttpStatus.NOT_FOUND,
                    "El producto con ID " + productId + " no existe o el servicio de productos no está disponible: " + e.getMessage());
        }
    }
}