package edu.utj.evnd.poo.restaurante.model;

import edu.utj.evnd.poo.restaurante.enums.EstadoAdministrador;
import java.util.Date;

/**
 * Representa a un administrador del sistema del restaurante. Extiende de
 * Persona agregando los datos necesarios para el inicio de sesión y el
 * control de su rol dentro del sistema. Es una clase terminal del árbol
 * de herencia: no está pensada para ser extendida por ninguna subclase.
 *
 * @author Gael Sebastian Garcia Valdez
 */
public final class Administrador extends Persona {
    private String usuario;
    private String contraseña;
    private EstadoAdministrador estado;
    private String rol;

    /**
     * Constructor vacío. Inicializa los atributos propios de Administrador
     * en valores por defecto (estado ACTIVO), garantizando un estado
     * inicial válido sin necesidad de parámetros.
     */
    public Administrador() {
        usuario = "";
        contraseña = "";
        estado = EstadoAdministrador.ACTIVO;
        rol = "";
    }

    /**
     * Constructor con los datos mínimos requeridos para que un Administrador
     * nazca en un estado válido e íntegro, incluyendo los atributos heredados
     * de Persona.
     *
     * @param usuario nombre de usuario para iniciar sesión
     * @param contraseña contraseña del administrador
     * @param estado estado actual del administrador (ej. ACTIVO/INACTIVO)
     * @param rol rol asignado al administrador dentro del sistema
     * @param id identificador numérico único de la persona
     * @param curp CURP de la persona
     * @param rfc RFC de la persona
     * @param nombre nombre de la persona
     * @param apellido apellido de la persona
     * @param nacimiento fecha de nacimiento de la persona
     * @param correo correo electrónico de la persona
     * @param telefono número telefónico de la persona
     */
    public Administrador(String usuario, String contraseña, EstadoAdministrador estado, String rol, int id, String curp, String rfc, String nombre, String apellido, Date nacimiento, String correo, String telefono) {
        super(id, curp, rfc, nombre, apellido, nacimiento, correo, telefono);
        this.usuario = usuario;
        this.contraseña = contraseña;
        this.estado = estado;
        this.rol = rol;
    }

    /**
     * @return el nombre de usuario del administrador
     */
    public String getUsuario() {
        return usuario;
    }

    /**
     * @param usuario nuevo nombre de usuario del administrador
     */
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    /**
     * @return la contraseña del administrador
     */
    public String getContraseña() {
        return contraseña;
    }

    /**
     * @param contraseña nueva contraseña del administrador
     */
    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    /**
     * @return el estado actual del administrador
     */
    public EstadoAdministrador getEstado() {
        return estado;
    }

    /**
     * @param estado nuevo estado del administrador
     */
    public void setEstado(EstadoAdministrador estado) {
        this.estado = estado;
    }

    /**
     * @return el rol del administrador dentro del sistema
     */
    public String getRol() {
        return rol;
    }

    /**
     * @param rol nuevo rol del administrador
     */
    public void setRol(String rol) {
        this.rol = rol;
    }

    /**
     * @return representación en texto con el estado completo del administrador,
     * incluyendo los datos heredados de Persona
     */
    @Override
    public String toString() {
        return super.toString() + "\nAdministrador{" + "usuario=" + usuario + ", contrase\u00f1a=" + contraseña + ", estado=" + estado + ", rol=" + rol + '}';
    }
}