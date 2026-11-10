package edu.utj.evnd.poo.restaurante.model;

import edu.utj.evnd.poo.restaurante.enums.EstadoPlatillo;
import java.util.Objects;

/**
 * Representa un platillo del menú del restaurante. Pertenece a una
 * categoría y tiene precio, existencia, descuento (en porcentaje) y un
 * estado. Dos platillos son iguales si comparten el mismo id.
 *
 * @author Gael Sebastian Garcia Valdez
 */
public final class Platillo {
    private int idPlatillo;
    private String nombre;
    private String descripcion;
    private Categoria categoria;
    private double precio;
    private int existencia;
    private double descuento;
    private String urlImagen;
    private EstadoPlatillo estado;

    /**
     * Constructor vacío. Inicializa los atributos en valores por defecto
     * (estado DISPONIBLE) para garantizar un estado inicial válido.
     */
    public Platillo() {
        idPlatillo = 0;
        nombre = "";
        descripcion = "";
        categoria = null;
        precio = 0;
        existencia = 0;
        descuento = 0;
        urlImagen = "";
        estado = EstadoPlatillo.DISPONIBLE;
    }

    /**
     * Constructor con todos los datos del platillo.
     *
     * @param idPlatillo identificador numérico único del platillo
     * @param nombre nombre del platillo
     * @param descripcion descripción del platillo
     * @param categoria categoría a la que pertenece el platillo
     * @param precio precio del platillo
     * @param existencia cantidad disponible en existencia
     * @param descuento descuento aplicado, en porcentaje (0 a 100)
     * @param urlImagen ruta relativa de la imagen del platillo
     * @param estado estado actual del platillo
     */
    public Platillo(int idPlatillo, String nombre, String descripcion, Categoria categoria, double precio, int existencia, double descuento, String urlImagen, EstadoPlatillo estado) {
        this.idPlatillo = idPlatillo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.precio = precio;
        this.existencia = existencia;
        this.descuento = descuento;
        this.urlImagen = urlImagen;
        this.estado = estado;
    }

    /**
     * @return el id del platillo
     */
    public int getIdPlatillo() {
        return idPlatillo;
    }

    /**
     * @param idPlatillo nuevo id del platillo
     */
    public void setIdPlatillo(int idPlatillo) {
        this.idPlatillo = idPlatillo;
    }

    /**
     * @return el nombre del platillo
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre nuevo nombre del platillo
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return la descripción del platillo
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @param descripcion nueva descripción del platillo
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * @return la categoría del platillo
     */
    public Categoria getCategoria() {
        return categoria;
    }

    /**
     * @param categoria nueva categoría del platillo
     */
    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    /**
     * @return el precio del platillo
     */
    public double getPrecio() {
        return precio;
    }

    /**
     * @param precio nuevo precio del platillo
     */
    public void setPrecio(double precio) {
        this.precio = precio;
    }

    /**
     * @return la existencia del platillo
     */
    public int getExistencia() {
        return existencia;
    }

    /**
     * @param existencia nueva existencia del platillo
     */
    public void setExistencia(int existencia) {
        this.existencia = existencia;
    }

    /**
     * @return el descuento del platillo, en porcentaje
     */
    public double getDescuento() {
        return descuento;
    }

    /**
     * @param descuento nuevo descuento del platillo, en porcentaje
     */
    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    /**
     * @return la ruta relativa de la imagen del platillo
     */
    public String getUrlImagen() {
        return urlImagen;
    }

    /**
     * @param urlImagen nueva ruta relativa de la imagen del platillo
     */
    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }

    /**
     * @return el estado actual del platillo
     */
    public EstadoPlatillo getEstado() {
        return estado;
    }

    /**
     * @param estado nuevo estado del platillo
     */
    public void setEstado(EstadoPlatillo estado) {
        this.estado = estado;
    }

    /**
     * Calcula el código hash basándose estrictamente en el id.
     *
     * @return código hash generado a partir del id
     */
    @Override
    public int hashCode() {
        return Objects.hash(idPlatillo);
    }

    /**
     * Compara este platillo con otro objeto, considerándolos iguales
     * únicamente si comparten el mismo id.
     *
     * @param obj objeto a comparar
     * @return true si ambos objetos son Platillo y tienen el mismo id
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
        final Platillo other = (Platillo) obj;
        return this.idPlatillo == other.idPlatillo;
    }

    /**
     * @return representación en texto con el estado completo del platillo
     */
    @Override
    public String toString() {
        return "Platillo{" + "idPlatillo=" + idPlatillo + ", nombre=" + nombre + ", descripcion=" + descripcion
                + ", categoria=" + categoria + ", precio=" + precio + ", existencia=" + existencia
                + ", descuento=" + descuento + ", urlImagen=" + urlImagen + ", estado=" + estado + '}';
    }
}