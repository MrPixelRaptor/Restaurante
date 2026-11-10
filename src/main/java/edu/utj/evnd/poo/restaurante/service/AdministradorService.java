package edu.utj.evnd.poo.restaurante.service;

import edu.utj.evnd.poo.restaurante.model.Administrador;
import edu.utj.evnd.poo.restaurante.repository.AdministradorRepositoriy;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Lógica de negocio para la gestión de administradores. Las reglas (como no
 * repetir ID ni CURP) se revisan aquí antes de tocar la base de datos.
 *
 * @author Gael Sebastian Garcia Valdez
 */
public class AdministradorService {
    private final AdministradorRepositoriy repository = new AdministradorRepositoriy();

    /**
     * @return null si se guardó, o el mensaje que explica por qué no se pudo
     */
    public String agregar(Administrador administrador) throws SQLException {
        if (repository.obtenerPorId(administrador.getId()) != null) {
            return "Ya existe un administrador con ese ID";
        }
        if (repository.obtenerPorCurp(administrador.getCurp()) != null) {
            return "Ya existe un administrador con ese CURP";
        }
        repository.agregar(administrador);
        return null;
    }

    /**
     * @return null si se actualizó, o el mensaje que explica por qué no se pudo
     */
    public String actualizar(Administrador administrador) throws SQLException {
        if (repository.obtenerPorId(administrador.getId()) == null) {
            return "No se encontró el administrador para actualizar";
        }
        Administrador conEseCurp = repository.obtenerPorCurp(administrador.getCurp());
        if (conEseCurp != null && conEseCurp.getId() != administrador.getId()) {
            return "Ya existe otro administrador con ese CURP";
        }
        repository.actualizar(administrador);
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

    public ArrayList<Administrador> obtenerTodos() throws SQLException {
        return repository.obtenerTodos();
    }

    public Administrador buscarPorId(int id) throws SQLException {
        return repository.obtenerPorId(id);
    }

    public Administrador buscarPorCurp(String curp) throws SQLException {
        return repository.obtenerPorCurp(curp);
    }
}