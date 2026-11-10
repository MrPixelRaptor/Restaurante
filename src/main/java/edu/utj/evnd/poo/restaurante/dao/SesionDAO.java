package edu.utj.evnd.poo.restaurante.dao;

import edu.utj.evnd.poo.restaurante.util.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Registra en la bitácora cuándo inicia y cuándo cierra sesión cada usuario.
 *
 * @author Gael Sebastian Garcia Valdez
 */
public class SesionDAO {

    /**
     * @return el id de la sesión recién registrada
     */
    public int registrarInicio(int idAdministrador) throws SQLException {
        String sql = "INSERT INTO bitacora_sesion (idAdministrador, inicio) VALUES (?, NOW())";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idAdministrador);
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                claves.next();
                return claves.getInt(1);
            }
        }
    }

    public void registrarCierre(int idSesion) throws SQLException {
        String sql = "UPDATE bitacora_sesion SET fin = NOW() WHERE idSesion = ?";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idSesion);
            ps.executeUpdate();
        }
    }
}