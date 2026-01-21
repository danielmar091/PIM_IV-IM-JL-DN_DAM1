-- ============================================
-- BASE DE DATOS VIDAJOVE - 1º DAM PIM
-- Proyecto Intermodular 2º Trimestre
-- ============================================

-- Creación de la base de datos
CREATE DATABASE IF NOT EXISTS vidajove_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE vidajove_db;

-- ============================================
-- TABLAS PRINCIPALES
-- ============================================

-- Tabla de usuarios del sistema
CREATE TABLE usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(150) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    fecha_nacimiento DATE,
    telefono VARCHAR(15),
    rol ENUM('admin', 'editor', 'usuario') DEFAULT 'usuario',
    activo BOOLEAN DEFAULT TRUE,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ultima_conexion TIMESTAMP NULL,
    INDEX idx_email (email),
    INDEX idx_rol (rol)
) ENGINE=InnoDB;

-- Tabla de categorías para noticias
CREATE TABLE categorias (
    id_categoria INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion TEXT,
    color VARCHAR(7) DEFAULT '#667eea',
    icono VARCHAR(50),
    activa BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Tabla de noticias
CREATE TABLE noticias (
    id_noticia INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    subtitulo VARCHAR(250),
    contenido TEXT NOT NULL,
    imagen_url VARCHAR(255),
    id_categoria INT NOT NULL,
    id_autor INT NOT NULL,
    estado ENUM('borrador', 'publicada', 'archivada') DEFAULT 'borrador',
    destacada BOOLEAN DEFAULT FALSE,
    visitas INT DEFAULT 0,
    fecha_publicacion TIMESTAMP NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_categoria) REFERENCES categorias(id_categoria) ON DELETE RESTRICT,
    FOREIGN KEY (id_autor) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    INDEX idx_categoria (id_categoria),
    INDEX idx_autor (id_autor),
    INDEX idx_estado (estado),
    INDEX idx_fecha_pub (fecha_publicacion),
    FULLTEXT idx_busqueda (titulo, contenido)
) ENGINE=InnoDB;

-- Tabla de eventos
CREATE TABLE eventos (
    id_evento INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    descripcion TEXT NOT NULL,
    lugar VARCHAR(200),
    fecha_inicio DATETIME NOT NULL,
    fecha_fin DATETIME NOT NULL,
    capacidad_maxima INT,
    plazas_disponibles INT,
    imagen_url VARCHAR(255),
    id_organizador INT NOT NULL,
    precio DECIMAL(10,2) DEFAULT 0.00,
    requiere_inscripcion BOOLEAN DEFAULT FALSE,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_organizador) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    INDEX idx_fecha (fecha_inicio),
    INDEX idx_organizador (id_organizador)
) ENGINE=InnoDB;

-- Tabla de inscripciones a eventos
CREATE TABLE inscripciones (
    id_inscripcion INT AUTO_INCREMENT PRIMARY KEY,
    id_evento INT NOT NULL,
    id_usuario INT NOT NULL,
    fecha_inscripcion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado ENUM('pendiente', 'confirmada', 'cancelada') DEFAULT 'pendiente',
    observaciones TEXT,
    FOREIGN KEY (id_evento) REFERENCES eventos(id_evento) ON DELETE CASCADE,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    UNIQUE KEY unique_inscripcion (id_evento, id_usuario),
    INDEX idx_evento (id_evento),
    INDEX idx_usuario (id_usuario)
) ENGINE=InnoDB;

-- Tabla de comentarios en noticias
CREATE TABLE comentarios (
    id_comentario INT AUTO_INCREMENT PRIMARY KEY,
    id_noticia INT NOT NULL,
    id_usuario INT NOT NULL,
    contenido TEXT NOT NULL,
    aprobado BOOLEAN DEFAULT FALSE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_noticia) REFERENCES noticias(id_noticia) ON DELETE CASCADE,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    INDEX idx_noticia (id_noticia),
    INDEX idx_usuario (id_usuario)
) ENGINE=InnoDB;

-- Tabla de recursos/documentos
CREATE TABLE recursos (
    id_recurso INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    descripcion TEXT,
    tipo_recurso ENUM('beca', 'convocatoria', 'subvencion', 'formacion') NOT NULL,
    archivo_url VARCHAR(255),
    fecha_limite DATE,
    enlace_externo VARCHAR(255),
    id_creador INT NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_creador) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    INDEX idx_tipo (tipo_recurso),
    INDEX idx_fecha_limite (fecha_limite)
) ENGINE=InnoDB;

-- ============================================
-- CREACIÓN DE USUARIOS Y PERMISOS
-- ============================================

-- Usuario administrador con todos los privilegios
CREATE USER IF NOT EXISTS 'vidajove_admin'@'localhost' 
IDENTIFIED BY 'VJ_Admin2025!';

GRANT ALL PRIVILEGES ON vidajove_db.* 
TO 'vidajove_admin'@'localhost';

-- Usuario para la aplicación con permisos limitados
CREATE USER IF NOT EXISTS 'vidajove_app'@'localhost' 
IDENTIFIED BY 'VJ_App2025!';

GRANT SELECT, INSERT, UPDATE, DELETE ON vidajove_db.* 
TO 'vidajove_app'@'localhost';

-- Usuario de solo lectura para reportes
CREATE USER IF NOT EXISTS 'vidajove_readonly'@'localhost' 
IDENTIFIED BY 'VJ_Read2025!';

GRANT SELECT ON vidajove_db.* 
TO 'vidajove_readonly'@'localhost';

FLUSH PRIVILEGES;

-- ============================================
-- DATOS DE PRUEBA
-- ============================================

-- Insertar categorías
INSERT INTO categorias (nombre, descripcion, color, icono) VALUES
('Cultura', 'Eventos y actividades culturales', '#FF6B6B', '🎭'),
('Deportes', 'Actividades deportivas y competiciones', '#4ECDC4', '⚽'),
('Formación', 'Cursos, talleres y becas', '#45B7D1', '📚'),
('Empleo', 'Ofertas de trabajo y emprendimiento', '#FFA07A', '💼'),
('Ocio', 'Actividades de tiempo libre', '#98D8C8', '🎉');

-- Insertar usuarios de prueba
INSERT INTO usuarios (nombre, apellidos, email, password_hash, fecha_nacimiento, rol) VALUES
('Admin', 'Sistema', 'admin@vidajove.com', '$2y$10$abcdefghijklmnopqrstuvwxyz1234567890', '1990-01-01', 'admin'),
('María', 'García López', 'maria.garcia@email.com', '$2y$10$abcdefghijklmnopqrstuvwxyz1234567890', '2000-05-15', 'editor'),
('Carlos', 'Martínez Sanz', 'carlos.martinez@email.com', '$2y$10$abcdefghijklmnopqrstuvwxyz1234567890', '1999-08-20', 'editor'),
('Laura', 'Fernández Ruiz', 'laura.fernandez@email.com', '$2y$10$abcdefghijklmnopqrstuvwxyz1234567890', '2001-03-10', 'usuario'),
('David', 'Rodríguez Torres', 'david.rodriguez@email.com', '$2y$10$abcdefghijklmnopqrstuvwxyz1234567890', '2000-11-25', 'usuario');

-- Insertar noticias de prueba
INSERT INTO noticias (titulo, subtitulo, contenido, id_categoria, id_autor, estado, destacada, fecha_publicacion) VALUES
('Gran Festival Juvenil 2025', 'El evento más esperado del año', 
 'Se aproxima el evento más esperado del año. Música, arte y cultura en un solo lugar. Este festival reunirá a los mejores artistas emergentes de la región y contará con actividades para todos los gustos. No te lo pierdas!', 
 1, 2, 'publicada', TRUE, NOW()),

('Nuevo Programa de Becas', 'Convocatoria abierta hasta marzo', 
 'Abierta la convocatoria para becas de estudio. Consulta los requisitos y plazos. El programa ofrece ayudas de hasta 3000€ para estudiantes con buen rendimiento académico.', 
 3, 2, 'publicada', TRUE, NOW()),

('Taller de Emprendimiento', 'Desarrolla tu idea de negocio', 
 'Aprende a desarrollar tu idea de negocio con expertos del sector. El taller incluye sesiones de mentoring personalizado y networking con inversores.', 
 4, 3, 'publicada', FALSE, NOW()),

('Torneo de Fútbol Sala', 'Inscripciones abiertas', 
 'Ya puedes inscribir a tu equipo en el torneo de fútbol sala más importante de la ciudad. Premios para los tres primeros clasificados.', 
 2, 3, 'publicada', FALSE, NOW()),

('Concierto Benéfico', 'Recaudación para causas sociales', 
 'Gran concierto benéfico con artistas locales. Todo lo recaudado irá destinado a proyectos sociales para jóvenes en situación vulnerable.', 
 5, 2, 'publicada', FALSE, NOW());

-- Insertar eventos de prueba
INSERT INTO eventos (titulo, descripcion, lugar, fecha_inicio, fecha_fin, capacidad_maxima, plazas_disponibles, id_organizador, precio, requiere_inscripcion) VALUES
('Festival Juvenil 2025', 'El mayor festival de música y cultura juvenil', 
 'Auditorio Municipal', '2025-03-15 18:00:00', '2025-03-15 23:59:00', 500, 350, 2, 15.00, TRUE),

('Taller de Emprendimiento', 'Aprende a crear tu startup', 
 'Centro de Innovación', '2025-02-10 10:00:00', '2025-02-10 14:00:00', 30, 15, 3, 0.00, TRUE),

('Torneo Fútbol Sala', 'Competición deportiva', 
 'Polideportivo Norte', '2025-02-20 09:00:00', '2025-02-20 20:00:00', 100, 60, 3, 5.00, TRUE);

-- Insertar recursos de prueba
INSERT INTO recursos (titulo, descripcion, tipo_recurso, fecha_limite, enlace_externo, id_creador) VALUES
('Becas Formación 2025', 'Programa de becas para estudios superiores', 'beca', '2025-03-31', 'https://becas.vidajove.com', 2),
('Convocatoria Empleo Joven', 'Ayudas para la contratación de jóvenes', 'convocatoria', '2025-02-28', 'https://empleo.vidajove.com', 2),
('Subvención Proyectos', 'Financiación para proyectos juveniles', 'subvencion', '2025-04-15', 'https://proyectos.vidajove.com', 3);

-- ============================================
-- VISTAS ÚTILES
-- ============================================

-- Vista de noticias publicadas con información completa
CREATE VIEW v_noticias_publicadas AS
SELECT 
    n.id_noticia,
    n.titulo,
    n.subtitulo,
    n.contenido,
    n.imagen_url,
    n.destacada,
    n.visitas,
    n.fecha_publicacion,
    c.nombre AS categoria,
    c.color AS color_categoria,
    CONCAT(u.nombre, ' ', u.apellidos) AS autor,
    u.email AS email_autor
FROM noticias n
INNER JOIN categorias c ON n.id_categoria = c.id_categoria
INNER JOIN usuarios u ON n.id_autor = u.id_usuario
WHERE n.estado = 'publicada'
ORDER BY n.fecha_publicacion DESC;

-- Vista de eventos activos
CREATE VIEW v_eventos_activos AS
SELECT 
    e.id_evento,
    e.titulo,
    e.descripcion,
    e.lugar,
    e.fecha_inicio,
    e.fecha_fin,
    e.capacidad_maxima,
    e.plazas_disponibles,
    e.precio,
    CONCAT(u.nombre, ' ', u.apellidos) AS organizador,
    (e.capacidad_maxima - e.plazas_disponibles) AS inscritos
FROM eventos e
INNER JOIN usuarios u ON e.id_organizador = u.id_usuario
WHERE e.activo = TRUE AND e.fecha_inicio >= CURDATE()
ORDER BY e.fecha_inicio ASC;

-- ============================================
-- PROCEDIMIENTOS ALMACENADOS
-- ============================================

DELIMITER //

-- Procedimiento para incrementar visitas de noticia
CREATE PROCEDURE sp_incrementar_visitas(IN p_id_noticia INT)
BEGIN
    UPDATE noticias 
    SET visitas = visitas + 1 
    WHERE id_noticia = p_id_noticia;
END //

-- Procedimiento para inscribir usuario a evento
CREATE PROCEDURE sp_inscribir_evento(
    IN p_id_evento INT,
    IN p_id_usuario INT,
    OUT p_resultado VARCHAR(100)
)
BEGIN
    DECLARE v_plazas INT;
    
    SELECT plazas_disponibles INTO v_plazas
    FROM eventos
    WHERE id_evento = p_id_evento AND activo = TRUE;
    
    IF v_plazas IS NULL THEN
        SET p_resultado = 'ERROR: Evento no encontrado o inactivo';
    ELSEIF v_plazas <= 0 THEN
        SET p_resultado = 'ERROR: No hay plazas disponibles';
    ELSE
        INSERT INTO inscripciones (id_evento, id_usuario, estado)
        VALUES (p_id_evento, p_id_usuario, 'confirmada');
        
        UPDATE eventos 
        SET plazas_disponibles = plazas_disponibles - 1
        WHERE id_evento = p_id_evento;
        
        SET p_resultado = 'OK: Inscripción realizada correctamente';
    END IF;
END //

DELIMITER ;

-- ============================================
-- ÍNDICES ADICIONALES PARA OPTIMIZACIÓN
-- ============================================

CREATE INDEX idx_noticias_destacadas ON noticias(destacada, fecha_publicacion);
CREATE INDEX idx_eventos_fecha_activo ON eventos(fecha_inicio, activo);

-- ============================================
-- SCRIPT COMPLETADO
-- ============================================