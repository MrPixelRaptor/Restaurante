package edu.utj.evnd.poo.restaurante.service;

import edu.utj.evnd.poo.restaurante.model.Platillo;
import edu.utj.evnd.poo.restaurante.repository.PlatilloRepository;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Lógica de negocio para la gestión de platillos. Es una instancia única
 * compartida, porque otros módulos (como Pedido) necesitan consultar los
 * mismos platillos. El borrado es lógico: el platillo se marca como inactivo
 * y su información (incluida la imagen) se conserva.
 *
 * @author Gael Sebastian Garcia Valdez
 */
public class PlatilloService {
    private static final PlatilloService INSTANCIA = new PlatilloService();

    private final PlatilloRepository repository = new PlatilloRepository();

    private PlatilloService() {
    }

    public static PlatilloService getInstance(){
        return INSTANCIA;
    }

    /**
     * @return null si se guardó, o el mensaje que explica por qué no se pudo
     */
    public String agregar(Platillo platillo) throws SQLException {
        if (repository.existeId(platillo.getIdPlatillo())) {
            return "Ya existe un platillo con ese ID (puede estar dado de baja)";
        }
        repository.agregar(platillo);
        return null;
    }

    /**
     * @return null si se actualizó, o el mensaje que explica por qué no se pudo
     */
    public String actualizar(Platillo platillo) throws SQLException {
        if (repository.obtenerPorId(platillo.getIdPlatillo()) == null) {
            return "No se encontró el platillo para actualizar";
        }
        repository.actualizar(platillo);
        return null;
    }

    /**
     * Borrado lógico: marca el platillo como inactivo.
     *
     * @return true si se dio de baja
     */
    public boolean eliminar(int id) throws SQLException {
        return repository.darDeBaja(id) > 0;
    }

    public ArrayList<Platillo> obtenerTodos() throws SQLException {
        return repository.obtenerTodos();
    }

    public Platillo buscarPorId(int id) throws SQLException {
        return repository.obtenerPorId(id);
    }
}