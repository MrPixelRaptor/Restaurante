package edu.utj.evnd.poo.restaurante.controller;

import edu.utj.evnd.poo.restaurante.dao.AutenticacionDAO;
import edu.utj.evnd.poo.restaurante.dao.PermisoDAO;
import edu.utj.evnd.poo.restaurante.dao.SesionDAO;
import edu.utj.evnd.poo.restaurante.model.Permiso;
import edu.utj.evnd.poo.restaurante.model.Usuario;
import edu.utj.evnd.poo.restaurante.util.Sesion;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controlador de la ventana de inicio de sesión, la primera que se muestra.
 *
 * @author Gael Sebastian Garcia Valdez
 */
public class LoginController {

    // Ruta del FXML del menú principal: es la misma que carga tu clase principal
    private static final String RUTA_MENU = "/fxml/menu/menu.fxml";
    private static final String RUTA_LOGIN = "/fxml/login/login.fxml";

    @FXML private TextField txtIdentificador;
    @FXML private PasswordField txtContrasena;
    @FXML private Label lblError;

    private final AutenticacionDAO autenticacionDAO = new AutenticacionDAO();
    private final PermisoDAO permisoDAO = new PermisoDAO();
    private final SesionDAO sesionDAO = new SesionDAO();

    // Muestra la ventana de login en el Stage indicado
    public static void mostrar(Stage stage) throws IOException {
        Parent raiz = FXMLLoader.load(LoginController.class.getResource(RUTA_LOGIN));
        stage.setScene(new Scene(raiz));
        stage.setTitle("Inicio de sesión");
        stage.setResizable(false);
        stage.show();
    }

    @FXML
    private void iniciarSesion(){
        String identificador = txtIdentificador.getText().trim();
        String contrasena = txtContrasena.getText();
        if (identificador.isEmpty() || contrasena.isEmpty()) {
            mostrarError("Escribe tu ID o correo y tu contraseña");
            return;
        }

        try {
            Usuario usuario = autenticacionDAO.iniciarSesion(identificador, contrasena);
            if (usuario == null) {
                mostrarError("ID, correo o contraseña incorrectos");
                return;
            }
            if (!usuario.activo()) {
                mostrarError("La cuenta no está activa");
                return;
            }
            if (usuario.rol() == null) {
                mostrarError("El rol de la cuenta no es válido (Administrador, Gerente o Recepcionista)");
                return;
            }

            Map<String, Permiso> permisos = permisoDAO.obtenerPorRol(usuario.rol());
            int idSesion = sesionDAO.registrarInicio(usuario.id());
            Sesion.iniciar(usuario, permisos, idSesion);

            abrirMenu();
            ((Stage) txtIdentificador.getScene().getWindow()).close();
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarError("No se pudo conectar con la base de datos");
        } catch (IOException e) {
            e.printStackTrace();
            Sesion.cerrar();
            mostrarError("No se pudo abrir el menú: " + e.getMessage());
        }
    }

    // Abre la ventana principal. Al cerrarla, la sesión se registra como terminada.
    private void abrirMenu() throws IOException {
        Parent raiz = FXMLLoader.load(getClass().getResource(RUTA_MENU));
        Stage principal = new Stage();
        principal.setScene(new Scene(raiz, 1100, 700));
        principal.setTitle("Restaurante");
        principal.setOnHidden(e -> Sesion.cerrar());
        principal.show();
    }

    private void mostrarError(String mensaje){
        lblError.setText(mensaje);
    }
}