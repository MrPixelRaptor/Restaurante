// Gael Sebastian Garcia Valdez
package edu.utj.evnd.poo.restaurante.model;

import edu.utj.evnd.poo.restaurante.enums.EstadoReserva;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Representa la reserva de una mesa hecha por un cliente.
 *
 * @author Gael Sebastian Garcia Valdez
 */
public class Reserva {
    private int idReserva;
    private Cliente cliente;
    private int mesa;
    private LocalDate fecha;
    private LocalTime hora;
    private int numeroPersonas;
    private EstadoReserva estado;

    /**
     * Constructor vacío. La reserva inicia en estado PENDIENTE.
     */
    public Reserva() {
        estado = EstadoReserva.PENDIENTE;
    }

    /**
     * Constructor con todos los datos de la reserva.
     *
     * @param idReserva identificador numérico único de la reserva
     * @param cliente cliente que hace la reserva
     * @param mesa número de mesa reservada
     * @param fecha fecha de la reserva
     * @param hora hora de la reserva
     * @param numeroPersonas cantidad de personas
     * @param estado estado actual de la reserva
     */
    public Reserva(int idReserva, Cliente cliente, int mesa, LocalDate fecha, LocalTime hora, int numeroPersonas, EstadoReserva estado) {
        this.idReserva = idReserva;
        this.cliente = cliente;
        this.mesa = mesa;
        this.fecha = fecha;
        this.hora = hora;
        this.numeroPersonas = numeroPersonas;
        this.estado = estado;
    }

    /** @return el identificador de la reserva */
    public int getIdReserva() {
        return idReserva;
    }

    /** @param idReserva nuevo identificador de la reserva */
    public void setIdReserva(int idReserva) {
        this.idReserva = idReserva;
    }

    /** @return el cliente que hizo la reserva */
    public Cliente getCliente() {
        return cliente;
    }

    /** @param cliente nuevo cliente de la reserva */
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    /** @return el número de mesa reservada */
    public int getMesa() {
        return mesa;
    }

    /** @param mesa nuevo número de mesa */
    public void setMesa(int mesa) {
        this.mesa = mesa;
    }

    /** @return la fecha de la reserva */
    public LocalDate getFecha() {
        return fecha;
    }

    /** @param fecha nueva fecha de la reserva */
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    /** @return la hora de la reserva */
    public LocalTime getHora() {
        return hora;
    }

    /** @param hora nueva hora de la reserva */
    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    /** @return la cantidad de personas de la reserva */
    public int getNumeroPersonas() {
        return numeroPersonas;
    }

    /** @param numeroPersonas nueva cantidad de personas */
    public void setNumeroPersonas(int numeroPersonas) {
        this.numeroPersonas = numeroPersonas;
    }

    /** @return el estado de la reserva */
    public EstadoReserva getEstado() {
        return estado;
    }

    /** @param estado nuevo estado de la reserva */
    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }

    /**
     * Dos reservas son iguales si tienen el mismo idReserva.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Reserva otra = (Reserva) obj;
        return idReserva == otra.idReserva;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idReserva);
    }

    /**
     * @return representación en texto con el estado completo de la reserva
     */
    @Override
    public String toString() {
        return "Reserva{" + "idReserva=" + idReserva + ", cliente=" + (cliente != null ? cliente.getNombre() : "sin cliente") + ", mesa=" + mesa + ", fecha=" + fecha + ", hora=" + hora + ", numeroPersonas=" + numeroPersonas + ", estado=" + estado + '}';
    }
}