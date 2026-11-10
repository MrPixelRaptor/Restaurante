package edu.utj.evnd.poo.restaurante.enums;

/**
 * Perfiles de usuario del sistema.
 *
 * @author Gael Sebastian Garcia Valdez
 */
public enum Rol {
    ADMINISTRADOR, GERENTE, RECEPCIONISTA;

    // Convierte el texto guardado en la columna rol; devuelve null si no es un rol válido
    public static Rol desde(String texto) {
        if (texto == null) {
            return null;
        }
        try {
            return Rol.valueOf(texto.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}