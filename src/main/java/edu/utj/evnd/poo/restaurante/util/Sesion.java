package edu.utj.evnd.poo.restaurante.util;

import edu.utj.evnd.poo.restaurante.dao.SesionDAO;
import edu.utj.evnd.poo.restaurante.model.Permiso;
import edu.utj.evnd.poo.restaurante.model.Usuario;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Guarda el usuario que inició sesión y sus permisos mientras el programa está abierto.
 *
 * @author Gael Sebastian Garcia Valdez
 */
public final class Sesion {
    private static Usuario usuario;
    private static Map<String, Permiso> permisos = new HashMap<>();
    private static int idSesion;

    private Sesion() {
    }

    public static void iniciar(Usuario nuevoUsuario, Map<String, Permiso> nuevosPermisos, int nuevoIdSesion) {
        usuario = nuevoUsuario;
        permisos = nuevosPermisos;
        idSesion = nuevoIdSesion;
    }

    // Registra el cierre en la bitácora y limpia la sesión. Si ya estaba cerrada, no hace nada.
    public static void cerrar() {
        if (usuario == null) {
            return;
        }
        try {
            new SesionDAO().registrarCierre(idSesion);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        usuario = null;
        permisos = new HashMap<>();
        idSesion = 0;
    }

    public static Usuario getUsuario() {
        return usuario;
    }

    // Si el rol no tiene permisos definidos para el módulo, se devuelve uno sin ningún permiso
    public static Permiso getPermiso(String modulo) {
        return permisos.getOrDefault(modulo, new Permiso(modulo, false, false, false, false));
    }

    public static boolean puedeConsultar(String modulo) {
        return getPermiso(modulo).consultar();
    }
}