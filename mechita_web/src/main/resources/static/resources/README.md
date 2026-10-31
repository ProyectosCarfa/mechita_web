# Base de datos mechita_web

Base de datos en PostgreSQL para el sistema web de gestión y venta de Productos Piuranos Mechita E.I.R.L.

## Entidades principales

| Entidad | Descripción |
|---|---|
| `roles` | Tipos de usuario del sistema: `ADMIN` y `CLIENTE`. |
| `usuarios` | Cuentas de acceso. Guardan correo, contraseña encriptada y rol. |
| `clientes` | Datos personales del usuario que compra: nombres, apellidos, DNI y teléfono. |
| `direcciones` | Direcciones de entrega de cada cliente. Un cliente puede tener varias. |
| `categorias` | Grupos en los que se clasifican los productos. |
| `productos` | Catálogo de la empresa: nombre, descripción, precio, stock e imagen. |
| `carritos` | Carrito de compras de cada cliente. Es uno por cliente. |
| `carrito_items` | Productos y cantidades que el cliente tiene en su carrito. |
| `pedidos` | Compras confirmadas por el cliente, con su dirección de entrega y estado. |
| `detalle_pedido` | Productos de cada pedido, con cantidad y el precio al momento de la compra. |
| `pagos` | Pago de cada pedido: monto, fecha, método y referencia. |

## Entidades de apoyo

| Entidad | Descripción |
|---|---|
| `estados_pedido` | Estados posibles de un pedido: pendiente, confirmado, en preparación, enviado, entregado y cancelado. |
| `metodos_pago` | Formas de pago: efectivo, Yape, Plin, transferencia y tarjeta. |
| `departamentos`, `provincias`, `distritos` | Ubicación geográfica usada en las direcciones. |

## Cómo se relacionan

- Un rol tiene muchos usuarios, y un usuario cliente tiene un registro en clientes.
- Un cliente tiene un carrito, varias direcciones y varios pedidos.
- Una categoría agrupa muchos productos.
- Un pedido tiene varios productos en detalle_pedido y un pago.

## Vista

- `vista_total_pedidos`: calcula el total de cada pedido sumando cantidad por precio de su detalle.

