-- =====================================================
-- 1. ELIMINAR I CREAR LA BASE DE DADES
-- =====================================================

DROP DATABASE IF EXISTS hard_track_db;

CREATE DATABASE hard_track_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE hard_track_db;


-- =====================================================
-- 2. TAULA USERS
-- =====================================================

DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(45),
    password VARCHAR(45),
    enable TINYINT(1)
) ENGINE=InnoDB;


-- =====================================================
-- 3. TAULA AUTHORITIES
-- =====================================================

DROP TABLE IF EXISTS authorities;

CREATE TABLE authorities (
    id_user INT NOT NULL,
    authority VARCHAR(45) NOT NULL,

    PRIMARY KEY (id_user, authority),

    CONSTRAINT fk_authorities_user
        FOREIGN KEY (id_user)
        REFERENCES users(id)
) ENGINE=InnoDB;


-- =====================================================
-- 4. TAULA DEVICE_TYPE
-- =====================================================

DROP TABLE IF EXISTS device_type;

CREATE TABLE device_type (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(45)
) ENGINE=InnoDB;


-- =====================================================
-- 5. DADES INICIALS DE DEVICE_TYPE
-- =====================================================

INSERT INTO device_type (name) VALUES
    ('Ratón'),
    ('Pantalla'),
    ('Teclado'),
    ('Impresora Laser b/n');


-- =====================================================
-- 6. TAULA DEVICE
-- =====================================================

DROP TABLE IF EXISTS device;

CREATE TABLE device (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_type INT NOT NULL,
    device_description VARCHAR(200),
    state TINYINT(1),

    CONSTRAINT fk_type_device
        FOREIGN KEY (id_type)
        REFERENCES device_type(id)
) ENGINE=InnoDB;


-- =====================================================
-- 7. COMPROVAR LES DADES INICIALS
-- =====================================================

SELECT * FROM device_type;
