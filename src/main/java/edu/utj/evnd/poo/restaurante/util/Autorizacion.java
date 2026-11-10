package edu.utj.evnd.poo.restaurante.util;

import edu.utj.evnd.poo.restaurante.model.Permiso;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;

/**
 * Habilita o deshabilita los botones de una pantalla según los permisos del
 * rol que inició sesión. Así no hace falta modificar cada controlador.
 *
 * @author Gael Sebastian Garcia Valdez
 */
public final class Autorizacion {

    private Autorizacion() {
    }

    public static void aplicar(Parent vista, String modulo) {
        recorrer(vista, Sesion.getPermiso(modulo));
    }

    // Revisa todos los controles de la pantalla, incluidos los que están dentro de otros contenedores
    private static void recorrer(Node nodo, Permiso permiso) {
        if (nodo instanceof Button) {
            ajustar((Button) nodo, permiso);
        }
        if (nodo instanceof Parent) {
            for (Node hijo : ((Parent) nodo).getChildrenUnmodifiable()) {
                recorrer(hijo, permiso);
            }
        }
    }

    private static void ajustar(Button boton, Permiso permiso) {
        String texto = boton.getText();
        if (texto == null) {
            return;
        }
        switch (texto) {
            case "Agregar" -> boton.setDisable(!permiso.crear());
            case "Buscar" -> boton.setDisable(!permiso.consultar());
            case "Actualizar" -> boton.setDisable(!permiso.actualizar());
            case "Borrar" -> boton.setDisable(!permiso.eliminar());
            case "Cargar Imagen", "Agregar platillo", "Quitar platillo" ->
                boton.setDisable(!(permiso.crear() || permiso.actualizar()));
            default -> { }
        }
    }
}