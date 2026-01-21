// ============================================
// VIDAJOVE - CRUD COMPLETO
// Proyecto Intermodular 1º DAM - 2º Trimestre
// ============================================
package Vidajove;

import Vidajove.dao.NoticiaDAO;
import Vidajove.model.Noticia;
import java.util.List;
import java.util.Scanner;

public class Main {
    
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