package edu.utj.evnd.poo.restaurante.model;

import edu.utj.evnd.poo.restaurante.enums.EstacionTrabajo;
import java.util.Date;

/**
 * Representa a un cocinero del restaurante. Extiende de Persona agregando
 * su número de empleado, su especialidad y la estación de trabajo en la
 * que se encuentra. Es una clase terminal del árbol de herencia.
 *
 * @author Gael Sebastian Garcia Valdez
 */
public final class Cocinero extends Persona {
    private int numEmpleado;
    private String especialidad;
    private EstacionTrabajo estacionTrabajo;

    /**
     * Constructor vacío. Inicializa los atributos propios del cocinero en
     * valores por defecto para garantizar un estado inicial válido.
     */
    public Cocinero() {
        numEmpleado = 0;
        especialidad = "";
        estacionTrabajo = EstacionTrabajo.PARRILLA;
    }

    /**
     * Constructor con los datos mínimos requeridos, incluyendo los
     * atributos heredados de Persona.
     *
     * @param numEmpleado número de empleado del cocinero
     * @param especialidad especialidad culinaria del cocinero
     * @param estacionTrabajo estación de la cocina donde trabaja
     * @param id identificador numérico único de la persona
     * @param curp CURP de la persona
     * @param rfc RFC de la persona
     * @param nombre nombre de la persona
     * @param apellido apellido de la persona
     * @param nacimiento fecha de nacimiento de la persona
     * @param correo correo electrónico de la persona
     * @param telefono número telefónico de la persona
     */
    public Cocinero(int numEmpleado, String especialidad, EstacionTrabajo estacionTrabajo, int id, String curp, String rfc, String nombre, String apellido, Date nacimiento, String correo, String telefono) {
        super(id, curp, rfc, nombre, apellido, nacimiento, correo, telefono);
        this.numEmpleado = numEmpleado;
        this.especialidad = especialidad;
        this.estacionTrabajo = estacionTrabajo;
    }

    /**
     * @return el número de empleado del cocinero
     */
    public int getNumEmpleado() {
        return numEmpleado;
    }

    /**
     * @param numEmpleado nuevo número de empleado del cocinero
     */
    public void setNumEmpleado(int numEmpleado) {
        this.numEmpleado = numEmpleado;
    }

    /**
     * @return la especialidad del cocinero
     */
    public String getEspecialidad() {
        return especialidad;
    }

    /**
     * @param especialidad nueva especialidad del cocinero
     */
    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    /**
     * @return la estación de trabajo del cocinero
     */
    public EstacionTrabajo getEstacionTrabajo() {
        return estacionTrabajo;
    }

    /**
     * @param estacionTrabajo nueva estación de trabajo del cocinero
     */
    public void setEstacionTrabajo(EstacionTrabajo estacionTrabajo) {
        this.estacionTrabajo = estacionTrabajo;
    }

    /**
     * @return representación en texto con el estado completo del cocinero,
     * incluyendo los datos heredados de Persona
     */
    @Override
    public String toString() {
        return super.toString() + "\nCocinero{" + "numEmpleado=" + numEmpleado + ", especialidad=" + especialidad + ", estacionTrabajo=" + estacionTrabajo + '}';
    }
}