package edu.utj.evnd.poo.restaurante.controller;

import edu.utj.evnd.poo.restaurante.enums.EstacionTrabajo;
import edu.utj.evnd.poo.restaurante.model.Cocinero;
import edu.utj.evnd.poo.restaurante.service.CocineroService;
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
 * Controlador de la pantalla de cocineros (CRUD conectado a MySQL).
 * El borrado es físico: al eliminar el registro también se borra su imagen.
 *
 * @author Gael Sebastian Garcia Valdez
 */
public class CocineroController implements Initializable {

    private static final String SUBCARPETA_IMAGENES = "cocineros";

    @FXML private TextField txtId;
    @FXML private TextField txtCurp;
    @FXML private TextField txtRfc;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private DatePicker dpNacimiento;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtUrlImagen;
    @FXML private TextField txtNumEmpleado;
    @FXML private TextField txtEspecialidad;
    @FXML private ComboBox<EstacionTrabajo> cmbEstacion;
    @FXML private ImageView imgCocinero;
    @FXML private TableView<Cocinero> tablaCocineros;

    @FXML private TableColumn<Cocinero, Integer> colId;
    @FXML private TableColumn<Cocinero, String> colCurp;
    @FXML private TableColumn<Cocinero, String> colRfc;
    @FXML private TableColumn<Cocinero, String> colNombre;
    @FXML private TableColumn<Cocinero, String> colApellido;
    @FXML private TableColumn<Cocinero, Date> colNacimiento;
    @FXML private TableColumn<Cocinero, String> colCorreo;
    @FXML private TableColumn<Cocinero, String> colTelefono;
    @FXML private TableColumn<Cocinero, String> colUrlImagen;
    @FXML private TableColumn<Cocinero, Integer> colNumEmpleado;
    @FXML private TableColumn<Cocinero, String> colEspecialidad;
    @FXML private TableColumn<Cocinero, EstacionTrabajo> colEstacion;

    private final CocineroService service = new CocineroService();
    private final ObservableList<Cocinero> datosTabla = FXCollections.observableArrayList();

    // Imagen elegida por el usuario que todavía no se copia a la carpeta del proyecto
    private File imagenSeleccionada;

    @FXML
    private void buscar(){
        try {
            Cocinero enc = service.buscarPorCurp(txtCurp.getText().trim());
            if (enc != null) {
                cargarFormulario(enc);
            } else {
                mostrarAlerta("No se encontró ningún cocinero con ese CURP");
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

        Cocinero nuevo = leerFormulario();

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
                mostrarExito("Cocinero agregado correctamente");
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

        Cocinero modificado = leerFormulario();
        String rutaNueva = null;

        try {
            Cocinero anterior = service.buscarPorId(modificado.getId());
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
                mostrarExito("Cocinero actualizado correctamente");
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
            mostrarAlerta("Selecciona un cocinero de la tabla o escribe su CURP");
            return;
        }

        try {
            Cocinero anterior = service.buscarPorCurp(txtCurp.getText().trim());
            if (anterior == null) {
                mostrarAlerta("No se encontró para borrar");
                return;
            }

            if (!confirmar("¿Seguro que quieres eliminar este cocinero?")) {
                return;
            }

            if (service.eliminar(anterior.getId())) {
                GestorImagenes.eliminar(anterior.getUrlImagen()); // borrado físico: ahora sí se borra la imagen
                refrescarTabla();
                limpiar();
                mostrarExito("Cocinero eliminado correctamente");
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
        txtNumEmpleado.clear();
        txtEspecialidad.clear();
        cmbEstacion.getSelectionModel().clearSelection();
        imagenSeleccionada = null;
        imgCocinero.setImage(null);
        txtId.setDisable(false); // el ID solo se escribe al registrar uno nuevo
        tablaCocineros.getSelectionModel().clearSelection(); // así la fila se puede volver a seleccionar
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
        File archivo = fileChooser.showOpenDialog(imgCocinero.getScene().getWindow());
        if (archivo != null) {
            imagenSeleccionada = archivo;
            imgCocinero.setImage(new Image(archivo.toURI().toString())); // solo vista previa
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

    private Cocinero leerFormulario(){
        Cocinero cocinero = new Cocinero();
        cocinero.setId(Integer.parseInt(txtId.getText().trim()));
        cocinero.setCurp(txtCurp.getText().trim());
        cocinero.setRfc(txtRfc.getText().trim());
        cocinero.setNombre(txtNombre.getText().trim());
        cocinero.setApellido(txtApellido.getText().trim());
        Date fecha = Date.from(dpNacimiento.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
        cocinero.setNacimiento(fecha);
        cocinero.setCorreo(txtCorreo.getText().trim());
        cocinero.setTelefono(txtTelefono.getText().trim());
        cocinero.setUrlImagen(txtUrlImagen.getText());
        cocinero.setNumEmpleado(Integer.parseInt(txtNumEmpleado.getText().trim()));
        cocinero.setEspecialidad(txtEspecialidad.getText().trim());
        cocinero.setEstacionTrabajo(cmbEstacion.getValue());
        return cocinero;
    }

    private void cargarFormulario(Cocinero c){
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
        txtNumEmpleado.setText(String.valueOf(c.getNumEmpleado()));
        txtEspecialidad.setText(c.getEspecialidad());
        cmbEstacion.setValue(c.getEstacionTrabajo());
        imgCocinero.setImage(GestorImagenes.cargar(c.getUrlImagen())); // la imagen se muestra al seleccionar o buscar
    }

    private void refrescarTabla(){
        try {
            datosTabla.setAll(service.obtenerTodos());
        } catch (SQLException e) {
            mostrarErrorBD(e);
        }
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

        String numEmpleado = txtNumEmpleado.getText().trim();
        if (numEmpleado.isEmpty()) {
            return "El número de empleado es obligatorio";
        }
        try {
            if (Integer.parseInt(numEmpleado) <= 0) {
                return "El número de empleado debe ser mayor a 0";
            }
        } catch (NumberFormatException e) {
            return "El número de empleado debe ser un número entero";
        }

        if (txtEspecialidad.getText().trim().isEmpty()) {
            return "La especialidad es obligatoria";
        }

        if (cmbEstacion.getValue() == null) {
            return "Selecciona una estación de trabajo";
        }

        return null; // todo está bien
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

        cmbEstacion.getItems().setAll(EstacionTrabajo.values());

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
        colNumEmpleado.setCellValueFactory(new PropertyValueFactory<>("numEmpleado"));
        colEspecialidad.setCellValueFactory(new PropertyValueFactory<>("especialidad"));
        colEstacion.setCellValueFactory(new PropertyValueFactory<>("estacionTrabajo"));

        tablaCocineros.setItems(datosTabla);
        tablaCocineros.getSelectionModel().selectedItemProperty().addListener((obs, viejo, sel) -> {
            if (sel != null) cargarFormulario(sel);
        });

        refrescarTabla();
    }
}