package edu.utj.evnd.poo.restaurante.service;

import edu.utj.evnd.poo.restaurante.model.Cliente;
import edu.utj.evnd.poo.restaurante.repository.ClienteRepository;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Lógica de negocio para la gestión de clientes. Es una instancia única
 * compartida, porque otros módulos (como Reserva) necesitan consultar los
 * mismos clientes. El borrado es lógico: el cliente se marca como inactivo y
 * su información (incluida la imagen) se conserva.
 *
 * @author Gael Sebastian Garcia Valdez
 */
public class ClienteService {
    private static final ClienteService INSTANCIA = new ClienteService();

    private final ClienteRepository repository = new ClienteRepository();

    private ClienteService() {
    }

    public static ClienteService getInstancia(){
        return INSTANCIA;
    }

    /**
     * @return null si se guardó, o el mensaje que explica por qué no se pudo
     */
    public String agregar(Cliente cliente) throws SQLException {
        if (repository.existeId(cliente.getId())) {
            return "Ya existe un cliente con ese ID (puede estar dado de baja)";
        }
        if (repository.existeCurp(cliente.getCurp())) {
            return "Ya existe un cliente con ese CURP (puede estar dado de baja)";
        }
        repository.agregar(cliente);
        return null;
    }

    /**
     * @return null si se actualizó, o el mensaje que explica por qué no se pudo
     */
    public String actualizar(Cliente cliente) throws SQLException {
        if (repository.obtenerPorId(cliente.getId()) == null) {
            return "No se encontró el cliente para actualizar";
        }
        if (repository.existeCurpDeOtro(cliente.getCurp(), cliente.getId())) {
            return "Ya existe otro cliente con ese CURP (puede estar dado de baja)";
        }
        repository.actualizar(cliente);
        return null;
    }

    /**
     * Borrado lógico: marca al cliente como inactivo.
     *
     * @return true si se dio de baja
     */
    public boolean eliminar(int id) throws SQLException {
        return repository.darDeBaja(id) > 0;
    }

    public ArrayList<Cliente> obtenerTodos() throws SQLException {
        return repository.obtenerTodos();
    }

    public Cliente buscarPorId(int id) throws SQLException {
        return repository.obtenerPorId(id);
    }

    public Cliente buscarPorCurp(String curp) throws SQLException {
        return repository.obtenerPorCurp(curp);
    }

    // Para mostrar el cliente de una reserva antigua aunque ya esté dado de baja
    public Cliente buscarPorIdIncluyendoBajas(int id) throws SQLException {
        return repository.obtenerPorIdIncluyendoBajas(id);
    }
}