CREATE DATABASE IF NOT EXISTS levelup_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE levelup_db;

CREATE TABLE IF NOT EXISTS categoria (
    id_categoria BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    descripcion VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS producto (
    id_producto BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    precio INT NOT NULL CHECK (precio >= 0),
    stock INT NOT NULL CHECK (stock >= 0),
    imagen VARCHAR(500),
    id_categoria BIGINT NOT NULL,
    activo TINYINT(1) NOT NULL DEFAULT 1,
    CONSTRAINT fk_producto_categoria FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria)
);

CREATE TABLE IF NOT EXISTS usuario (
    id_usuario BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol INT NOT NULL DEFAULT 2 CHECK (rol IN (1, 2)),
    activo TINYINT(1) NOT NULL DEFAULT 1,
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS boleta (
    id_boleta BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
    subtotal INT NOT NULL CHECK (subtotal >= 0),
    descuento INT NOT NULL DEFAULT 0 CHECK (descuento >= 0),
    total INT NOT NULL CHECK (total >= 0),
    estado VARCHAR(20) NOT NULL DEFAULT 'PAGADA',
    direccion_envio VARCHAR(255) NOT NULL,
    telefono_contacto VARCHAR(20) NOT NULL,
    id_usuario BIGINT NOT NULL,
    metodo_pago VARCHAR(50) NOT NULL,
    CONSTRAINT fk_boleta_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

CREATE TABLE IF NOT EXISTS detalle_boleta (
    id_detalle BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_boleta BIGINT NOT NULL,
    id_producto BIGINT NOT NULL,
    cantidad INT NOT NULL CHECK (cantidad > 0),
    precio_unitario INT NOT NULL CHECK (precio_unitario >= 0),
    subtotal INT NOT NULL CHECK (subtotal >= 0),
    CONSTRAINT fk_detalle_boleta FOREIGN KEY (id_boleta) REFERENCES boleta(id_boleta),
    CONSTRAINT fk_detalle_producto FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
);

CREATE TABLE IF NOT EXISTS noticia (
    id_noticia BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    contenido TEXT NOT NULL,
    imagen_url VARCHAR(500),
    fecha_publicacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    id_usuario BIGINT NOT NULL,
    CONSTRAINT fk_noticia_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

CREATE TABLE IF NOT EXISTS comentario (
    id_comentario BIGINT AUTO_INCREMENT PRIMARY KEY,
    contenido TEXT NOT NULL,
    fecha_comentario DATETIME DEFAULT CURRENT_TIMESTAMP,
    id_noticia BIGINT NOT NULL,
    id_usuario BIGINT NOT NULL,
    CONSTRAINT fk_comentario_noticia FOREIGN KEY (id_noticia) REFERENCES noticia(id_noticia),
    CONSTRAINT fk_comentario_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

DELIMITER //
CREATE TRIGGER trg_descontar_stock AFTER INSERT ON detalle_boleta
FOR EACH ROW
BEGIN
    UPDATE producto
    SET stock = stock - NEW.cantidad
    WHERE id_producto = NEW.id_producto;
END//
DELIMITER ;

INSERT INTO categoria (nombre, descripcion) VALUES
('Consolas', 'Consolas de última generación'),
('Periféricos', 'Teclados, mouse y audífonos gamer');

INSERT INTO producto (nombre, descripcion, precio, stock, imagen, id_categoria, activo) VALUES
('PlayStation 5 Slim', 'Consola PS5 edición con lector de discos 1TB', 549990, 10, 'https://ejemplo.com/ps5.jpg', 1, 1),
('Teclado Mecánico RGB', 'Switches Red, formato TKL', 59990, 25, 'https://ejemplo.com/teclado.jpg', 2, 1);

INSERT INTO usuario (nombre, email, password, rol, activo) VALUES
('Admin LevelUp', 'admin@levelup.cl', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 1, 1),
('Cliente Prueba', 'cliente@levelup.cl', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 2, 1);

CREATE USER IF NOT EXISTS 'levelup'@'localhost' IDENTIFIED BY '1234';
GRANT ALL PRIVILEGES ON levelup_db.* TO 'levelup'@'localhost';
FLUSH PRIVILEGES;