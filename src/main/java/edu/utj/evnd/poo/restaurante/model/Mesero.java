package edu.utj.evnd.poo.restaurante.model;

import edu.utj.evnd.poo.restaurante.enums.TurnoMesero;
import java.util.Date;

/**
 * Representa a un mesero del restaurante. Extiende de Persona agregando
 * su número de empleado y el turno en el que trabaja. Es una clase
 * terminal del árbol de herencia.
 *
 * @author Gael Sebastian Garcia Valdez
 */
public final class Mesero extends Persona {
    private int numEmpleado;
    private TurnoMesero turno;

    /**
     * Constructor vacío. Inicializa el número de empleado en 0 y el turno
     * en MATUTINO para garantizar un estado inicial válido.
     */
    public Mesero() {
        numEmpleado = 0;
        turno = TurnoMesero.MATUTINO;
    }

    /**
     * Constructor con los datos mínimos requeridos, incluyendo los
     * atributos heredados de Persona.
     *
     * @param numEmpleado número de empleado del mesero
     * @param turno turno de trabajo del mesero
     * @param id identificador numérico único de la persona
     * @param curp CURP de la persona
     * @param rfc RFC de la persona
     * @param nombre nombre de la persona
     * @param apellido apellido de la persona
     * @param nacimiento fecha de nacimiento de la persona
     * @param correo correo electrónico de la persona
     * @param telefono número telefónico de la persona
     */
    public Mesero(int numEmpleado, TurnoMesero turno, int id, String curp, String rfc, String nombre, String apellido, Date nacimiento, String correo, String telefono) {
        super(id, curp, rfc, nombre, apellido, nacimiento, correo, telefono);
        this.numEmpleado = numEmpleado;
        this.turno = turno;
    }

    /**
     * @return el número de empleado del mesero
     */
    public int getNumEmpleado() {
        return numEmpleado;
    }

    /**
     * @param numEmpleado nuevo número de empleado del mesero
     */
    public void setNumEmpleado(int numEmpleado) {
        this.numEmpleado = numEmpleado;
    }

    /**
     * @return el turno de trabajo del mesero
     */
    public TurnoMesero getTurno() {
        return turno;
    }

    /**
     * @param turno nuevo turno de trabajo del mesero
     */
    public void setTurno(TurnoMesero turno) {
        this.turno = turno;
    }

    /**
     * @return representación en texto con el estado completo del mesero,
     * incluyendo los datos heredados de Persona
     */
    @Override
    public String toString() {
        return super.toString() + "\nMesero{" + "numEmpleado=" + numEmpleado + ", turno=" + turno + '}';
    }
}