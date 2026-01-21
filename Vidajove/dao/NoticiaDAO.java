package Vidajove.dao;

import Vidajove.config.DatabaseConfig;
import Vidajove.model.Noticia;
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

