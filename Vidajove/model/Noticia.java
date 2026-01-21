package Vidajove.model;

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