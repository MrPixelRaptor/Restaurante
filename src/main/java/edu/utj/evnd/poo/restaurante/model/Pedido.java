package edu.utj.evnd.poo.restaurante.model;

import edu.utj.evnd.poo.restaurante.enums.EstadoPedido;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Representa un pedido del restaurante. Contiene la lista de sus detalles
 * (una línea por platillo pedido) y calcula su descuento, IVA y total a
 * partir de ellos.
 *
 * @author Gael Sebastian Garcia Valdez
 */
public class Pedido {
    /** Tasa de IVA aplicada a los pedidos (16%). */
    public static final double TASA_IVA = 0.16;

    private int idPedido;
    private LocalDate fecha;
    private double iva;
    private double descuento;
    private double total;
    private EstadoPedido estado;
    private final ArrayList<DetallePedido> detalles;

    /**
     * Constructor vacío. El pedido inicia en estado PENDIENTE, con la fecha
     * de hoy y sin detalles.
     */
    public Pedido() {
        fecha = LocalDate.now();
        estado = EstadoPedido.PENDIENTE;
        detalles = new ArrayList<>();
    }

    /**
     * Constructor con todos los datos del pedido (sin detalles; se agregan
     * después con agregarDetalle).
     *
     * @param idPedido identificador numérico único del pedido
     * @param fecha fecha del pedido
     * @param iva monto del IVA, en pesos
     * @param descuento descuento total del pedido, en pesos
     * @param total total a pagar, en pesos
     * @param estado estado actual del pedido
     */
    public Pedido(int idPedido, LocalDate fecha, double iva, double descuento, double total, EstadoPedido estado) {
        this.idPedido = idPedido;
        this.fecha = fecha;
        this.iva = iva;
        this.descuento = descuento;
        this.total = total;
        this.estado = estado;
        this.detalles = new ArrayList<>();
    }

    /** @return el identificador del pedido */
    public int getIdPedido() {
        return idPedido;
    }

    /**
     * Cambia el identificador del pedido y también el de todos sus detalles,
     * para que siempre coincidan.
     *
     * @param idPedido nuevo identificador del pedido
     */
    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
        for (DetallePedido d : detalles) {
            d.setIdPedido(idPedido);
        }
    }

    /** @return la fecha del pedido */
    public LocalDate getFecha() {
        return fecha;
    }

    /** @param fecha nueva fecha del pedido */
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    /** @return el monto del IVA, en pesos */
    public double getIva() {
        return iva;
    }

    /** @param iva nuevo monto del IVA, en pesos */
    public void setIva(double iva) {
        this.iva = iva;
    }

    /** @return el descuento total, en pesos */
    public double getDescuento() {
        return descuento;
    }

    /** @param descuento nuevo descuento total, en pesos */
    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    /** @return el total a pagar, en pesos */
    public double getTotal() {
        return total;
    }

    /** @param total nuevo total a pagar, en pesos */
    public void setTotal(double total) {
        this.total = total;
    }

    /** @return el estado del pedido */
    public EstadoPedido getEstado() {
        return estado;
    }

    /** @param estado nuevo estado del pedido */
    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    /**
     * @return una copia de la lista de detalles (agregar o quitar elementos
     * de la copia no cambia el pedido)
     */
    public ArrayList<DetallePedido> getDetalles() {
        return new ArrayList<>(detalles);
    }

    /**
     * @return la suma de los subtotales de los detalles (ya con descuento,
     * antes de IVA), en pesos
     */
    public double getSubtotal() {
        double suma = 0;
        for (DetallePedido d : detalles) {
            suma += d.getSubtotal();
        }
        return redondear(suma);
    }

    /**
     * Agrega un detalle al pedido, le asigna el idPedido de este pedido y
     * recalcula los totales.
     *
     * @param detalle detalle a agregar
     */
    public void agregarDetalle(DetallePedido detalle) {
        detalle.setIdPedido(idPedido);
        detalles.add(detalle);
        recalcularTotales();
    }

    /**
     * Elimina del pedido el detalle con el id indicado y recalcula los totales.
     *
     * @param idDetalle identificador del detalle a quitar
     * @return true si se encontró y se eliminó
     */
    public boolean eliminarDetalle(int idDetalle) {
        boolean quitado = detalles.removeIf(d -> d.getIdDetalle() == idDetalle);
        recalcularTotales();
        return quitado;
    }

    /**
     * Quita todos los detalles del pedido y recalcula los totales.
     */
    public void limpiarDetalles() {
        detalles.clear();
        recalcularTotales();
    }

    /**
     * Recalcula descuento, IVA y total a partir de los detalles actuales.
     * El descuento es la suma de lo descontado en cada línea; el IVA se
     * calcula sobre el subtotal ya con descuento.
     */
    public void recalcularTotales() {
        double descuentoTotal = 0;
        for (DetallePedido d : detalles) {
            descuentoTotal += d.getMontoDescuento();
        }
        double subtotal = getSubtotal();
        descuento = redondear(descuentoTotal);
        iva = redondear(subtotal * TASA_IVA);
        total = redondear(subtotal + iva);
    }

    // Redondea a 2 decimales (centavos)
    private static double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    /**
     * Dos pedidos son iguales si tienen el mismo idPedido.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Pedido otro = (Pedido) obj;
        return idPedido == otro.idPedido;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPedido);
    }

    /**
     * @return representación en texto con el estado completo del pedido
     */
    @Override
    public String toString() {
        return "Pedido{" + "idPedido=" + idPedido + ", fecha=" + fecha + ", iva=" + iva + ", descuento=" + descuento + ", total=" + total + ", estado=" + estado + ", detalles=" + detalles.size() + '}';
    }
}