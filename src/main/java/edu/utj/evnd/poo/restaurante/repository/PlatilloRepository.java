package edu.utj.evnd.poo.restaurante.repository;

import edu.utj.evnd.poo.restaurante.enums.EstadoPlatillo;
import edu.utj.evnd.poo.restaurante.model.Categoria;
import edu.utj.evnd.poo.restaurante.model.Platillo;
import edu.utj.evnd.poo.restaurante.util.ConexionBD;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Acceso a la tabla platillo de la base de datos (JDBC). Al leer un platillo
 * se trae también su categoría (JOIN). El borrado es lógico: un platillo dado
 * de baja se queda en la tabla con activo = 0 y deja de aparecer en las consultas.
 *
 * @author Gael Sebastian Garcia Valdez
 */
public class PlatilloRepository {

    private static final String SELECT =
        "SELECT p.idPlatillo, p.nombre, p.descripcion, p.precio, p.existencia, p.descuento, p.imagen, p.estado, "
        + "c.idCategoria, c.nombre AS nombreCategoria, c.imagen AS imagenCategoria "
        + "FROM platillo p JOIN categoria c ON p.idCategoria = c.idCategoria";

    // La columna activo no se manda: la base le pone 1 por defecto
    public void agregar(Platillo p) throws SQLException {
        String sql = "INSERT INTO platillo (idPlatillo, nombre, descripcion, idCategoria, precio, existencia, descuento, imagen, estado) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, p.getIdPlatillo());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getDescripcion());
            ps.setInt(4, p.getCategoria().getIdCategoria());
            ps.setBigDecimal(5, BigDecimal.valueOf(p.getPrecio()));
            ps.setInt(6, p.getExistencia());
            ps.setBigDecimal(7, BigDecimal.valueOf(p.getDescuento()));
            ps.setString(8, texto(p.getUrlImagen()));
            ps.setString(9, p.getEstado().name());
            ps.executeUpdate();
        }
    }

    // Devuelve cuántas filas se actualizaron (0 si no existe o está dado de baja)
    public int actualizar(Platillo p) throws SQLException {
        String sql = "UPDATE platillo SET nombre = ?, descripcion = ?, idCategoria = ?, precio = ?, existencia = ?, "
                + "descuento = ?, imagen = ?, estado = ? WHERE idPlatillo = ? AND activo = 1";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getDescripcion());
            ps.setInt(3, p.getCategoria().getIdCategoria());
            ps.setBigDecimal(4, BigDecimal.valueOf(p.getPrecio()));
            ps.setInt(5, p.getExistencia());
            ps.setBigDecimal(6, BigDecimal.valueOf(p.getDescuento()));
            ps.setString(7, texto(p.getUrlImagen()));
            ps.setString(8, p.getEstado().name());
            ps.setInt(9, p.getIdPlatillo());
            return ps.executeUpdate();
        }
    }

    // Borrado lógico: la fila se queda en la tabla, solo se marca como inactiva
    public int darDeBaja(int id) throws SQLException {
        String sql = "UPDATE platillo SET activo = 0 WHERE idPlatillo = ? AND activo = 1";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }

    public ArrayList<Platillo> obtenerTodos() throws SQLException {
        ArrayList<Platillo> lista = new ArrayList<>();
        try (Connection con = ConexionBD.obtener();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(SELECT + " WHERE p.activo = 1 ORDER BY p.idPlatillo")) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    // Solo busca entre los platillos activos
    public Platillo obtenerPorId(int id) throws SQLException {
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(SELECT + " WHERE p.idPlatillo = ? AND p.activo = 1")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    // Revisa también a los dados de baja, porque el ID sigue ocupado en la tabla
    public boolean existeId(int id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM platillo WHERE idPlatillo = ?";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    // Convierte una fila de la base (con los datos de su categoría) en un objeto Platillo
    private Platillo mapear(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria();
        categoria.setIdCategoria(rs.getInt("idCategoria"));
        categoria.setNombre(rs.getString("nombreCategoria"));
        categoria.setUrlImagen(rs.getString("imagenCategoria"));

        Platillo p = new Platillo();
        p.setIdPlatillo(rs.getInt("idPlatillo"));
        p.setNombre(rs.getString("nombre"));
        p.setDescripcion(rs.getString("descripcion"));
        p.setCategoria(categoria);
        p.setPrecio(rs.getDouble("precio"));
        p.setExistencia(rs.getInt("existencia"));
        p.setDescuento(rs.getDouble("descuento"));
        p.setUrlImagen(rs.getString("imagen"));
        p.setEstado(EstadoPlatillo.valueOf(rs.getString("estado")));
        return p;
    }

    private String texto(String valor) {
        return (valor == null) ? "" : valor;
    }
}