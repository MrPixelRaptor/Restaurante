package edu.utj.evnd.poo.restaurante.controller;

import edu.utj.evnd.poo.restaurante.enums.MetodoPago;
import edu.utj.evnd.poo.restaurante.model.Cliente;
import edu.utj.evnd.poo.restaurante.service.ClienteService;
import edu.utj.evnd.poo.restaurante.util.GestorImagenes;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

/**
 * Controlador de la pantalla de clientes (CRUD conectado a MySQL).
 * El borrado es lógico: el cliente se marca como inactivo y su imagen se conserva.
 *
 * @author Gael Sebastian Garcia Valdez
 */
public class ClienteController implements Initializable {

    private static final String SUBCARPETA_IMAGENES = "clientes";

    @FXML private TextField txtId;
    @FXML private TextField txtCurp;
    @FXML private TextField txtRfc;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private DatePicker dpNacimiento;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtUrlImagen;
    @FXML private ComboBox<MetodoPago> cmbMetodoPago;
    @FXML private TextField txtPuntos;
    @FXML private ImageView imgCliente;
    @FXML private TableView<Cliente> tablaClientes;

    @FXML private TableColumn<Cliente, Integer> colId;
    @FXML private TableColumn<Cliente, String> colCurp;
    @FXML private TableColumn<Cliente, String> colRfc;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colApellido;
    @FXML private TableColumn<Cliente, Date> colNacimiento;
    @FXML private TableColumn<Cliente, String> colCorreo;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, String> colUrlImagen;
    @FXML private TableColumn<Cliente, MetodoPago> colMetodoPago;
    @FXML private TableColumn<Cliente, Integer> colPuntos;

    private final ClienteService service = ClienteService.getInstancia();
    private final ObservableList<Cliente> datosTabla = FXCollections.observableArrayList();

    // Imagen elegida por el usuario que todavía no se copia a la carpeta del proyecto
    private File imagenSeleccionada;

    @FXML
    private void buscar(){
        try {
            Cliente enc = service.buscarPorCurp(txtCurp.getText().trim());
            if (enc != null) {
                cargarFormulario(enc);
            } else {
                mostrarAlerta("No se encontró ningún cliente con ese CURP");
            }
        } catch (SQLException e) {
            mostrarErrorBD(e);
        }
    }

    @FXML
    private void agregar(){
        String error = validar();
        if (error != null) {
            mostrarAlerta(error);
            return;
        }

        Cliente nuevo = leerFormulario();

        String rutaNueva = null;
        if (imagenSeleccionada != null) {
            rutaNueva = copiarImagenSeleccionada();
            if (rutaNueva == null) {
                return; // ya se mostró la alerta de error
            }
            nuevo.setUrlImagen(rutaNueva);
        }

        try {
            String errorServicio = service.agregar(nuevo);
            if (errorServicio == null) {
                refrescarTabla();
                limpiar();
                mostrarExito("Cliente agregado correctamente");
            } else {
                GestorImagenes.eliminar(rutaNueva); // no se guardó, se descarta la copia
                mostrarAlerta(errorServicio);
            }
        } catch (SQLException e) {
            GestorImagenes.eliminar(rutaNueva);
            mostrarErrorBD(e);
        }
    }

    @FXML
    private void actualizar(){
        String error = validar();
        if (error != null) {
            mostrarAlerta(error);
            return;
        }

        Cliente modificado = leerFormulario();
        String rutaNueva = null;

        try {
            Cliente anterior = service.buscarPorId(modificado.getId());
            if (anterior == null) {
                mostrarAlerta("No se encontró para actualizar");
                return;
            }
            String rutaAnterior = anterior.getUrlImagen();

            if (imagenSeleccionada != null) {
                rutaNueva = copiarImagenSeleccionada();
                if (rutaNueva == null) {
                    return; // ya se mostró la alerta de error
                }
                modificado.setUrlImagen(rutaNueva);
            } else {
                modificado.setUrlImagen(rutaAnterior);
            }

            String errorServicio = service.actualizar(modificado);
            if (errorServicio == null) {
                if (rutaNueva != null && !rutaNueva.equals(rutaAnterior)) {
                    GestorImagenes.eliminar(rutaAnterior); // se reemplazó: la imagen vieja ya no la usa ningún registro
                }
                refrescarTabla();
                limpiar();
                mostrarExito("Cliente actualizado correctamente");
            } else {
                GestorImagenes.eliminar(rutaNueva); // no se actualizó, se descarta la copia nueva
                mostrarAlerta(errorServicio);
            }
        } catch (SQLException e) {
            GestorImagenes.eliminar(rutaNueva);
            mostrarErrorBD(e);
        }
    }

    @FXML
    private void borrar(){
        if (txtCurp.getText().trim().isEmpty()) {
            mostrarAlerta("Selecciona un cliente de la tabla o escribe su CURP");
            return;
        }

        try {
            Cliente anterior = service.buscarPorCurp(txtCurp.getText().trim());
            if (anterior == null) {
                mostrarAlerta("No se encontró para borrar");
                return;
            }

            if (!confirmar("¿Seguro que quieres eliminar este cliente?")) {
                return;
            }

            // Borrado lógico: el cliente se marca como inactivo y su imagen se conserva
            if (service.eliminar(anterior.getId())) {
                refrescarTabla();
                limpiar();
                mostrarExito("Cliente eliminado correctamente");
            } else {
                mostrarAlerta("No se encontró para borrar");
            }
        } catch (SQLException e) {
            mostrarErrorBD(e);
        }
    }

    @FXML
    private void limpiar(){
        txtId.clear(); txtCurp.clear(); txtRfc.clear(); txtNombre.clear(); txtApellido.clear();
        dpNacimiento.setValue(null);
        txtCorreo.clear(); txtTelefono.clear(); txtUrlImagen.clear();
        cmbMetodoPago.getSelectionModel().clearSelection();
        txtPuntos.clear();
        imagenSeleccionada = null;
        imgCliente.setImage(null);
        txtId.setDisable(false); // el ID solo se escribe al registrar uno nuevo
        tablaClientes.getSelectionModel().clearSelection(); // así la fila se puede volver a seleccionar
    }

    @FXML
    private void cancelar(){
        limpiar();
    }

    @FXML
    private void cargarImagen(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );
        File archivo = fileChooser.showOpenDialog(imgCliente.getScene().getWindow());
        if (archivo != null) {
            imagenSeleccionada = archivo;
            imgCliente.setImage(new Image(archivo.toURI().toString())); // solo vista previa
            txtUrlImagen.setText(archivo.getName());
        }
    }

    // Copia la imagen elegida a la carpeta del proyecto y devuelve la ruta relativa
    private String copiarImagenSeleccionada(){
        try {
            return GestorImagenes.guardar(imagenSeleccionada, SUBCARPETA_IMAGENES);
        } catch (IOException e) {
            mostrarAlerta("No se pudo guardar la imagen: " + e.getMessage());
            return null;
        }
    }

    private Cliente leerFormulario(){
        Cliente cliente = new Cliente();
        cliente.setId(Integer.parseInt(txtId.getText().trim()));
        cliente.setCurp(txtCurp.getText().trim());
        cliente.setRfc(txtRfc.getText().trim());
        cliente.setNombre(txtNombre.getText().trim());
        cliente.setApellido(txtApellido.getText().trim());
        Date fecha = Date.from(dpNacimiento.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
        cliente.setNacimiento(fecha);
        cliente.setCorreo(txtCorreo.getText().trim());
        cliente.setTelefono(txtTelefono.getText().trim());
        cliente.setUrlImagen(txtUrlImagen.getText());
        cliente.setMetodoPago(cmbMetodoPago.getValue());
        cliente.setPuntosFidelidad(Integer.parseInt(txtPuntos.getText().trim()));
        return cliente;
    }

    private void cargarFormulario(Cliente c){
        imagenSeleccionada = null; // al cargar un registro no hay imagen nueva pendiente
        txtId.setText(String.valueOf(c.getId()));
        txtId.setDisable(true); // el ID es la llave primaria: no se cambia en un registro existente
        txtCurp.setText(c.getCurp());
        txtRfc.setText(c.getRfc());
        txtNombre.setText(c.getNombre());
        txtApellido.setText(c.getApellido());
        if (c.getNacimiento() != null) {
            dpNacimiento.setValue(c.getNacimiento().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        } else {
            dpNacimiento.setValue(null);
        }
        txtCorreo.setText(c.getCorreo());
        txtTelefono.setText(c.getTelefono());
        txtUrlImagen.setText(c.getUrlImagen());
        cmbMetodoPago.setValue(c.getMetodoPago());
        txtPuntos.setText(String.valueOf(c.getPuntosFidelidad()));
        imgCliente.setImage(GestorImagenes.cargar(c.getUrlImagen())); // la imagen se muestra al seleccionar o buscar
    }

    private String validar(){
        String id = txtId.getText().trim();
        if (id.isEmpty()) {
            return "El ID es obligatorio";
        }
        try {
            if (Integer.parseInt(id) <= 0) {
                return "El ID debe ser mayor a 0";
            }
        } catch (NumberFormatException e) {
            return "El ID debe ser un número entero";
        }

        if (txtCurp.getText().trim().length() != 18) {
            return "El CURP debe tener 18 caracteres";
        }

        int largoRfc = txtRfc.getText().trim().length();
        if (largoRfc < 12 || largoRfc > 13) {
            return "El RFC debe tener 12 o 13 caracteres";
        }

        if (txtNombre.getText().trim().isEmpty()) {
            return "El nombre es obligatorio";
        }

        if (txtApellido.getText().trim().isEmpty()) {
            return "El apellido es obligatorio";
        }

        if (dpNacimiento.getValue() == null) {
            return "Selecciona la fecha de nacimiento";
        }
        if (dpNacimiento.getValue().isAfter(LocalDate.now())) {
            return "La fecha de nacimiento no puede ser futura";
        }

        if (!txtCorreo.getText().trim().matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) {
            return "El correo no tiene un formato válido (ejemplo: nombre@correo.com)";
        }

        if (!txtTelefono.getText().trim().matches("\\d{10}")) {
            return "El teléfono debe tener 10 dígitos, solo números";
        }

        if (cmbMetodoPago.getValue() == null) {
            return "Selecciona un método de pago";
        }

        String puntos = txtPuntos.getText().trim();
        if (puntos.isEmpty()) {
            return "Los puntos de fidelidad son obligatorios (puede ser 0)";
        }
        try {
            if (Integer.parseInt(puntos) < 0) {
                return "Los puntos de fidelidad no pueden ser negativos";
            }
        } catch (NumberFormatException e) {
            return "Los puntos de fidelidad deben ser un número entero";
        }

        return null; // todo está bien
    }

    private void refrescarTabla(){
        try {
            datosTabla.setAll(service.obtenerTodos());
        } catch (SQLException e) {
            mostrarErrorBD(e);
        }
    }

    private void mostrarAlerta(String mensaje){
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void mostrarExito(String mensaje){
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    // Error real de la base de datos (MySQL apagado, texto demasiado largo, etc.)
    private void mostrarErrorBD(SQLException e){
        e.printStackTrace();
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setHeaderText(null);
        alerta.setContentText("Error en la base de datos: " + e.getMessage());
        alerta.showAndWait();
    }

    private boolean confirmar(String mensaje){
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        Optional<ButtonType> respuesta = alerta.showAndWait();
        return respuesta.isPresent() && respuesta.get() == ButtonType.OK;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        cmbMetodoPago.getItems().setAll(MetodoPago.values());

        // La ruta de la imagen se llena con el botón de cargar imagen, no se escribe a mano
        txtUrlImagen.setEditable(false);

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCurp.setCellValueFactory(new PropertyValueFactory<>("curp"));
        colRfc.setCellValueFactory(new PropertyValueFactory<>("rfc"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colNacimiento.setCellValueFactory(new PropertyValueFactory<>("nacimiento"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colUrlImagen.setCellValueFactory(new PropertyValueFactory<>("urlImagen"));
        colMetodoPago.setCellValueFactory(new PropertyValueFactory<>("metodoPago"));
        colPuntos.setCellValueFactory(new PropertyValueFactory<>("puntosFidelidad"));

        tablaClientes.setItems(datosTabla);
        tablaClientes.getSelectionModel().selectedItemProperty().addListener((obs, viejo, sel) -> {
            if (sel != null) cargarFormulario(sel);
        });

        refrescarTabla();
    }
}