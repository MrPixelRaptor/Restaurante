package edu.utj.evnd.poo.restaurante.repository;

import edu.utj.evnd.poo.restaurante.model.Categoria;
import edu.utj.evnd.poo.restaurante.util.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Acceso a la tabla categoria de la base de datos (JDBC). El borrado es
 * lógico: una categoría dada de baja se queda en la tabla con activo = 0 y
 * deja de aparecer en las consultas.
 *
 * @author Gael Sebastian Garcia Valdez
 */
public class CategoriaRepository {

    private static final String SELECT = "SELECT idCategoria, nombre, imagen FROM categoria";

    // La columna activo no se manda: la base le pone 1 por defecto
    public void agregar(Categoria c) throws SQLException {
        String sql = "INSERT INTO categoria (idCategoria, nombre, imagen) VALUES (?, ?, ?)";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, c.getIdCategoria());
            ps.setString(2, c.getNombre());
            ps.setString(3, texto(c.getUrlImagen()));
            ps.executeUpdate();
        }
    }

    // Devuelve cuántas filas se actualizaron (0 si no existe o está dada de baja)
    public int actualizar(Categoria c) throws SQLException {
        String sql = "UPDATE categoria SET nombre = ?, imagen = ? WHERE idCategoria = ? AND activo = 1";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, texto(c.getUrlImagen()));
            ps.setInt(3, c.getIdCategoria());
            return ps.executeUpdate();
        }
    }

    // Borrado lógico: la fila se queda en la tabla, solo se marca como inactiva
    public int darDeBaja(int id) throws SQLException {
        String sql = "UPDATE categoria SET activo = 0 WHERE idCategoria = ? AND activo = 1";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }

    public ArrayList<Categoria> obtenerTodos() throws SQLException {
        ArrayList<Categoria> lista = new ArrayList<>();
        try (Connection con = ConexionBD.obtener();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(SELECT + " WHERE activo = 1 ORDER BY idCategoria")) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    // Solo busca entre las categorías activas
    public Categoria obtenerPorId(int id) throws SQLException {
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(SELECT + " WHERE idCategoria = ? AND activo = 1")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    // Revisa también a las dadas de baja, porque el ID sigue ocupado en la tabla
    public boolean existeId(int id) throws SQLException {
        return contar("SELECT COUNT(*) FROM categoria WHERE idCategoria = ?", id) > 0;
    }

    // Indica si hay platillos activos que pertenecen a la categoría
    public boolean tienePlatillosActivos(int idCategoria) throws SQLException {
        return contar("SELECT COUNT(*) FROM platillo WHERE idCategoria = ? AND activo = 1", idCategoria) > 0;
    }

    private int contar(String sql, int valor) throws SQLException {
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, valor);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    // Convierte una fila de la base en un objeto Categoria
    private Categoria mapear(ResultSet rs) throws SQLException {
        Categoria c = new Categoria();
        c.setIdCategoria(rs.getInt("idCategoria"));
        c.setNombre(rs.getString("nombre"));
        c.setUrlImagen(rs.getString("imagen"));
        return c;
    }

    private String texto(String valor) {
        return (valor == null) ? "" : valor;
    }
}