package edu.utj.evnd.poo.restaurante.model;

import java.util.Objects;

/**
 * Representa una categoría del menú del restaurante (por ejemplo, bebidas
 * o postres). Dos categorías son iguales si comparten el mismo id.
 *
 * @author Gael Sebastian Garcia Valdez
 */
public final class Categoria {
    private int idCategoria;
    private String nombre;
    private String urlImagen;

    /**
     * Constructor vacío. Inicializa los atributos en valores por defecto
     * para garantizar un estado inicial válido.
     */
    public Categoria() {
        idCategoria = 0;
        nombre = "";
        urlImagen = "";
    }

    /**
     * Constructor con todos los datos de la categoría.
     *
     * @param idCategoria identificador numérico único de la categoría
     * @param nombre nombre de la categoría
     * @param urlImagen ruta relativa de la imagen de la categoría
     */
    public Categoria(int idCategoria, String nombre, String urlImagen) {
        this.idCategoria = idCategoria;
        this.nombre = nombre;
        this.urlImagen = urlImagen;
    }

    /**
     * @return el id de la categoría
     */
    public int getIdCategoria() {
        return idCategoria;
    }

    /**
     * @param idCategoria nuevo id de la categoría
     */
    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    /**
     * @return el nombre de la categoría
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre nuevo nombre de la categoría
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return la ruta relativa de la imagen de la categoría
     */
    public String getUrlImagen() {
        return urlImagen;
    }

    /**
     * @param urlImagen nueva ruta relativa de la imagen de la categoría
     */
    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }

    /**
     * Calcula el código hash basándose estrictamente en el id.
     *
     * @return código hash generado a partir del id
     */
    @Override
    public int hashCode() {
        return Objects.hash(idCategoria);
    }

    /**
     * Compara esta categoría con otro objeto, considerándolos iguales
     * únicamente si comparten el mismo id.
     *
     * @param obj objeto a comparar
     * @return true si ambos objetos son Categoria y tienen el mismo id
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
        final Categoria other = (Categoria) obj;
        return this.idCategoria == other.idCategoria;
    }

    /**
     * @return representación en texto con el estado completo de la categoría
     */
    @Override
    public String toString() {
        return "Categoria{" + "idCategoria=" + idCategoria + ", nombre=" + nombre + ", urlImagen=" + urlImagen + '}';
    }
}