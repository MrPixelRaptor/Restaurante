package edu.utj.evnd.poo.restaurante.model;

import java.util.Objects;

/**
 * Representa una línea de un pedido: un platillo, su cantidad, el precio
 * al momento de pedirlo y el descuento aplicado (en porcentaje).
 *
 * @author Gael Sebastian Garcia Valdez
 */
public class DetallePedido {
    private int idDetalle;
    private int idPedido;
    private int idPlatillo;
    private int cantidad;
    private double precio;
    private double descuento;

    /**
     * Constructor vacío. La cantidad inicia en 1.
     */
    public DetallePedido() {
        cantidad = 1;
    }

    /**
     * Constructor con todos los datos del detalle.
     *
     * @param idDetalle identificador numérico único del detalle
     * @param idPedido identificador del pedido al que pertenece
     * @param idPlatillo identificador del platillo pedido
     * @param cantidad cantidad de platillos
     * @param precio precio unitario del platillo al momento del pedido
     * @param descuento descuento aplicado al platillo, en porcentaje (0 a 100)
     */
    public DetallePedido(int idDetalle, int idPedido, int idPlatillo, int cantidad, double precio, double descuento) {
        this.idDetalle = idDetalle;
        this.idPedido = idPedido;
        this.idPlatillo = idPlatillo;
        this.cantidad = cantidad;
        this.precio = precio;
        this.descuento = descuento;
    }

    /** @return el identificador del detalle */
    public int getIdDetalle() {
        return idDetalle;
    }

    /** @param idDetalle nuevo identificador del detalle */
    public void setIdDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }

    /** @return el identificador del pedido al que pertenece */
    public int getIdPedido() {
        return idPedido;
    }

    /** @param idPedido nuevo identificador del pedido */
    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    /** @return el identificador del platillo */
    public int getIdPlatillo() {
        return idPlatillo;
    }

    /** @param idPlatillo nuevo identificador del platillo */
    public void setIdPlatillo(int idPlatillo) {
        this.idPlatillo = idPlatillo;
    }

    /** @return la cantidad de platillos */
    public int getCantidad() {
        return cantidad;
    }

    /** @param cantidad nueva cantidad de platillos */
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    /** @return el precio unitario */
    public double getPrecio() {
        return precio;
    }

    /** @param precio nuevo precio unitario */
    public void setPrecio(double precio) {
        this.precio = precio;
    }

    /** @return el descuento aplicado, en porcentaje */
    public double getDescuento() {
        return descuento;
    }

    /** @param descuento nuevo descuento, en porcentaje (0 a 100) */
    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    /**
     * @return el importe sin descuento (cantidad por precio), en pesos
     */
    public double getImporte() {
        return redondear(cantidad * precio);
    }

    /**
     * @return lo que se descuenta en pesos (importe por porcentaje de descuento)
     */
    public double getMontoDescuento() {
        return redondear(cantidad * precio * descuento / 100);
    }

    /**
     * @return el subtotal de la línea ya con el descuento aplicado, en pesos
     */
    public double getSubtotal() {
        return redondear(getImporte() - getMontoDescuento());
    }

    // Redondea a 2 decimales (centavos)
    private static double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    /**
     * Dos detalles son iguales si tienen el mismo idDetalle.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DetallePedido otro = (DetallePedido) obj;
        return idDetalle == otro.idDetalle;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idDetalle);
    }

    /**
     * @return representación en texto con el estado completo del detalle
     */
    @Override
    public String toString() {
        return "DetallePedido{" + "idDetalle=" + idDetalle + ", idPedido=" + idPedido + ", idPlatillo=" + idPlatillo + ", cantidad=" + cantidad + ", precio=" + precio + ", descuento=" + descuento + '}';
    }
}