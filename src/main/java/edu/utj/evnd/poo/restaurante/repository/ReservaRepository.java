package edu.utj.evnd.poo.restaurante.repository;

import edu.utj.evnd.poo.restaurante.enums.EstadoReserva;
import edu.utj.evnd.poo.restaurante.model.Cliente;
import edu.utj.evnd.poo.restaurante.model.Reserva;
import edu.utj.evnd.poo.restaurante.util.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Acceso a la tabla reserva de la base de datos (JDBC). El borrado es físico:
 * al eliminar una reserva se borra su fila.
 *
 * @author Gael Sebastian Garcia Valdez
 */
public class ReservaRepository {

    private static final String SELECT =
        "SELECT idReserva, idCliente, mesa, fecha, hora, numeroPersonas, estado FROM reserva";

    private final ClienteRepository clienteRepository = new ClienteRepository();

    public void agregar(Reserva r) throws SQLException {
        String sql = "INSERT INTO reserva (idReserva, idCliente, mesa, fecha, hora, numeroPersonas, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, r.getIdReserva());
            ps.setInt(2, r.getCliente().getId());
            ps.setInt(3, r.getMesa());
            ps.setObject(4, r.getFecha());
            ps.setObject(5, r.getHora());
            ps.setInt(6, r.getNumeroPersonas());
            ps.setString(7, r.getEstado().name());
            ps.executeUpdate();
        }
    }

    // Devuelve cuántas filas se actualizaron (0 si no existe ese id)
    public int actualizar(Reserva r) throws SQLException {
        String sql = "UPDATE reserva SET idCliente = ?, mesa = ?, fecha = ?, hora = ?, numeroPersonas = ?, estado = ? WHERE idReserva = ?";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, r.getCliente().getId());
            ps.setInt(2, r.getMesa());
            ps.setObject(3, r.getFecha());
            ps.setObject(4, r.getHora());
            ps.setInt(5, r.getNumeroPersonas());
            ps.setString(6, r.getEstado().name());
            ps.setInt(7, r.getIdReserva());
            return ps.executeUpdate();
        }
    }

    // Borrado físico. Devuelve cuántas filas se eliminaron (0 si no existe ese id)
    public int eliminar(int id) throws SQLException {
        String sql = "DELETE FROM reserva WHERE idReserva = ?";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }

    public ArrayList<Reserva> obtenerTodos() throws SQLException {
        ArrayList<Reserva> lista = new ArrayList<>();
        Map<Integer, Cliente> clientes = new HashMap<>(); // para no consultar dos veces al mismo cliente
        try (Connection con = ConexionBD.obtener();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(SELECT + " ORDER BY idReserva")) {
            while (rs.next()) {
                lista.add(mapear(rs, clientes));
            }
        }
        return lista;
    }

    public Reserva obtenerPorId(int id) throws SQLException {
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(SELECT + " WHERE idReserva = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs, new HashMap<>()) : null;
            }
        }
    }

    public boolean existeId(int id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reserva WHERE idReserva = ?";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    // Revisa si ya hay otra reserva activa (no cancelada) en la misma mesa, fecha y hora.
    // idExcluir es la reserva que se está editando, para que no choque consigo misma (-1 si es nueva).
    public boolean hayConflicto(Reserva r, int idExcluir) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reserva WHERE mesa = ? AND fecha = ? AND hora = ? AND estado <> ? AND idReserva <> ?";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, r.getMesa());
            ps.setObject(2, r.getFecha());
            ps.setObject(3, r.getHora());
            ps.setString(4, EstadoReserva.CANCELADA.name());
            ps.setInt(5, idExcluir);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    // Convierte una fila de la base en un objeto Reserva, trayendo también a su cliente (aunque esté dado de baja)
    private Reserva mapear(ResultSet rs, Map<Integer, Cliente> clientes) throws SQLException {
        int idCliente = rs.getInt("idCliente");
        Cliente cliente = clientes.get(idCliente);
        if (cliente == null) {
            cliente = clienteRepository.obtenerPorIdIncluyendoBajas(idCliente);
            clientes.put(idCliente, cliente);
        }

        Reserva r = new Reserva();
        r.setIdReserva(rs.getInt("idReserva"));
        r.setCliente(cliente);
        r.setMesa(rs.getInt("mesa"));
        r.setFecha(rs.getObject("fecha", LocalDate.class));
        r.setHora(rs.getObject("hora", LocalTime.class));
        r.setNumeroPersonas(rs.getInt("numeroPersonas"));
        r.setEstado(EstadoReserva.valueOf(rs.getString("estado")));
        return r;
    }
}