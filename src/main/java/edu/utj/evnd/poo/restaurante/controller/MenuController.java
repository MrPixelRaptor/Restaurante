package edu.utj.evnd.poo.restaurante.controller;

import edu.utj.evnd.poo.restaurante.model.Usuario;
import edu.utj.evnd.poo.restaurante.util.Autorizacion;
import edu.utj.evnd.poo.restaurante.util.Sesion;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Controlador del menú principal. Solo muestra los módulos que el rol puede
 * consultar, aplica los permisos del rol a cada pantalla y permite cerrar sesión.
 *
 * @author Gael Sebastian Garcia Valdez
 */
public class MenuController implements Initializable {

    private static final String ESTILO_NORMAL =
        "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14; -fx-alignment: CENTER_LEFT; -fx-cursor: hand;";
    private static final String ESTILO_ACTIVO =
        "-fx-background-color: #C0643C; -fx-text-fill: white; -fx-font-size: 14; -fx-font-weight: bold; -fx-alignment: CENTER_LEFT; -fx-cursor: hand;";

    @FXML private BorderPane contenedor;
    @FXML private Label lblUsuario;
    @FXML private Button btnAdministradores;
    @FXML private Button btnMeseros;
    @FXML private Button btnCocineros;
    @FXML private Button btnClientes;
    @FXML private Button btnCategorias;
    @FXML private Button btnPlatillos;
    @FXML private Button btnReservas;
    @FXML private Button btnPedidos;

    // Módulo (nombre de la tabla) con su botón y su pantalla
    private final Map<String, Button> botones = new LinkedHashMap<>();
    private final Map<String, String> rutas = new HashMap<>();

    // Pantallas ya cargadas, para reutilizarlas y no perder sus datos
    private final Map<String, Parent> vistas = new HashMap<>();

    @FXML
    private void mostrarAdministradores(){
        mostrar("administrador");
    }

    @FXML
    private void mostrarMeseros(){
        mostrar("mesero");
    }

    @FXML
    private void mostrarCocineros(){
        mostrar("cocinero");
    }

    @FXML
    private void mostrarClientes(){
        mostrar("cliente");
    }

    @FXML
    private void mostrarCategorias(){
        mostrar("categoria");
    }

    @FXML
    private void mostrarPlatillos(){
        mostrar("platillo");
    }

    @FXML
    private void mostrarReservas(){
        mostrar("reserva");
    }

    @FXML
    private void mostrarPedidos(){
        mostrar("pedido");
    }

    @FXML
    private void cerrarSesion(){
        Stage actual = (Stage) contenedor.getScene().getWindow();
        Sesion.cerrar(); // registra el cierre en la bitácora
        try {
            // Primero se abre el login y después se cierra esta ventana, para que la aplicación no termine sola
            LoginController.mostrar(new Stage());
            actual.close();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("No se pudo abrir el inicio de sesión: " + e.getMessage());
        }
    }

    private void mostrar(String modulo){
        try {
            Parent vista = vistas.get(modulo);
            if (vista == null) {
                vista = FXMLLoader.load(getClass().getResource(rutas.get(modulo)));
                Autorizacion.aplicar(vista, modulo); // se apagan los botones que el rol no puede usar
                vistas.put(modulo, vista);
            }
            contenedor.setCenter(vista);
            marcarActivo(botones.get(modulo));
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
            mostrarError("No se pudo abrir el módulo: " + e.getMessage());
        }
    }

    private void marcarActivo(Button activo){
        for (Button b : botones.values()) {
            b.setStyle(b == activo ? ESTILO_ACTIVO : ESTILO_NORMAL);
        }
    }

    private void mostrarError(String mensaje){
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        botones.put("administrador", btnAdministradores);
        botones.put("mesero", btnMeseros);
        botones.put("cocinero", btnCocineros);
        botones.put("cliente", btnClientes);
        botones.put("categoria", btnCategorias);
        botones.put("platillo", btnPlatillos);
        botones.put("reserva", btnReservas);
        botones.put("pedido", btnPedidos);

        rutas.put("administrador", "/fxml/administrador/administrador.fxml");
        rutas.put("mesero", "/fxml/mesero/mesero.fxml");
        rutas.put("cocinero", "/fxml/cocinero/cocinero.fxml");
        rutas.put("cliente", "/fxml/cliente/cliente.fxml");
        rutas.put("categoria", "/fxml/categoria/categoria.fxml");
        rutas.put("platillo", "/fxml/platillo/platillo.fxml");
        rutas.put("reserva", "/fxml/reserva/reserva.fxml");
        rutas.put("pedido", "/fxml/pedido/pedido.fxml");

        // Solo se muestran los módulos que el rol puede consultar
        for (Map.Entry<String, Button> entrada : botones.entrySet()) {
            boolean acceso = Sesion.puedeConsultar(entrada.getKey());
            entrada.getValue().setVisible(acceso);
            entrada.getValue().setManaged(acceso);
            entrada.getValue().setStyle(ESTILO_NORMAL);
        }

        Usuario usuario = Sesion.getUsuario();
        if (usuario != null) {
            lblUsuario.setText(usuario.nombre() + " (" + usuario.rol() + ")");
        }

        // Pantalla inicial: el primer módulo al que el rol tiene acceso
        for (String modulo : botones.keySet()) {
            if (Sesion.puedeConsultar(modulo)) {
                mostrar(modulo);
                return;
            }
        }
        mostrarError("Tu rol no tiene módulos asignados");
    }
}