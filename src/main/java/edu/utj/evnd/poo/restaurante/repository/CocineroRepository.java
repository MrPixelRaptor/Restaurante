package edu.utj.evnd.poo.restaurante.repository;

import edu.utj.evnd.poo.restaurante.enums.EstacionTrabajo;
import edu.utj.evnd.poo.restaurante.model.Cocinero;
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
 * Acceso a la tabla cocinero de la base de datos (JDBC).
 *
 * @author Gael Sebastian Garcia Valdez
 */
public class CocineroRepository {

    private static final String SELECT =
        "SELECT idCocinero, curp, rfc, nombre, apellidos, nacimiento, correo, telefono, imagen, numEmpleado, especialidad, estacionTrabajo FROM cocinero";

    public void agregar(Cocinero c) throws SQLException {
        String sql = "INSERT INTO cocinero (idCocinero, curp, rfc, nombre, apellidos, nacimiento, correo, telefono, imagen, numEmpleado, especialidad, estacionTrabajo) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
            ps.setInt(10, c.getNumEmpleado());
            ps.setString(11, c.getEspecialidad());
            ps.setString(12, c.getEstacionTrabajo().name());
            ps.executeUpdate();
        }
    }

    // Devuelve cuántas filas se actualizaron (0 si no existe ese id)
    public int actualizar(Cocinero c) throws SQLException {
        String sql = "UPDATE cocinero SET curp = ?, rfc = ?, nombre = ?, apellidos = ?, nacimiento = ?, correo = ?, "
                + "telefono = ?, imagen = ?, numEmpleado = ?, especialidad = ?, estacionTrabajo = ? WHERE idCocinero = ?";
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
            ps.setInt(9, c.getNumEmpleado());
            ps.setString(10, c.getEspecialidad());
            ps.setString(11, c.getEstacionTrabajo().name());
            ps.setInt(12, c.getId());
            return ps.executeUpdate();
        }
    }

    // Borrado físico. Devuelve cuántas filas se eliminaron (0 si no existe ese id)
    public int eliminar(int id) throws SQLException {
        String sql = "DELETE FROM cocinero WHERE idCocinero = ?";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }

    public ArrayList<Cocinero> obtenerTodos() throws SQLException {
        ArrayList<Cocinero> lista = new ArrayList<>();
        try (Connection con = ConexionBD.obtener();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(SELECT + " ORDER BY idCocinero")) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public Cocinero obtenerPorId(int id) throws SQLException {
        return obtenerUno("idCocinero", id);
    }

    public Cocinero obtenerPorCurp(String curp) throws SQLException {
        return obtenerUno("curp", curp);
    }

    // La columna siempre es una constante del programa, el valor buscado va por el parámetro ?
    private Cocinero obtenerUno(String columna, Object valor) throws SQLException {
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(SELECT + " WHERE " + columna + " = ?")) {
            ps.setObject(1, valor);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    // Convierte una fila de la base en un objeto Cocinero
    private Cocinero mapear(ResultSet rs) throws SQLException {
        Cocinero c = new Cocinero();
        c.setId(rs.getInt("idCocinero"));
        c.setCurp(rs.getString("curp"));
        c.setRfc(rs.getString("rfc"));
        c.setNombre(rs.getString("nombre"));
        c.setApellido(rs.getString("apellidos"));
        LocalDate nacimiento = rs.getObject("nacimiento", LocalDate.class);
        c.setNacimiento(Date.from(nacimiento.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        c.setCorreo(rs.getString("correo"));
        c.setTelefono(rs.getString("telefono"));
        c.setUrlImagen(rs.getString("imagen"));
        c.setNumEmpleado(rs.getInt("numEmpleado"));
        c.setEspecialidad(rs.getString("especialidad"));
        c.setEstacionTrabajo(EstacionTrabajo.valueOf(rs.getString("estacionTrabajo")));
        return c;
    }

    private LocalDate aLocalDate(Date fecha) {
        return fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private String texto(String valor) {
        return (valor == null) ? "" : valor;
    }
}