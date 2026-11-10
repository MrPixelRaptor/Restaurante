package edu.utj.evnd.poo.restaurante.service;

import edu.utj.evnd.poo.restaurante.model.Categoria;
import edu.utj.evnd.poo.restaurante.repository.CategoriaRepository;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Lógica de negocio para la gestión de categorías. Es una instancia única
 * compartida, porque otros módulos (como Platillo) necesitan consultar las
 * mismas categorías. El borrado es lógico: la categoría se marca como
 * inactiva y su información (incluida la imagen) se conserva.
 *
 * @author Gael Sebastian Garcia Valdez
 */
public class CategoriaService {
    private static final CategoriaService INSTANCIA = new CategoriaService();

    private final CategoriaRepository repository = new CategoriaRepository();

    private CategoriaService() {
    }

    public static CategoriaService getInstance(){
        return INSTANCIA;
    }

    /**
     * @return null si se guardó, o el mensaje que explica por qué no se pudo
     */
    public String agregar(Categoria categoria) throws SQLException {
        if (repository.existeId(categoria.getIdCategoria())) {
            return "Ya existe una categoría con ese ID (puede estar dada de baja)";
        }
        repository.agregar(categoria);
        return null;
    }

    /**
     * @return null si se actualizó, o el mensaje que explica por qué no se pudo
     */
    public String actualizar(Categoria categoria) throws SQLException {
        if (repository.obtenerPorId(categoria.getIdCategoria()) == null) {
            return "No se encontró la categoría para actualizar";
        }
        repository.actualizar(categoria);
        return null;
    }

    // Indica si hay platillos activos que pertenecen a la categoría
    public boolean tienePlatillosActivos(int idCategoria) throws SQLException {
        return repository.tienePlatillosActivos(idCategoria);
    }

    /**
     * Borrado lógico: marca la categoría como inactiva.
     *
     * @return true si se dio de baja
     */
    public boolean eliminar(int id) throws SQLException {
        return repository.darDeBaja(id) > 0;
    }

    public ArrayList<Categoria> obtenerTodos() throws SQLException {
        return repository.obtenerTodos();
    }

    public Categoria buscarPorId(int id) throws SQLException {
        return repository.obtenerPorId(id);
    }
}