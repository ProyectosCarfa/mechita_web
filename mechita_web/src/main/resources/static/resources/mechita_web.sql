CREATE DATABASE mechita_web WITH ENCODING 'UTF8' TEMPLATE template0;

\connect mechita_web

CREATE TABLE roles (
    id_rol BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE usuarios (
    id_usuario BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_rol BIGINT NOT NULL,
    correo VARCHAR(120) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_usuarios_roles FOREIGN KEY (id_rol) REFERENCES roles (id_rol)
);

CREATE TABLE clientes (
    id_cliente BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_usuario BIGINT NOT NULL UNIQUE,
    nombres VARCHAR(80) NOT NULL,
    apellidos VARCHAR(80) NOT NULL,
    dni CHAR(8) UNIQUE,
    telefono VARCHAR(15),
    CONSTRAINT fk_clientes_usuarios FOREIGN KEY (id_usuario) REFERENCES usuarios (id_usuario) ON DELETE CASCADE,
    CONSTRAINT ck_clientes_dni CHECK (dni ~ '^[0-9]{8}$')
);

CREATE TABLE departamentos (
    id_departamento BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre VARCHAR(60) NOT NULL UNIQUE
);

CREATE TABLE provincias (
    id_provincia BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_departamento BIGINT NOT NULL,
    nombre VARCHAR(60) NOT NULL,
    CONSTRAINT uq_provincias UNIQUE (id_departamento, nombre),
    CONSTRAINT fk_provincias_departamentos FOREIGN KEY (id_departamento) REFERENCES departamentos (id_departamento)
);

CREATE TABLE distritos (
    id_distrito BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_provincia BIGINT NOT NULL,
    nombre VARCHAR(60) NOT NULL,
    CONSTRAINT uq_distritos UNIQUE (id_provincia, nombre),
    CONSTRAINT fk_distritos_provincias FOREIGN KEY (id_provincia) REFERENCES provincias (id_provincia)
);

CREATE TABLE direcciones (
    id_direccion BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_cliente BIGINT NOT NULL,
    id_distrito BIGINT NOT NULL,
    direccion VARCHAR(200) NOT NULL,
    referencia VARCHAR(200),
    CONSTRAINT fk_direcciones_clientes FOREIGN KEY (id_cliente) REFERENCES clientes (id_cliente) ON DELETE CASCADE,
    CONSTRAINT fk_direcciones_distritos FOREIGN KEY (id_distrito) REFERENCES distritos (id_distrito)
);

CREATE TABLE categorias (
    id_categoria BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    imagen_url VARCHAR(255),
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE productos (
    id_producto BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_categoria BIGINT NOT NULL,
    nombre VARCHAR(120) NOT NULL,
    descripcion TEXT,
    precio NUMERIC(10,2) NOT NULL,
    stock INTEGER NOT NULL DEFAULT 0,
    imagen_url VARCHAR(255),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_productos_categorias FOREIGN KEY (id_categoria) REFERENCES categorias (id_categoria),
    CONSTRAINT ck_productos_precio CHECK (precio >= 0),
    CONSTRAINT ck_productos_stock CHECK (stock >= 0)
);

CREATE TABLE carritos (
    id_carrito BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_cliente BIGINT NOT NULL UNIQUE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_carritos_clientes FOREIGN KEY (id_cliente) REFERENCES clientes (id_cliente) ON DELETE CASCADE
);

CREATE TABLE carrito_items (
    id_carrito BIGINT NOT NULL,
    id_producto BIGINT NOT NULL,
    cantidad INTEGER NOT NULL DEFAULT 1,
    PRIMARY KEY (id_carrito, id_producto),
    CONSTRAINT fk_carrito_items_carritos FOREIGN KEY (id_carrito) REFERENCES carritos (id_carrito) ON DELETE CASCADE,
    CONSTRAINT fk_carrito_items_productos FOREIGN KEY (id_producto) REFERENCES productos (id_producto),
    CONSTRAINT ck_carrito_items_cantidad CHECK (cantidad > 0)
);

CREATE TABLE estados_pedido (
    id_estado BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE pedidos (
    id_pedido BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_cliente BIGINT NOT NULL,
    id_direccion BIGINT NOT NULL,
    id_estado BIGINT NOT NULL,
    fecha_pedido TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pedidos_clientes FOREIGN KEY (id_cliente) REFERENCES clientes (id_cliente),
    CONSTRAINT fk_pedidos_direcciones FOREIGN KEY (id_direccion) REFERENCES direcciones (id_direccion),
    CONSTRAINT fk_pedidos_estados FOREIGN KEY (id_estado) REFERENCES estados_pedido (id_estado)
);

CREATE TABLE detalle_pedido (
    id_pedido BIGINT NOT NULL,
    id_producto BIGINT NOT NULL,
    cantidad INTEGER NOT NULL,
    precio_unitario NUMERIC(10,2) NOT NULL,
    PRIMARY KEY (id_pedido, id_producto),
    CONSTRAINT fk_detalle_pedidos FOREIGN KEY (id_pedido) REFERENCES pedidos (id_pedido) ON DELETE CASCADE,
    CONSTRAINT fk_detalle_productos FOREIGN KEY (id_producto) REFERENCES productos (id_producto),
    CONSTRAINT ck_detalle_cantidad CHECK (cantidad > 0),
    CONSTRAINT ck_detalle_precio CHECK (precio_unitario >= 0)
);

CREATE TABLE metodos_pago (
    id_metodo_pago BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre VARCHAR(40) NOT NULL UNIQUE
);

CREATE TABLE pagos (
    id_pago BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_pedido BIGINT NOT NULL UNIQUE,
    id_metodo_pago BIGINT NOT NULL,
    monto NUMERIC(10,2) NOT NULL,
    fecha_pago TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    referencia VARCHAR(80),
    CONSTRAINT fk_pagos_pedidos FOREIGN KEY (id_pedido) REFERENCES pedidos (id_pedido),
    CONSTRAINT fk_pagos_metodos FOREIGN KEY (id_metodo_pago) REFERENCES metodos_pago (id_metodo_pago),
    CONSTRAINT ck_pagos_monto CHECK (monto >= 0)
);

CREATE INDEX idx_usuarios_rol ON usuarios (id_rol);
CREATE INDEX idx_provincias_departamento ON provincias (id_departamento);
CREATE INDEX idx_distritos_provincia ON distritos (id_provincia);
CREATE INDEX idx_direcciones_cliente ON direcciones (id_cliente);
CREATE INDEX idx_direcciones_distrito ON direcciones (id_distrito);
CREATE INDEX idx_productos_categoria ON productos (id_categoria);
CREATE INDEX idx_carrito_items_producto ON carrito_items (id_producto);
CREATE INDEX idx_pedidos_cliente ON pedidos (id_cliente);
CREATE INDEX idx_pedidos_direccion ON pedidos (id_direccion);
CREATE INDEX idx_pedidos_estado ON pedidos (id_estado);
CREATE INDEX idx_pedidos_fecha ON pedidos (fecha_pedido);
CREATE INDEX idx_detalle_producto ON detalle_pedido (id_producto);
CREATE INDEX idx_pagos_metodo ON pagos (id_metodo_pago);

CREATE VIEW vista_total_pedidos AS
SELECT p.id_pedido,
       p.id_cliente,
       p.id_estado,
       p.fecha_pedido,
       SUM(d.cantidad * d.precio_unitario) AS total
FROM pedidos p
JOIN detalle_pedido d ON d.id_pedido = p.id_pedido
GROUP BY p.id_pedido, p.id_cliente, p.id_estado, p.fecha_pedido;

INSERT INTO roles (nombre) VALUES ('ADMIN'), ('CLIENTE');

INSERT INTO estados_pedido (nombre) VALUES ('PENDIENTE'), ('CONFIRMADO'), ('EN_PREPARACION'), ('ENVIADO'), ('ENTREGADO'), ('CANCELADO');

INSERT INTO metodos_pago (nombre) VALUES ('EFECTIVO'), ('YAPE'), ('PLIN'), ('TRANSFERENCIA'), ('TARJETA');

INSERT INTO departamentos (nombre) VALUES ('Piura');

INSERT INTO provincias (id_departamento, nombre)
SELECT id_departamento, 'Piura' FROM departamentos WHERE nombre = 'Piura';

INSERT INTO distritos (id_provincia, nombre)
SELECT id_provincia, d.nombre
FROM provincias
CROSS JOIN (VALUES ('Piura'), ('Castilla'), ('Veintiséis de Octubre'), ('Catacaos'), ('Las Lomas')) AS d (nombre)
WHERE provincias.nombre = 'Piura';
