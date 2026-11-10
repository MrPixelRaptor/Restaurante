package edu.utj.evnd.poo.restaurante.service;

import edu.utj.evnd.poo.restaurante.model.Reserva;
import edu.utj.evnd.poo.restaurante.repository.ReservaRepository;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Lógica de negocio para la gestión de reservas. Las reglas (como no repetir
 * ID ni reservar la misma mesa a la misma hora) se revisan aquí antes de
 * tocar la base de datos. El borrado es físico.
 *
 * @author Gael Sebastian Garcia Valdez
 */
public class ReservaService {
    private final ReservaRepository repository = new ReservaRepository();

    /**
     * @return null si se guardó, o el mensaje que explica por qué no se pudo
     */
    public String agregar(Reserva reserva) throws SQLException {
        if (repository.existeId(reserva.getIdReserva())) {
            return "Ya existe una reserva con ese ID";
        }
        if (repository.hayConflicto(reserva, -1)) {
            return "Esa mesa ya está reservada en esa fecha y hora";
        }
        repository.agregar(reserva);
        return null;
    }

    /**
     * @return null si se actualizó, o el mensaje que explica por qué no se pudo
     */
    public String actualizar(Reserva reserva) throws SQLException {
        if (repository.obtenerPorId(reserva.getIdReserva()) == null) {
            return "No se encontró la reserva para actualizar";
        }
        if (repository.hayConflicto(reserva, reserva.getIdReserva())) {
            return "Esa mesa ya está reservada en esa fecha y hora";
        }
        repository.actualizar(reserva);
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

    public ArrayList<Reserva> obtenerTodos() throws SQLException {
        return repository.obtenerTodos();
    }

    public Reserva buscarPorId(int id) throws SQLException {
        return repository.obtenerPorId(id);
    }
}