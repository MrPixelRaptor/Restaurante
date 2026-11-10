package edu.utj.evnd.poo.restaurante.service;

import edu.utj.evnd.poo.restaurante.enums.EstadoPlatillo;
import edu.utj.evnd.poo.restaurante.model.DetallePedido;
import edu.utj.evnd.poo.restaurante.model.Pedido;
import edu.utj.evnd.poo.restaurante.model.Platillo;
import edu.utj.evnd.poo.restaurante.repository.PedidoRepository;
import java.util.ArrayList;

/**
 * Lógica de negocio para la gestión de pedidos y sus detalles.
 *
 * @author Gael Sebastian Garcia Valdez
 */
public class PedidoService {
    private final PedidoRepository repository;
    private final PlatilloService platilloService = PlatilloService.getInstance(); // mismos platillos que el módulo Platillos

    // Los idDetalle son únicos en todo el sistema
    private int siguienteIdDetalle = 1;

    public PedidoService() {
        repository = new PedidoRepository();
    }

    public boolean agregar(Pedido pedido){
        if (repository.obtenerPorId(pedido.getIdPedido()) != null) {
            return false; // ya existe un pedido con ese id
        }
        pedido.recalcularTotales();
        repository.agregar(pedido);
        return true;
    }

    public ArrayList<Pedido> obtenerTodos(){
        return repository.obtenerTodos();
    }

    public Pedido buscarPorId(int id){
        return repository.obtenerPorId(id);
    }

    public boolean actualizar(Pedido pedido, int id){
        if (repository.obtenerPorId(id) == null) {
            return false; // no existe, no se puede actualizar
        }
        pedido.recalcularTotales();
        return repository.actualizar(pedido, id);
    }

    public boolean eliminar(int id){
        if (repository.obtenerPorId(id) == null) {
            return false; // no existe, no se puede borrar
        }
        return repository.eliminar(id);
    }

    /**
     * Agrega un platillo a un pedido. Si el platillo ya estaba en el pedido,
     * suma la cantidad a su línea. Copia el precio y el descuento actuales
     * del platillo y recalcula los totales del pedido.
     *
     * @param pedido pedido al que se agrega (puede ser uno que aún no se guarda)
     * @param idPlatillo id del platillo a pedir
     * @param cantidad cantidad de platillos
     * @return null si todo salió bien, o el mensaje de error a mostrar
     */
        public String agregarPlatillo(Pedido pedido, int idPlatillo, int cantidad){
        if (cantidad <= 0) {
            return "La cantidad debe ser mayor a 0";
        }

        Platillo platillo;
        try {
            platillo = platilloService.buscarPorId(idPlatillo);
        } catch (java.sql.SQLException e) {
            return "Error en la base de datos: " + e.getMessage();
        }
        if (platillo == null) {
            return "El platillo ya no existe";
        }
        if (platillo.getEstado() != EstadoPlatillo.DISPONIBLE) {
            return "El platillo " + platillo.getNombre() + " no está disponible";
        }

        DetallePedido existente = buscarDetalle(pedido, idPlatillo);
        int cantidadFinal = cantidad + (existente != null ? existente.getCantidad() : 0);
        if (cantidadFinal > platillo.getExistencia()) {
            return "Solo hay " + platillo.getExistencia() + " en existencia de " + platillo.getNombre();
        }

        if (existente != null) {
            existente.setCantidad(cantidadFinal);
            pedido.recalcularTotales();
        } else {
            DetallePedido nuevo = new DetallePedido(siguienteIdDetalle++, pedido.getIdPedido(),
                    idPlatillo, cantidad, platillo.getPrecio(), platillo.getDescuento());
            pedido.agregarDetalle(nuevo); // este método ya recalcula los totales
        }
        return null;
    }

    /**
     * Quita un detalle de un pedido y recalcula sus totales.
     *
     * @param pedido pedido del que se quita
     * @param idDetalle id del detalle a quitar
     * @return true si se encontró y se quitó
     */
    public boolean quitarDetalle(Pedido pedido, int idDetalle){
        return pedido.eliminarDetalle(idDetalle); // este método ya recalcula los totales
    }

    // Busca en el pedido la línea de un platillo, o null si no está
    private DetallePedido buscarDetalle(Pedido pedido, int idPlatillo){
        for (DetallePedido d : pedido.getDetalles()) {
            if (d.getIdPlatillo() == idPlatillo) {
                return d;
            }
        }
        return null;
    }
}