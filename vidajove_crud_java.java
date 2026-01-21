// ============================================
// VIDAJOVE - CRUD COMPLETO
// Proyecto Intermodular 1º DAM - 2º Trimestre
// ============================================

// ============================================
// 1. CLASE DE CONFIGURACIÓN DE BASE DE DATOS
// ============================================
package com.vidajove.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    private static final String URL = System.getenv("DB_URL") != null 
        ? System.getenv("DB_URL") 
        : "jdbc:mysql://localhost:3306/vidajove_db?useSSL=false&serverTimezone=UTC";
    
    private static final String USER = System.getenv("DB_USER") != null 
        ? System.getenv("DB_USER") 
        : "vidajove_app";
    
    private static final String PASSWORD = System.getenv("DB_PASSWORD") != null 
        ? System.getenv("DB_PASSWORD") 
        : "VJ_App2025!";
    
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL no encontrado", e);
        }
    }
    
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar conexión: " + e.getMessage());
            }
        }
    }
}

// ============================================
// 2. MODELO - CLASE NOTICIA
// ============================================
package com.vidajove.model;

import java.time.LocalDateTime;

public class Noticia {
    private int idNoticia;
    private String titulo;
    private String subtitulo;
    private String contenido;
    private String imagenUrl;
    private int idCategoria;
    private int idAutor;
    private String estado;
    private boolean destacada;
    private int visitas;
    private LocalDateTime fechaPublicacion;
    private LocalDateTime fechaCreacion;
    
    // Constructor vacío
    public Noticia() {}
    
    // Constructor completo
    public Noticia(String titulo, String subtitulo, String contenido, 
                   int idCategoria, int idAutor) {
        this.titulo = titulo;
        this.subtitulo = subtitulo;
        this.contenido = contenido;
        this.idCategoria = idCategoria;
        this.idAutor = idAutor;
        this.estado = "borrador";
        this.destacada = false;
        this.visitas = 0;
    }
    
    // Getters y Setters
    public int getIdNoticia() { return idNoticia; }
    public void setIdNoticia(int idNoticia) { this.idNoticia = idNoticia; }
    
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    public String getSubtitulo() { return subtitulo; }
    public void setSubtitulo(String subtitulo) { this.subtitulo = subtitulo; }
    
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    
    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }
    
    public int getIdAutor() { return idAutor; }
    public void setIdAutor(int idAutor) { this.idAutor = idAutor; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public boolean isDestacada() { return destacada; }
    public void setDestacada(boolean destacada) { this.destacada = destacada; }
    
    public int getVisitas() { return visitas; }
    public void setVisitas(int visitas) { this.visitas = visitas; }
    
    public LocalDateTime getFechaPublicacion() { return fechaPublicacion; }
    public void setFechaPublicacion(LocalDateTime fechaPublicacion) { 
        this.fechaPublicacion = fechaPublicacion; 
    }
    
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { 
        this.fechaCreacion = fechaCreacion; 
    }
    
    @Override
    public String toString() {
        return "Noticia{" +
                "id=" + idNoticia +
                ", titulo='" + titulo + '\'' +
                ", estado='" + estado + '\'' +
                ", visitas=" + visitas +
                '}';
    }
}

// ============================================
// 3. DAO - INTERFAZ CRUD GENÉRICA
// ============================================
package com.vidajove.dao;

import java.util.List;

public interface CrudDAO<T> {
    boolean crear(T objeto);
    T obtenerPorId(int id);
    List<T> obtenerTodos();
    boolean actualizar(T objeto);
    boolean eliminar(int id);
}

// ============================================
// 4. DAO - IMPLEMENTACIÓN NOTICIA DAO
// ============================================
package com.vidajove.dao;

import com.vidajove.config.DatabaseConfig;
import com.vidajove.model.Noticia;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NoticiaDAO implements CrudDAO<Noticia> {
    
    // CREATE - Crear nueva noticia
    @Override
    public boolean crear(Noticia noticia) {
        String sql = "INSERT INTO noticias (titulo, subtitulo, contenido, imagen_url, " +
                     "id_categoria, id_autor, estado, destacada) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, noticia.getTitulo());
            pstmt.setString(2, noticia.getSubtitulo());
            pstmt.setString(3, noticia.getContenido());
            pstmt.setString(4, noticia.getImagenUrl());
            pstmt.setInt(5, noticia.getIdCategoria());
            pstmt.setInt(6, noticia.getIdAutor());
            pstmt.setString(7, noticia.getEstado());
            pstmt.setBoolean(8, noticia.isDestacada());
            
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        noticia.setIdNoticia(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("Error al crear noticia: " + e.getMessage());
            return false;
        }
    }
    
    // READ - Obtener noticia por ID
    @Override
    public Noticia obtenerPorId(int id) {
        String sql = "SELECT * FROM noticias WHERE id_noticia = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapearNoticia(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener noticia: " + e.getMessage());
        }
        return null;
    }
    
    // READ - Obtener todas las noticias
    @Override
    public List<Noticia> obtenerTodos() {
        List<Noticia> noticias = new ArrayList<>();
        String sql = "SELECT * FROM noticias ORDER BY fecha_creacion DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                noticias.add(mapearNoticia(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener noticias: " + e.getMessage());
        }
        return noticias;
    }
    
    // UPDATE - Actualizar noticia
    @Override
    public boolean actualizar(Noticia noticia) {
        String sql = "UPDATE noticias SET titulo = ?, subtitulo = ?, contenido = ?, " +
                     "imagen_url = ?, id_categoria = ?, estado = ?, destacada = ? " +
                     "WHERE id_noticia = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, noticia.getTitulo());
            pstmt.setString(2, noticia.getSubtitulo());
            pstmt.setString(3, noticia.getContenido());
            pstmt.setString(4, noticia.getImagenUrl());
            pstmt.setInt(5, noticia.getIdCategoria());
            pstmt.setString(6, noticia.getEstado());
            pstmt.setBoolean(7, noticia.isDestacada());
            pstmt.setInt(8, noticia.getIdNoticia());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar noticia: " + e.getMessage());
            return false;
        }
    }
    
    // DELETE - Eliminar noticia
    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM noticias WHERE id_noticia = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar noticia: " + e.getMessage());
            return false;
        }
    }
    
    // Métodos adicionales específicos
    
    public List<Noticia> obtenerPorCategoria(int idCategoria) {
        List<Noticia> noticias = new ArrayList<>();
        String sql = "SELECT * FROM noticias WHERE id_categoria = ? " +
                     "AND estado = 'publicada' ORDER BY fecha_publicacion DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idCategoria);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                noticias.add(mapearNoticia(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener noticias por categoría: " + e.getMessage());
        }
        return noticias;
    }
    
    public List<Noticia> obtenerDestacadas() {
        List<Noticia> noticias = new ArrayList<>();
        String sql = "SELECT * FROM noticias WHERE destacada = TRUE " +
                     "AND estado = 'publicada' ORDER BY fecha_publicacion DESC LIMIT 5";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                noticias.add(mapearNoticia(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener noticias destacadas: " + e.getMessage());
        }
        return noticias;
    }
    
    public boolean publicarNoticia(int idNoticia) {
        String sql = "UPDATE noticias SET estado = 'publicada', " +
                     "fecha_publicacion = NOW() WHERE id_noticia = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idNoticia);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al publicar noticia: " + e.getMessage());
            return false;
        }
    }
    
    public boolean incrementarVisitas(int idNoticia) {
        String sql = "{CALL sp_incrementar_visitas(?)}";
        
        try (Connection conn = DatabaseConfig.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setInt(1, idNoticia);
            return cstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al incrementar visitas: " + e.getMessage());
            return false;
        }
    }
    
    public List<Noticia> buscarPorTexto(String textoBusqueda) {
        List<Noticia> noticias = new ArrayList<>();
        String sql = "SELECT * FROM noticias WHERE " +
                     "MATCH(titulo, contenido) AGAINST(? IN NATURAL LANGUAGE MODE) " +
                     "AND estado = 'publicada'";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, textoBusqueda);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                noticias.add(mapearNoticia(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar noticias: " + e.getMessage());
        }
        return noticias;
    }
    
    // Método auxiliar para mapear ResultSet a Noticia
    private Noticia mapearNoticia(ResultSet rs) throws SQLException {
        Noticia noticia = new Noticia();
        noticia.setIdNoticia(rs.getInt("id_noticia"));
        noticia.setTitulo(rs.getString("titulo"));
        noticia.setSubtitulo(rs.getString("subtitulo"));
        noticia.setContenido(rs.getString("contenido"));
        noticia.setImagenUrl(rs.getString("imagen_url"));
        noticia.setIdCategoria(rs.getInt("id_categoria"));
        noticia.setIdAutor(rs.getInt("id_autor"));
        noticia.setEstado(rs.getString("estado"));
        noticia.setDestacada(rs.getBoolean("destacada"));
        noticia.setVisitas(rs.getInt("visitas"));
        
        Timestamp fechaPub = rs.getTimestamp("fecha_publicacion");
        if (fechaPub != null) {
            noticia.setFechaPublicacion(fechaPub.toLocalDateTime());
        }
        
        Timestamp fechaCrea = rs.getTimestamp("fecha_creacion");
        if (fechaCrea != null) {
            noticia.setFechaCreacion(fechaCrea.toLocalDateTime());
        }
        
        return noticia;
    }
}

// ============================================
// 5. CLASE PRINCIPAL - PRUEBAS DEL CRUD
// ============================================
package com.vidajove;

import com.vidajove.dao.NoticiaDAO;
import com.vidajove.model.Noticia;
import java.util.List;
import java.util.Scanner;

public class MainApp {
    
    private static NoticiaDAO noticiaDAO = new NoticiaDAO();
    private static Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        boolean salir = false;
        
        while (!salir) {
            mostrarMenu();
            int opcion = leerOpcion();
            
            switch (opcion) {
                case 1: crearNoticia(); break;
                case 2: listarNoticias(); break;
                case 3: buscarNoticiaPorId(); break;
                case 4: actualizarNoticia(); break;
                case 5: eliminarNoticia(); break;
                case 6: publicarNoticia(); break;
                case 7: buscarPorTexto(); break;
                case 8: listarDestacadas(); break;
                case 0: salir = true; break;
                default: System.out.println("Opción no válida");
            }
        }
        
        System.out.println("¡Hasta pronto!");
        scanner.close();
    }
    
    private static void mostrarMenu() {
        System.out.println("\n=== VIDAJOVE - GESTIÓN DE NOTICIAS ===");
        System.out.println("1. Crear nueva noticia");
        System.out.println("2. Listar todas las noticias");
        System.out.println("3. Buscar noticia por ID");
        System.out.println("4. Actualizar noticia");
        System.out.println("5. Eliminar noticia");
        System.out.println("6. Publicar noticia");
        System.out.println("7. Buscar por texto");
        System.out.println("8. Listar destacadas");
        System.out.println("0. Salir");
        System.out.print("Seleccione una opción: ");
    }
    
    private static int leerOpcion() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    private static void crearNoticia() {
        System.out.println("\n--- CREAR NUEVA NOTICIA ---");
        
        System.out.print("Título: ");
        String titulo = scanner.nextLine();
        
        System.out.print("Subtítulo: ");
        String subtitulo = scanner.nextLine();
        
        System.out.print("Contenido: ");
        String contenido = scanner.nextLine();
        
        System.out.print("ID Categoría (1-5): ");
        int idCategoria = Integer.parseInt(scanner.nextLine());
        
        System.out.print("ID Autor: ");
        int idAutor = Integer.parseInt(scanner.nextLine());
        
        Noticia noticia = new Noticia(titulo, subtitulo, contenido, idCategoria, idAutor);
        
        if (noticiaDAO.crear(noticia)) {
            System.out.println("✓ Noticia creada con ID: " + noticia.getIdNoticia());
        } else {
            System.out.println("✗ Error al crear la noticia");
        }
    }
    
    private static void listarNoticias() {
        System.out.println("\n--- LISTADO DE NOTICIAS ---");
        List<Noticia> noticias = noticiaDAO.obtenerTodos();
        
        if (noticias.isEmpty()) {
            System.out.println("No hay noticias registradas");
        } else {
            for (Noticia n : noticias) {
                System.out.println(n);
            }
        }
    }
    
    private static void buscarNoticiaPorId() {
        System.out.print("\nIngrese ID de noticia: ");
        int id = Integer.parseInt(scanner.nextLine());
        
        Noticia noticia = noticiaDAO.obtenerPorId(id);
        if (noticia != null) {
            System.out.println("\n" + noticia);
            System.out.println("Contenido: " + noticia.getContenido());
        } else {
            System.out.println("✗ Noticia no encontrada");
        }
    }
    
    private static void actualizarNoticia() {
        System.out.print("\nIngrese ID de noticia a actualizar: ");
        int id = Integer.parseInt(scanner.nextLine());
        
        Noticia noticia = noticiaDAO.obtenerPorId(id);
        if (noticia == null) {
            System.out.println("✗ Noticia no encontrada");
            return;
        }
        
        System.out.print("Nuevo título (Enter para mantener): ");
        String titulo = scanner.nextLine();
        if (!titulo.isEmpty()) noticia.setTitulo(titulo);
        
        System.out.print("Nuevo contenido (Enter para mantener): ");
        String contenido = scanner.nextLine();
        if (!contenido.isEmpty()) noticia.setContenido(contenido);
        
        if (noticiaDAO.actualizar(noticia)) {
            System.out.println("✓ Noticia actualizada correctamente");
        } else {
            System.out.println("✗ Error al actualizar");
        }
    }
    
    private static void eliminarNoticia() {
        System.out.print("\nIngrese ID de noticia a eliminar: ");
        int id = Integer.parseInt(scanner.nextLine());
        
        System.out.print("¿Está seguro? (S/N): ");
        String confirmacion = scanner.nextLine();
        
        if (confirmacion.equalsIgnoreCase("S")) {
            if (noticiaDAO.eliminar(id)) {
                System.out.println("✓ Noticia eliminada");
            } else {
                System.out.println("✗ Error al eliminar");
            }
        }
    }
    
    private static void publicarNoticia() {
        System.out.print("\nIngrese ID de noticia a publicar: ");
        int id = Integer.parseInt(scanner.nextLine());
        
        if (noticiaDAO.publicarNoticia(id)) {
            System.out.println("✓ Noticia publicada correctamente");
        } else {
            System.out.println("✗ Error al publicar");
        }
    }
    
    private static void buscarPorTexto() {
        System.out.print("\nIngrese texto a buscar: ");
        String texto = scanner.nextLine();
        
        List<Noticia> noticias = noticiaDAO.buscarPorTexto(texto);
        System.out.println("\nResultados encontrados: " + noticias.size());
        for (Noticia n : noticias) {
            System.out.println(n);
        }
    }
    
    private static void listarDestacadas() {
        System.out.println("\n--- NOTICIAS DESTACADAS ---");
        List<Noticia> noticias = noticiaDAO.obtenerDestacadas();
        
        for (Noticia n : noticias) {
            System.out.println(n);
        }
    }
}