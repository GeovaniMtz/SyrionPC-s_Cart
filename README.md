# Microservicio de Carrito (cart)

Este microservicio gestiona la lógica completa del carrito de compras, cumpliendo con el **Requerimiento 1 (Carrito de Compras)** del proyecto.

Martinez Martinez Geovani
Oritiz Menez Victor Gael
Flores Linares Oscar Daniel

## Requerimientos Cubiertos

| Funcionalidad | Método | Endpoint | Detalle |
| :--- | :--- | :--- | :--- |
| **Agregar/Incrementar** | `POST` | `/cart-item` | Agrega un nuevo producto o suma unidades si ya existe. Valida stock con la API Product. |
| **Consultar Carrito** | `GET` | `/cart-item` | Lista artículos, obteniendo nombre y precio unitario del Microservicio de Producto. |
| **Restar Unidades** | `PUT` | `/cart-item/{id}` | Resta la cantidad de unidades enviada. Si la cantidad total es ≤ 0, el artículo se elimina. |
| **Eliminar Ítem** | `DELETE` | `/cart-item/{id}` | Elimina el registro completo del artículo del carrito. |
| **Vaciar Carrito** | `DELETE` | `/cart-item` | Elimina todos los artículos del cliente. Usado por el Microservicio de Facturación. |

## Configuración

| Componente | Valor / Detalle |
| :--- | :--- |
| **Puerto** | `8082` |
| **Base de Datos** | MySQL (Tabla `cart_item`). |
| **Auth** | Spring Security. El `client_id` se extrae automáticamente del token JWT. |
| **API Product** | Se comunica con la API Product (`http://localhost:8080/v1`) usando `RestTemplate` para validar stock y obtener datos de visualización. |

## Uso

1.  **Dependencias:** Asegúrese de tener el conector MySQL y las librerías JWT (v0.12.6) en el `pom.xml`.
2.  **Configuración:** Verificar la URL del servicio de producto en `application.properties`.
3.  **Ejecución:** Ejecutar `CartApplication.java`.

### Prueba de Autenticación

Todas las peticiones a `/cart-item` deben incluir el header de autorización con un token JWT válido:

`Authorization: Bearer <TU_TOKEN_JWT>`

Igualmente se dejo la colección de postman a nivel del src para cualquier prueba necesaria, ese ya incluye pruebas para product, auth, y para carrito
