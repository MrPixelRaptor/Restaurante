package edu.utj.evnd.poo.restaurante.repository;

import edu.utj.evnd.poo.restaurante.enums.MetodoPago;
import edu.utj.evnd.poo.restaurante.model.Cliente;
import edu.utj.evnd.poo.restaurante.util.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

/**
 * Acceso a la tabla cliente de la base de datos (JDBC). El borrado es lógico:
 * un cliente dado de baja se queda en la tabla con activo = 0 y deja de
 * aparecer en las consultas.
 *
 * @author Gael Sebastian Garcia Valdez
 */
public class ClienteRepository {

    private static final String SELECT =
        "SELECT idCliente, curp, rfc, nombre, apellidos, nacimiento, correo, telefono, imagen, metodoPago, puntosFidelidad FROM cliente";

    // La columna activo no se manda: la base le pone 1 por defecto
    public void agregar(Cliente c) throws SQLException {
        String sql = "INSERT INTO cliente (idCliente, curp, rfc, nombre, apellidos, nacimiento, correo, telefono, imagen, metodoPago, puntosFidelidad) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, c.getId());
            ps.setString(2, c.getCurp());
            ps.setString(3, c.getRfc());
            ps.setString(4, c.getNombre());
            ps.setString(5, c.getApellido());
            ps.setObject(6, aLocalDate(c.getNacimiento()));
            ps.setString(7, c.getCorreo());
            ps.setString(8, c.getTelefono());
            ps.setString(9, texto(c.getUrlImagen()));
            ps.setString(10, c.getMetodoPago().name());
            ps.setInt(11, c.getPuntosFidelidad());
            ps.executeUpdate();
        }
    }

    // Devuelve cuántas filas se actualizaron (0 si no existe o está dado de baja)
    public int actualizar(Cliente c) throws SQLException {
        String sql = "UPDATE cliente SET curp = ?, rfc = ?, nombre = ?, apellidos = ?, nacimiento = ?, correo = ?, "
                + "telefono = ?, imagen = ?, metodoPago = ?, puntosFidelidad = ? WHERE idCliente = ? AND activo = 1";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getCurp());
            ps.setString(2, c.getRfc());
            ps.setString(3, c.getNombre());
            ps.setString(4, c.getApellido());
            ps.setObject(5, aLocalDate(c.getNacimiento()));
            ps.setString(6, c.getCorreo());
            ps.setString(7, c.getTelefono());
            ps.setString(8, texto(c.getUrlImagen()));
            ps.setString(9, c.getMetodoPago().name());
            ps.setInt(10, c.getPuntosFidelidad());
            ps.setInt(11, c.getId());
            return ps.executeUpdate();
        }
    }

    // Borrado lógico: la fila se queda en la tabla, solo se marca como inactiva
    public int darDeBaja(int id) throws SQLException {
        String sql = "UPDATE cliente SET activo = 0 WHERE idCliente = ? AND activo = 1";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }

    public ArrayList<Cliente> obtenerTodos() throws SQLException {
        ArrayList<Cliente> lista = new ArrayList<>();
        try (Connection con = ConexionBD.obtener();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(SELECT + " WHERE activo = 1 ORDER BY idCliente")) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    // Solo busca entre los clientes activos
    public Cliente obtenerPorId(int id) throws SQLException {
        return obtenerUno("idCliente", id, true);
    }

    // Solo busca entre los clientes activos
    public Cliente obtenerPorCurp(String curp) throws SQLException {
        return obtenerUno("curp", curp, true);
    }

    // Busca también entre los dados de baja (para mostrar el cliente de una reserva antigua)
    public Cliente obtenerPorIdIncluyendoBajas(int id) throws SQLException {
        return obtenerUno("idCliente", id, false);
    }

    // Revisa también a los dados de baja, porque el ID y el CURP siguen ocupados en la tabla
    public boolean existeId(int id) throws SQLException {
        return contar("SELECT COUNT(*) FROM cliente WHERE idCliente = ?", id) > 0;
    }

    public boolean existeCurp(String curp) throws SQLException {
        return contar("SELECT COUNT(*) FROM cliente WHERE curp = ?", curp) > 0;
    }

    // Revisa si el CURP lo tiene otro cliente (activo o dado de baja)
    public boolean existeCurpDeOtro(String curp, int idActual) throws SQLException {
        String sql = "SELECT COUNT(*) FROM cliente WHERE curp = ? AND idCliente <> ?";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, curp);
            ps.setInt(2, idActual);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    private int contar(String sql, Object valor) throws SQLException {
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, valor);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    // La columna siempre es una constante del programa, el valor buscado va por el parámetro ?
    private Cliente obtenerUno(String columna, Object valor, boolean soloActivos) throws SQLException {
        String sql = SELECT + " WHERE " + columna + " = ?" + (soloActivos ? " AND activo = 1" : "");
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, valor);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    // Convierte una fila de la base en un objeto Cliente
    private Cliente mapear(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setId(rs.getInt("idCliente"));
        c.setCurp(rs.getString("curp"));
        c.setRfc(rs.getString("rfc"));
        c.setNombre(rs.getString("nombre"));
        c.setApellido(rs.getString("apellidos"));
        LocalDate nacimiento = rs.getObject("nacimiento", LocalDate.class);
        c.setNacimiento(Date.from(nacimiento.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        c.setCorreo(rs.getString("correo"));
        c.setTelefono(rs.getString("telefono"));
        c.setUrlImagen(rs.getString("imagen"));
        c.setMetodoPago(MetodoPago.valueOf(rs.getString("metodoPago")));
        c.setPuntosFidelidad(rs.getInt("puntosFidelidad"));
        return c;
    }

    private LocalDate aLocalDate(Date fecha) {
        return fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private String texto(String valor) {
        return (valor == null) ? "" : valor;
    }
}