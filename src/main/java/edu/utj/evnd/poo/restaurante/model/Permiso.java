package edu.utj.evnd.poo.restaurante.model;

/**
 * Permisos de un rol sobre un módulo (tabla): crear, consultar, actualizar y eliminar.
 *
 * @author Gael Sebastian Garcia Valdez
 */
public record Permiso(String modulo, boolean crear, boolean consultar, boolean actualizar, boolean eliminar) {
}