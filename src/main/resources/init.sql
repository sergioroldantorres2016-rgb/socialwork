CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    rol VARCHAR(50) DEFAULT 'Trabajador social',
    avatar VARCHAR(10) DEFAULT 'US',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS publicaciones (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    contenido TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS casos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    titulo VARCHAR(200) NOT NULL,
    descripcion TEXT,
    ubicacion VARCHAR(150),
    estado ENUM('activo','seguimiento','cerrado') DEFAULT 'activo',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comentarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    publicacion_id INT NULL,
    caso_id INT NULL,
    usuario_id INT NOT NULL,
    contenido TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (publicacion_id) REFERENCES publicaciones(id) ON DELETE CASCADE,
    FOREIGN KEY (caso_id) REFERENCES casos(id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS conexiones (
    id INT AUTO_INCREMENT PRIMARY KEY,
    seguidor_id INT NOT NULL,
    seguido_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_conexion (seguidor_id, seguido_id),
    FOREIGN KEY (seguidor_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (seguido_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS conversaciones (
    id INT AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS conversacion_participantes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    conversacion_id INT NOT NULL,
    usuario_id INT NOT NULL,
    UNIQUE KEY unique_participante (conversacion_id, usuario_id),
    FOREIGN KEY (conversacion_id) REFERENCES conversaciones(id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS mensajes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    conversacion_id INT NOT NULL,
    remitente_id INT NOT NULL,
    contenido TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (conversacion_id) REFERENCES conversaciones(id) ON DELETE CASCADE,
    FOREIGN KEY (remitente_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

INSERT IGNORE INTO usuarios (id, nombre, email, password_hash, rol, avatar) VALUES
(1, 'Sergio P.', 'sergio@email.com', '1234', 'Trabajador social', 'SP'),
(2, 'María García', 'maria@email.com', '1234', 'Trabajadora social', 'MG'),
(3, 'Javier Romero', 'javier@email.com', '1234', 'Coordinador SS', 'JR'),
(4, 'Ana López', 'ana@email.com', '1234', 'Trabajadora social', 'AL'),
(5, 'Paula Ruiz', 'paula@email.com', '1234', 'Educadora social', 'PR'),
(6, 'Diego M.', 'diego@email.com', '1234', 'Psicólogo', 'DM'),
(7, 'Admin', 'admin@socialwork.com', 'admin123', 'Admin', 'AD');

INSERT IGNORE INTO publicaciones (id, usuario_id, contenido) VALUES
(1, 2, 'Situación compleja con familia en riesgo de exclusión social en Vallecas. Menores implicados. ¿Alguien ha trabajado con UMES?'),
(2, 3, 'Nuevo protocolo de intervención en violencia de género publicado por la Comunidad de Madrid. Muy recomendable.');

INSERT IGNORE INTO casos (id, usuario_id, titulo, descripcion, ubicacion, estado) VALUES
(1, 1, 'Familia en riesgo — Vallecas Norte', 'Unidad familiar con 3 menores. Riesgo de desahucio.', 'Vallecas', 'activo'),
(2, 2, 'Menor no acompañado — Carabanchel', 'Chico de 16 años, proceso de tutela iniciado.', 'Carabanchel', 'seguimiento'),
(3, 3, 'Anciana en situación de soledad — Retiro', 'Mujer de 84 años, seguimiento completado.', 'Retiro', 'cerrado');

INSERT IGNORE INTO conversaciones (id) VALUES (1), (2);

INSERT IGNORE INTO conversacion_participantes (conversacion_id, usuario_id) VALUES
(1, 1), (1, 2),
(2, 1), (2, 3);

INSERT IGNORE INTO mensajes (conversacion_id, remitente_id, contenido) VALUES
(1, 2, 'Hola Sergio, te escribo por el caso de Vallecas.'),
(1, 1, 'Hola María! Sí, trabajé algo similar el año pasado.'),
(1, 2, '¿Pudiste contactar con la UMES?'),
(1, 1, 'Fue derivación del pediatra del centro de salud.'),
(2, 3, 'Sergio, ¿tienes el informe del caso Carabanchel?');

INSERT IGNORE INTO conexiones (seguidor_id, seguido_id) VALUES
(1, 2), (1, 3), (1, 4), (2, 1), (3, 1), (4, 1), (5, 1), (6, 1), (7, 1), (7, 2), (7, 3), (7, 4), (7, 5), (7, 6);

INSERT IGNORE INTO comentarios (id, publicacion_id, usuario_id, contenido) VALUES
(1, 1, 3, 'He trabajado con ellos en Usera. Buena experiencia.'),
(2, 1, 4, 'Contacta con la trabajadora social del centro de salud.');

INSERT IGNORE INTO comentarios (id, caso_id, usuario_id, contenido) VALUES
(3, 1, 2, 'He trabajado con familias similares en Usera. Podemos coordinar.'),
(4, 1, 4, 'Contacta con servicios sociales de Vallecas.'),
(5, 2, 1, 'Necesitamos una reunión para determinar la tutela.');