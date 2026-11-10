package edu.utj.evnd.poo.restaurante.repository;

import edu.utj.evnd.poo.restaurante.enums.TurnoMesero;
import edu.utj.evnd.poo.restaurante.model.Mesero;
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
 * Acceso a la tabla mesero de la base de datos (JDBC). El borrado es lógico:
 * un mesero dado de baja se queda en la tabla con activo = 0 y deja de
 * aparecer en las consultas.
 *
 * @author Gael Sebastian Garcia Valdez
 */
public class MeseroRepository {

    private static final String SELECT =
        "SELECT idMesero, curp, rfc, nombre, apellidos, nacimiento, correo, telefono, imagen, numEmpleado, turno FROM mesero";

    // La columna activo no se manda: la base le pone 1 por defecto
    public void agregar(Mesero m) throws SQLException {
        String sql = "INSERT INTO mesero (idMesero, curp, rfc, nombre, apellidos, nacimiento, correo, telefono, imagen, numEmpleado, turno) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, m.getId());
            ps.setString(2, m.getCurp());
            ps.setString(3, m.getRfc());
            ps.setString(4, m.getNombre());
            ps.setString(5, m.getApellido());
            ps.setObject(6, aLocalDate(m.getNacimiento()));
            ps.setString(7, m.getCorreo());
            ps.setString(8, m.getTelefono());
            ps.setString(9, texto(m.getUrlImagen()));
            ps.setInt(10, m.getNumEmpleado());
            ps.setString(11, m.getTurno().name());
            ps.executeUpdate();
        }
    }

    // Devuelve cuántas filas se actualizaron (0 si no existe o está dado de baja)
    public int actualizar(Mesero m) throws SQLException {
        String sql = "UPDATE mesero SET curp = ?, rfc = ?, nombre = ?, apellidos = ?, nacimiento = ?, correo = ?, "
                + "telefono = ?, imagen = ?, numEmpleado = ?, turno = ? WHERE idMesero = ? AND activo = 1";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, m.getCurp());
            ps.setString(2, m.getRfc());
            ps.setString(3, m.getNombre());
            ps.setString(4, m.getApellido());
            ps.setObject(5, aLocalDate(m.getNacimiento()));
            ps.setString(6, m.getCorreo());
            ps.setString(7, m.getTelefono());
            ps.setString(8, texto(m.getUrlImagen()));
            ps.setInt(9, m.getNumEmpleado());
            ps.setString(10, m.getTurno().name());
            ps.setInt(11, m.getId());
            return ps.executeUpdate();
        }
    }

    // Borrado lógico: la fila se queda en la tabla, solo se marca como inactiva
    public int darDeBaja(int id) throws SQLException {
        String sql = "UPDATE mesero SET activo = 0 WHERE idMesero = ? AND activo = 1";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }

    public ArrayList<Mesero> obtenerTodos() throws SQLException {
        ArrayList<Mesero> lista = new ArrayList<>();
        try (Connection con = ConexionBD.obtener();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(SELECT + " WHERE activo = 1 ORDER BY idMesero")) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    // Solo busca entre los meseros activos
    public Mesero obtenerPorId(int id) throws SQLException {
        return obtenerUno("idMesero", id);
    }

    // Solo busca entre los meseros activos
    public Mesero obtenerPorCurp(String curp) throws SQLException {
        return obtenerUno("curp", curp);
    }

    // Revisa también a los dados de baja, porque el ID y el CURP siguen ocupados en la tabla
    public boolean existeId(int id) throws SQLException {
        return contar("SELECT COUNT(*) FROM mesero WHERE idMesero = ?", id) > 0;
    }

    public boolean existeCurp(String curp) throws SQLException {
        return contar("SELECT COUNT(*) FROM mesero WHERE curp = ?", curp) > 0;
    }

    // Revisa si el CURP lo tiene otro mesero (activo o dado de baja)
    public boolean existeCurpDeOtro(String curp, int idActual) throws SQLException {
        String sql = "SELECT COUNT(*) FROM mesero WHERE curp = ? AND idMesero <> ?";
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
    private Mesero obtenerUno(String columna, Object valor) throws SQLException {
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(SELECT + " WHERE " + columna + " = ? AND activo = 1")) {
            ps.setObject(1, valor);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    // Convierte una fila de la base en un objeto Mesero
    private Mesero mapear(ResultSet rs) throws SQLException {
        Mesero m = new Mesero();
        m.setId(rs.getInt("idMesero"));
        m.setCurp(rs.getString("curp"));
        m.setRfc(rs.getString("rfc"));
        m.setNombre(rs.getString("nombre"));
        m.setApellido(rs.getString("apellidos"));
        LocalDate nacimiento = rs.getObject("nacimiento", LocalDate.class);
        m.setNacimiento(Date.from(nacimiento.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        m.setCorreo(rs.getString("correo"));
        m.setTelefono(rs.getString("telefono"));
        m.setUrlImagen(rs.getString("imagen"));
        m.setNumEmpleado(rs.getInt("numEmpleado"));
        m.setTurno(TurnoMesero.valueOf(rs.getString("turno")));
        return m;
    }

    private LocalDate aLocalDate(Date fecha) {
        return fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private String texto(String valor) {
        return (valor == null) ? "" : valor;
    }
}