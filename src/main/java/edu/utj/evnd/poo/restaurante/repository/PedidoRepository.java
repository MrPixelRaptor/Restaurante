package edu.utj.evnd.poo.restaurante.repository;

import edu.utj.evnd.poo.restaurante.model.Pedido;
import java.util.ArrayList;

/**
 * Almacenamiento en memoria de los pedidos (sin base de datos).
 *
 * @author Gael Sebastian Garcia Valdez
 */
public class PedidoRepository {
    private final ArrayList<Pedido> pedidos = new ArrayList<>();

    public void agregar(Pedido pedido){
        pedidos.add(pedido);
    }

    public ArrayList<Pedido> obtenerTodos(){
        return new ArrayList<>(pedidos); // copia, para no exponer la lista interna
    }

    public Pedido obtenerPorId(int id){
        for (Pedido p : pedidos) {
            if (p.getIdPedido() == id) {
                return p;
            }
        }
        return null;
    }

    public boolean actualizar(Pedido pedido, int id){
        for (int i = 0; i < pedidos.size(); i++) {
            if (pedidos.get(i).getIdPedido() == id) {
                pedidos.set(i, pedido);
                return true;
            }
        }
        return false;
    }

    public boolean eliminar(int id){
        return pedidos.removeIf(p -> p.getIdPedido() == id);
    }
}