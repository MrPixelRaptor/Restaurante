package edu.utj.evnd.poo.restaurante.dao;

import edu.utj.evnd.poo.restaurante.enums.Rol;
import edu.utj.evnd.poo.restaurante.model.Usuario;
import edu.utj.evnd.poo.restaurante.util.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Valida las credenciales de inicio de sesión contra la tabla administrador.
 *
 * @author Gael Sebastian Garcia Valdez
 */
public class AutenticacionDAO {

    // Valor de la columna estado que indica una cuenta activa
    private static final String ESTADO_ACTIVO = "ACTIVO";

    /**
     * Busca la cuenta por ID o correo y revisa la contraseña.
     *
     * @return el usuario, o null si el ID/correo o la contraseña no coinciden
     */
    public Usuario iniciarSesion(String identificador, String contrasena) throws SQLException {
        String sql = "SELECT idAdministrador, nombre, apellidos, correo, contrasena, rol, estado "
                + "FROM administrador WHERE idAdministrador = ? OR correo = ?";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, comoNumero(identificador));
            ps.setString(2, identificador);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // La contraseña se compara aquí porque MySQL ignora mayúsculas y minúsculas al comparar texto
                    if (contrasena.equals(rs.getString("contrasena"))) {
                        return new Usuario(
                            rs.getInt("idAdministrador"),
                            rs.getString("nombre") + " " + rs.getString("apellidos"),
                            rs.getString("correo"),
                            Rol.desde(rs.getString("rol")),
                            ESTADO_ACTIVO.equalsIgnoreCase(rs.getString("estado")));
                    }
                }
            }
        }
        return null;
    }

    // Si lo escrito es un número se busca como ID; si no, -1 (no existe ningún ID así)
    private int comoNumero(String texto) {
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}