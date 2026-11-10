package edu.utj.evnd.poo.restaurante.model;

import edu.utj.evnd.poo.restaurante.enums.MetodoPago;
import java.util.Date;

/**
 * Representa a un cliente del restaurante. Extiende de Persona agregando
 * su método de pago preferido y los puntos de fidelidad acumulados. Es una
 * clase terminal del árbol de herencia.
 *
 * @author Gael Sebastian Garcia Valdez
 */
public final class Cliente extends Persona {
    private MetodoPago metodoPago;
    private int puntosFidelidad;

    /**
     * Constructor vacío. Inicializa el método de pago en EFECTIVO y los
     * puntos de fidelidad en 0 para garantizar un estado inicial válido.
     */
    public Cliente() {
        metodoPago = MetodoPago.EFECTIVO;
        puntosFidelidad = 0;
    }

    /**
     * Constructor con los datos mínimos requeridos, incluyendo los
     * atributos heredados de Persona.
     *
     * @param metodoPago método de pago preferido del cliente
     * @param puntosFidelidad puntos de fidelidad acumulados
     * @param id identificador numérico único de la persona
     * @param curp CURP de la persona
     * @param rfc RFC de la persona
     * @param nombre nombre de la persona
     * @param apellido apellido de la persona
     * @param nacimiento fecha de nacimiento de la persona
     * @param correo correo electrónico de la persona
     * @param telefono número telefónico de la persona
     */
    public Cliente(MetodoPago metodoPago, int puntosFidelidad, int id, String curp, String rfc, String nombre, String apellido, Date nacimiento, String correo, String telefono) {
        super(id, curp, rfc, nombre, apellido, nacimiento, correo, telefono);
        this.metodoPago = metodoPago;
        this.puntosFidelidad = puntosFidelidad;
    }

    /**
     * @return el método de pago preferido del cliente
     */
    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    /**
     * @param metodoPago nuevo método de pago preferido del cliente
     */
    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    /**
     * @return los puntos de fidelidad acumulados del cliente
     */
    public int getPuntosFidelidad() {
        return puntosFidelidad;
    }

    /**
     * @param puntosFidelidad nuevos puntos de fidelidad del cliente
     */
    public void setPuntosFidelidad(int puntosFidelidad) {
        this.puntosFidelidad = puntosFidelidad;
    }

    /**
     * @return representación en texto con el estado completo del cliente,
     * incluyendo los datos heredados de Persona
     */
    @Override
    public String toString() {
        return super.toString() + "\nCliente{" + "metodoPago=" + metodoPago + ", puntosFidelidad=" + puntosFidelidad + '}';
    }
}