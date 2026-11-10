package edu.utj.evnd.poo.restaurante.service;

import edu.utj.evnd.poo.restaurante.model.Mesero;
import edu.utj.evnd.poo.restaurante.repository.MeseroRepository;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Lógica de negocio para la gestión de meseros. El borrado es lógico: el
 * mesero se marca como inactivo y su información (incluida la imagen) se conserva.
 *
 * @author Gael Sebastian Garcia Valdez
 */
public class MeseroService {
    private final MeseroRepository repository = new MeseroRepository();

    /**
     * @return null si se guardó, o el mensaje que explica por qué no se pudo
     */
    public String agregar(Mesero mesero) throws SQLException {
        if (repository.existeId(mesero.getId())) {
            return "Ya existe un mesero con ese ID (puede estar dado de baja)";
        }
        if (repository.existeCurp(mesero.getCurp())) {
            return "Ya existe un mesero con ese CURP (puede estar dado de baja)";
        }
        repository.agregar(mesero);
        return null;
    }

    /**
     * @return null si se actualizó, o el mensaje que explica por qué no se pudo
     */
    public String actualizar(Mesero mesero) throws SQLException {
        if (repository.obtenerPorId(mesero.getId()) == null) {
            return "No se encontró el mesero para actualizar";
        }
        if (repository.existeCurpDeOtro(mesero.getCurp(), mesero.getId())) {
            return "Ya existe otro mesero con ese CURP (puede estar dado de baja)";
        }
        repository.actualizar(mesero);
        return null;
    }

    /**
     * Borrado lógico: marca al mesero como inactivo.
     *
     * @return true si se dio de baja
     */
    public boolean eliminar(int id) throws SQLException {
        return repository.darDeBaja(id) > 0;
    }

    public ArrayList<Mesero> obtenerTodos() throws SQLException {
        return repository.obtenerTodos();
    }

    public Mesero buscarPorId(int id) throws SQLException {
        return repository.obtenerPorId(id);
    }

    public Mesero buscarPorCurp(String curp) throws SQLException {
        return repository.obtenerPorCurp(curp);
    }
}