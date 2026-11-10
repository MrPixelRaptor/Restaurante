package edu.utj.evnd.poo.restaurante.service;

import edu.utj.evnd.poo.restaurante.model.Cocinero;
import edu.utj.evnd.poo.restaurante.repository.CocineroRepository;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Lógica de negocio para la gestión de cocineros. Las reglas (como no
 * repetir ID ni CURP) se revisan aquí antes de tocar la base de datos.
 *
 * @author Gael Sebastian Garcia Valdez
 */
public class CocineroService {
    private final CocineroRepository repository = new CocineroRepository();

    /**
     * @return null si se guardó, o el mensaje que explica por qué no se pudo
     */
    public String agregar(Cocinero cocinero) throws SQLException {
        if (repository.obtenerPorId(cocinero.getId()) != null) {
            return "Ya existe un cocinero con ese ID";
        }
        if (repository.obtenerPorCurp(cocinero.getCurp()) != null) {
            return "Ya existe un cocinero con ese CURP";
        }
        repository.agregar(cocinero);
        return null;
    }

    /**
     * @return null si se actualizó, o el mensaje que explica por qué no se pudo
     */
    public String actualizar(Cocinero cocinero) throws SQLException {
        if (repository.obtenerPorId(cocinero.getId()) == null) {
            return "No se encontró el cocinero para actualizar";
        }
        Cocinero conEseCurp = repository.obtenerPorCurp(cocinero.getCurp());
        if (conEseCurp != null && conEseCurp.getId() != cocinero.getId()) {
            return "Ya existe otro cocinero con ese CURP";
        }
        repository.actualizar(cocinero);
        return null;
    }

    /**
     * Borrado físico: elimina la fila de la base de datos.
     *
     * @return true si se eliminó
     */
    public boolean eliminar(int id) throws SQLException {
        return repository.eliminar(id) > 0;
    }

    public ArrayList<Cocinero> obtenerTodos() throws SQLException {
        return repository.obtenerTodos();
    }

    public Cocinero buscarPorId(int id) throws SQLException {
        return repository.obtenerPorId(id);
    }

    public Cocinero buscarPorCurp(String curp) throws SQLException {
        return repository.obtenerPorCurp(curp);
    }
}