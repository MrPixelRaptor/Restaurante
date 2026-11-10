package edu.utj.evnd.poo.restaurante.model;

import edu.utj.evnd.poo.restaurante.enums.Rol;

/**
 * Usuario que inició sesión.
 *
 * @author Gael Sebastian Garcia Valdez
 */
public record Usuario(int id, String nombre, String correo, Rol rol, boolean activo) {
}