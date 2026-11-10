package edu.utj.evnd.poo.restaurante.repository;

import edu.utj.evnd.poo.restaurante.enums.EstadoAdministrador;
import edu.utj.evnd.poo.restaurante.model.Administrador;
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
 * Acceso a la tabla administrador de la base de datos (JDBC).
 *
 * @author Gael Sebastian Garcia Valdez
 */
public class AdministradorRepositoriy {

    private static final String SELECT =
        "SELECT idAdministrador, curp, rfc, nombre, apellidos, nacimiento, correo, telefono, imagen, usuario, contrasena, estado, rol FROM administrador";

    public void agregar(Administrador a) throws SQLException {
        String sql = "INSERT INTO administrador (idAdministrador, curp, rfc, nombre, apellidos, nacimiento, correo, telefono, imagen, usuario, contrasena, estado, rol) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, a.getId());
            ps.setString(2, a.getCurp());
            ps.setString(3, a.getRfc());
            ps.setString(4, a.getNombre());
            ps.setString(5, a.getApellido());
            ps.setObject(6, aLocalDate(a.getNacimiento()));
            ps.setString(7, a.getCorreo());
            ps.setString(8, a.getTelefono());
            ps.setString(9, texto(a.getUrlImagen()));
            ps.setString(10, a.getUsuario());
            ps.setString(11, a.getContraseña());
            ps.setString(12, a.getEstado().name());
            ps.setString(13, a.getRol());
            ps.executeUpdate();
        }
    }

    // Devuelve cuántas filas se actualizaron (0 si no existe ese id)
    public int actualizar(Administrador a) throws SQLException {
        String sql = "UPDATE administrador SET curp = ?, rfc = ?, nombre = ?, apellidos = ?, nacimiento = ?, correo = ?, "
                + "telefono = ?, imagen = ?, usuario = ?, contrasena = ?, estado = ?, rol = ? WHERE idAdministrador = ?";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, a.getCurp());
            ps.setString(2, a.getRfc());
            ps.setString(3, a.getNombre());
            ps.setString(4, a.getApellido());
            ps.setObject(5, aLocalDate(a.getNacimiento()));
            ps.setString(6, a.getCorreo());
            ps.setString(7, a.getTelefono());
            ps.setString(8, texto(a.getUrlImagen()));
            ps.setString(9, a.getUsuario());
            ps.setString(10, a.getContraseña());
            ps.setString(11, a.getEstado().name());
            ps.setString(12, a.getRol());
            ps.setInt(13, a.getId());
            return ps.executeUpdate();
        }
    }

    // Borrado físico. Devuelve cuántas filas se eliminaron (0 si no existe ese id)
    public int eliminar(int id) throws SQLException {
        String sql = "DELETE FROM administrador WHERE idAdministrador = ?";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }

    public ArrayList<Administrador> obtenerTodos() throws SQLException {
        ArrayList<Administrador> lista = new ArrayList<>();
        try (Connection con = ConexionBD.obtener();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(SELECT + " ORDER BY idAdministrador")) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public Administrador obtenerPorId(int id) throws SQLException {
        return obtenerUno("idAdministrador", id);
    }

    public Administrador obtenerPorCurp(String curp) throws SQLException {
        return obtenerUno("curp", curp);
    }

    // La columna siempre es una constante del programa, el valor buscado va por el parámetro ?
    private Administrador obtenerUno(String columna, Object valor) throws SQLException {
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(SELECT + " WHERE " + columna + " = ?")) {
            ps.setObject(1, valor);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    // Convierte una fila de la base en un objeto Administrador
    private Administrador mapear(ResultSet rs) throws SQLException {
        Administrador a = new Administrador();
        a.setId(rs.getInt("idAdministrador"));
        a.setCurp(rs.getString("curp"));
        a.setRfc(rs.getString("rfc"));
        a.setNombre(rs.getString("nombre"));
        a.setApellido(rs.getString("apellidos"));
        LocalDate nacimiento = rs.getObject("nacimiento", LocalDate.class);
        a.setNacimiento(Date.from(nacimiento.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        a.setCorreo(rs.getString("correo"));
        a.setTelefono(rs.getString("telefono"));
        a.setUrlImagen(rs.getString("imagen"));
        a.setUsuario(rs.getString("usuario"));
        a.setContraseña(rs.getString("contrasena"));
        a.setEstado(EstadoAdministrador.valueOf(rs.getString("estado")));
        a.setRol(rs.getString("rol"));
        return a;
    }

    private LocalDate aLocalDate(Date fecha) {
        return fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private String texto(String valor) {
        return (valor == null) ? "" : valor;
    }
}