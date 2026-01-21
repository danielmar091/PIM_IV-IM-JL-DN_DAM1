package Vidajove.dao;

import java.util.List;

public interface CrudDAO<T> {
    boolean crear(T objeto);
    T obtenerPorId(int id);
    List<T> obtenerTodos();
    boolean actualizar(T objeto);
    boolean eliminar(int id);
}