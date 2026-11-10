package edu.utj.evnd.poo.restaurante.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Entrega conexiones a la base de datos "restaurante" (MySQL en XAMPP).
 * Cada repository pide una conexión cuando la necesita y la cierra al terminar.
 *
 * @author Gael Sebastian Garcia Valdez
 */
public final class ConexionBD {

    private static final String URL = "jdbc:mysql://localhost:3306/restaurante"
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Mexico_City&characterEncoding=UTF-8";
    private static final String USUARIO = "root";
    private static final String CONTRASENA = "";

    private ConexionBD() {
    }

    /**
     * @return una conexión nueva a la base de datos
     * @throws SQLException si MySQL no está encendido o los datos de conexión no son correctos
     */
    public static Connection obtener() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, CONTRASENA);
    }
}