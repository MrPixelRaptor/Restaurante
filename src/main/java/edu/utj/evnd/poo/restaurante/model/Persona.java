package edu.utj.evnd.poo.restaurante.model;

import java.util.Date;
import java.util.Objects;

/**
 * Clase abstracta que representa a una persona dentro del sistema del restaurante.
 * Define los atributos y comportamientos comunes a cualquier entidad de tipo persona
 * (por ejemplo, Administrador). No puede instanciarse directamente.
 *
 * @author Gael Sebasstian Garcia Valdez
 */
public abstract class Persona {
    private int id;
    private String curp;
    private String rfc;
    private String nombre;
    private String apellido;
    private Date nacimiento;
    private String correo;
    private String telefono;
    private String urlImagen;

    /**
     * Constructor vacío. Inicializa los atributos de tipo String en cadena vacía
     * y la fecha de nacimiento en la fecha actual, para garantizar un estado
     * inicial válido sin necesidad de parámetros.
     */
    public Persona() {
        curp = "";
        rfc = "";
        nombre = "";
        apellido = "";
        nacimiento = new Date();
        correo = "";
        telefono = "";
        urlImagen = "";
    }

    /**
     * Constructor con los datos mínimos requeridos para que una Persona
     * nazca en un estado válido e íntegro.
     *
     * @param id identificador numérico único de la persona
     * @param curp CURP de la persona
     * @param rfc RFC de la persona
     * @param nombre nombre de la persona
     * @param apellido apellido de la persona
     * @param nacimiento fecha de nacimiento de la persona
     * @param correo correo electrónico de la persona
     * @param telefono número telefónico de la persona
     */
    public Persona(int id, String curp, String rfc, String nombre, String apellido, Date nacimiento, String correo, String telefono) {
        this.id = id;
        this.curp = curp;
        this.rfc = rfc;
        this.nombre = nombre;
        this.apellido = apellido;
        this.nacimiento = nacimiento;
        this.correo = correo;
        this.telefono = telefono;
    }

    /**
     * @return el id de la persona
     */
    public int getId() {
        return id;
    }

    /**
     * @param id nuevo id de la persona
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return el CURP de la persona
     */
    public String getCurp() {
        return curp;
    }

    /**
     * @param curp nuevo CURP de la persona
     */
    public void setCurp(String curp) {
        this.curp = curp;
    }

    /**
     * @return el RFC de la persona
     */
    public String getRfc() {
        return rfc;
    }

    /**
     * @param rfc nuevo RFC de la persona
     */
    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    /**
     * @return el nombre de la persona
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre nuevo nombre de la persona
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return el apellido de la persona
     */
    public String getApellido() {
        return apellido;
    }

    /**
     * @param apellido nuevo apellido de la persona
     */
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    /**
     * @return la fecha de nacimiento de la persona
     */
    public Date getNacimiento() {
        return nacimiento;
    }

    /**
     * @param nacimiento nueva fecha de nacimiento de la persona
     */
    public void setNacimiento(Date nacimiento) {
        this.nacimiento = nacimiento;
    }

    /**
     * @return el correo electrónico de la persona
     */
    public String getCorreo() {
        return correo;
    }

    /**
     * @param correo nuevo correo electrónico de la persona
     */
    public void setCorreo(String correo) {
        this.correo = correo;
    }

    /**
     * @return el teléfono de la persona
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * @param telefono nuevo teléfono de la persona
     */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /**
     * @return la URL de la imagen de perfil de la persona
     */
    public String getUrlImagen() {
        return urlImagen;
    }

    /**
     * @param urlImagen nueva URL de la imagen de perfil de la persona
     */
    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }

    /**
     * Calcula el código hash basándose estrictamente en el id de la persona.
     *
     * @return código hash generado a partir del id
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Compara esta persona con otro objeto, considerándolos iguales
     * únicamente si comparten el mismo id.
     *
     * @param obj objeto a comparar
     * @return true si ambos objetos son Persona y tienen el mismo id
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Persona other = (Persona) obj;
        return this.id == other.id;
    }

    /**
     * @return representación en texto con el estado completo de la persona
     */
    @Override
    public String toString() {
        return "Persona{" + "id=" + id + ", curp=" + curp + ", rfc=" + rfc +
                ", nombre=" + nombre + ", apellido=" + apellido + ", nacimiento=" + nacimiento +
                ", correo=" + correo + ", telefono=" + telefono + ", urlImagen=" + urlImagen + '}';
    }
}