package edu.utj.evnd.poo.restaurante.dao;

import edu.utj.evnd.poo.restaurante.enums.Rol;
import edu.utj.evnd.poo.restaurante.model.Permiso;
import edu.utj.evnd.poo.restaurante.util.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Consulta los permisos de cada rol sobre cada módulo (tabla permiso).
 *
 * @author Gael Sebastian Garcia Valdez
 */
public class PermisoDAO {

    /**
     * @return los permisos del rol, indexados por nombre de módulo
     */
    public Map<String, Permiso> obtenerPorRol(Rol rol) throws SQLException {
        Map<String, Permiso> permisos = new HashMap<>();
        String sql = "SELECT modulo, puedeCrear, puedeConsultar, puedeActualizar, puedeEliminar FROM permiso WHERE rol = ?";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, rol.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String modulo = rs.getString("modulo");
                    permisos.put(modulo, new Permiso(modulo,
                        rs.getBoolean("puedeCrear"),
                        rs.getBoolean("puedeConsultar"),
                        rs.getBoolean("puedeActualizar"),
                        rs.getBoolean("puedeEliminar")));
                }
            }
        }
        return permisos;
    }

    /**
     * Valida si un rol puede hacer una acción ("crear", "consultar", "actualizar" o "eliminar") en un módulo.
     */
    public boolean tienePermiso(Rol rol, String modulo, String accion) throws SQLException {
        Permiso p = obtenerPorRol(rol).get(modulo);
        if (p == null) {
            return false;
        }
        return switch (accion) {
            case "crear" -> p.crear();
            case "consultar" -> p.consultar();
            case "actualizar" -> p.actualizar();
            case "eliminar" -> p.eliminar();
            default -> false;
        };
    }
}