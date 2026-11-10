package edu.utj.evnd.poo.restaurante.controller;

import edu.utj.evnd.poo.restaurante.enums.EstadoReserva;
import edu.utj.evnd.poo.restaurante.model.Cliente;
import edu.utj.evnd.poo.restaurante.model.Reserva;
import edu.utj.evnd.poo.restaurante.service.ClienteService;
import edu.utj.evnd.poo.restaurante.service.ReservaService;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.util.StringConverter;

/**
 * Controlador de la pantalla de reservas (CRUD conectado a MySQL).
 * El borrado es físico: al eliminar una reserva se borra su fila.
 *
 * @author Gael Sebastian Garcia Valdez
 */
public class ReservaController implements Initializable {

    private static final int MAX_MESAS = 20;
    private static final int MAX_PERSONAS = 20;

    @FXML private TextField txtId;
    @FXML private ComboBox<Cliente> cmbCliente;
    @FXML private TextField txtMesa;
    @FXML private DatePicker dpFecha;
    @FXML private ComboBox<LocalTime> cmbHora;
    @FXML private TextField txtPersonas;
    @FXML private ComboBox<EstadoReserva> cmbEstado;
    @FXML private TableView<Reserva> tablaReservas;

    @FXML private TableColumn<Reserva, Integer> colId;
    @FXML private TableColumn<Reserva, String> colCliente;
    @FXML private TableColumn<Reserva, Integer> colMesa;
    @FXML private TableColumn<Reserva, LocalDate> colFecha;
    @FXML private TableColumn<Reserva, LocalTime> colHora;
    @FXML private TableColumn<Reserva, Integer> colPersonas;
    @FXML private TableColumn<Reserva, EstadoReserva> colEstado;

    private final ReservaService service = new ReservaService();
    private final ClienteService clienteService = ClienteService.getInstancia(); // mismos clientes que el módulo Clientes
    private final ObservableList<Reserva> datosTabla = FXCollections.observableArrayList();

    @FXML
    private void buscar(){
        Integer id = leerId();
        if (id == null) {
            mostrarAlerta("Escribe un ID numérico para buscar");
            return;
        }
        try {
            Reserva enc = service.buscarPorId(id);
            if (enc != null) {
                cargarFormulario(enc);
            } else {
                mostrarAlerta("No se encontró ninguna reserva con ese ID");
            }
        } catch (SQLException e) {
            mostrarErrorBD(e);
        }
    }

    @FXML
    private void agregar(){
        String error = validar(true);
        if (error != null) {
            mostrarAlerta(error);
            return;
        }

        try {
            String errorServicio = service.agregar(leerFormulario());
            if (errorServicio == null) {
                refrescarTabla();
                limpiar();
                mostrarExito("Reserva agregada correctamente");
            } else {
                mostrarAlerta(errorServicio);
            }
        } catch (SQLException e) {
            mostrarErrorBD(e);
        }
    }

    @FXML
    private void actualizar(){
        String error = validar(false);
        if (error != null) {
            mostrarAlerta(error);
            return;
        }

        try {
            String errorServicio = service.actualizar(leerFormulario());
            if (errorServicio == null) {
                refrescarTabla();
                limpiar();
                mostrarExito("Reserva actualizada correctamente");
            } else {
                mostrarAlerta(errorServicio);
            }
        } catch (SQLException e) {
            mostrarErrorBD(e);
        }
    }

    @FXML
    private void borrar(){
        Integer id = leerId();
        if (id == null) {
            mostrarAlerta("Selecciona una reserva de la tabla o escribe su ID");
            return;
        }

        try {
            if (service.buscarPorId(id) == null) {
                mostrarAlerta("No se encontró para borrar");
                return;
            }

            if (!confirmar("¿Seguro que quieres eliminar esta reserva?")) {
                return;
            }

            // Borrado físico: la fila se elimina de la base de datos
            if (service.eliminar(id)) {
                refrescarTabla();
                limpiar();
                mostrarExito("Reserva eliminada correctamente");
            } else {
                mostrarAlerta("No se encontró para borrar");
            }
        } catch (SQLException e) {
            mostrarErrorBD(e);
        }
    }

    @FXML
    private void limpiar(){
        txtId.clear();
        cmbCliente.setValue(null);
        txtMesa.clear();
        dpFecha.setValue(null);
        cmbHora.setValue(null);
        txtPersonas.clear();
        cmbEstado.setValue(EstadoReserva.PENDIENTE);
        txtId.setDisable(false); // el ID solo se escribe al registrar una nueva
        tablaReservas.getSelectionModel().clearSelection(); // así la fila se puede volver a seleccionar
    }

    @FXML
    private void cancelar(){
        limpiar();
    }

    // Devuelve el ID escrito en el formulario, o null si está vacío o no es un número
    private Integer leerId(){
        try {
            return Integer.parseInt(txtId.getText().trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Reserva leerFormulario(){
        return new Reserva(
            Integer.parseInt(txtId.getText().trim()),
            cmbCliente.getValue(),
            Integer.parseInt(txtMesa.getText().trim()),
            dpFecha.getValue(),
            cmbHora.getValue(),
            Integer.parseInt(txtPersonas.getText().trim()),
            cmbEstado.getValue()
        );
    }

    private void cargarFormulario(Reserva r){
        txtId.setText(String.valueOf(r.getIdReserva()));
        txtId.setDisable(true); // el ID es la llave primaria: no se cambia en un registro existente
        cmbCliente.setValue(r.getCliente());
        txtMesa.setText(String.valueOf(r.getMesa()));
        dpFecha.setValue(r.getFecha());
        cmbHora.setValue(r.getHora());
        txtPersonas.setText(String.valueOf(r.getNumeroPersonas()));
        cmbEstado.setValue(r.getEstado());
    }

    // esNueva: la fecha pasada solo se rechaza al crear (una reserva vieja sí se puede editar, por ejemplo para marcarla COMPLETADA)
    private String validar(boolean esNueva){
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

        if (cmbCliente.getValue() == null) {
            return "Selecciona un cliente (si no hay ninguno, primero registra uno en el módulo Clientes)";
        }

        String mesa = txtMesa.getText().trim();
        if (mesa.isEmpty()) {
            return "La mesa es obligatoria";
        }
        try {
            int m = Integer.parseInt(mesa);
            if (m < 1 || m > MAX_MESAS) {
                return "La mesa debe estar entre 1 y " + MAX_MESAS;
            }
        } catch (NumberFormatException e) {
            return "La mesa debe ser un número entero";
        }

        if (dpFecha.getValue() == null) {
            return "Selecciona la fecha de la reserva";
        }
        if (esNueva && dpFecha.getValue().isBefore(LocalDate.now())) {
            return "La fecha no puede ser anterior a hoy";
        }

        if (cmbHora.getValue() == null) {
            return "Selecciona la hora de la reserva";
        }

        String personas = txtPersonas.getText().trim();
        if (personas.isEmpty()) {
            return "El número de personas es obligatorio";
        }
        try {
            int p = Integer.parseInt(personas);
            if (p < 1 || p > MAX_PERSONAS) {
                return "El número de personas debe estar entre 1 y " + MAX_PERSONAS;
            }
        } catch (NumberFormatException e) {
            return "El número de personas debe ser un número entero";
        }

        if (cmbEstado.getValue() == null) {
            return "Selecciona el estado de la reserva";
        }

        return null; // todo está bien
    }

    // Vuelve a leer los clientes activos (la pantalla se carga una sola vez).
    // Conserva el cliente seleccionado, aunque ya esté dado de baja (por ejemplo, el de una reserva antigua).
    private void cargarClientes(){
        Cliente actual = cmbCliente.getValue();
        try {
            cmbCliente.getItems().setAll(clienteService.obtenerTodos());
        } catch (SQLException e) {
            mostrarErrorBD(e);
        }
        cmbCliente.setValue(actual);
    }

    private String textoCliente(Cliente c){
        if (c == null) {
            return "";
        }
        return c.getNombre() + " " + c.getApellido() + " (" + c.getCurp() + ")";
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

    // Error real de la base de datos (MySQL apagado, etc.)
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

        // ComboBox de clientes: muestra "Nombre Apellido (CURP)" y se recarga cada vez que se despliega
        cmbCliente.setConverter(new StringConverter<Cliente>() {
            @Override
            public String toString(Cliente c) {
                return textoCliente(c);
            }

            @Override
            public Cliente fromString(String texto) {
                return null; // el combo no es editable
            }
        });
        cmbCliente.setOnShowing(e -> cargarClientes());
        cargarClientes();

        // Horarios disponibles: de 12:00 a 22:00 cada 30 minutos
        for (LocalTime h = LocalTime.of(12, 0); !h.isAfter(LocalTime.of(22, 0)); h = h.plusMinutes(30)) {
            cmbHora.getItems().add(h);
        }

        cmbEstado.getItems().setAll(EstadoReserva.values());
        cmbEstado.setValue(EstadoReserva.PENDIENTE);

        colId.setCellValueFactory(new PropertyValueFactory<>("idReserva"));
        colCliente.setCellValueFactory(data -> new SimpleStringProperty(textoCliente(data.getValue().getCliente())));
        colMesa.setCellValueFactory(new PropertyValueFactory<>("mesa"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
        colPersonas.setCellValueFactory(new PropertyValueFactory<>("numeroPersonas"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        tablaReservas.setItems(datosTabla);
        tablaReservas.getSelectionModel().selectedItemProperty().addListener((obs, viejo, sel) -> {
            if (sel != null) cargarFormulario(sel);
        });

        // Cada vez que la pantalla se muestra, se actualizan los clientes y la tabla
        tablaReservas.sceneProperty().addListener((obs, viejo, nueva) -> {
            if (nueva != null) {
                cargarClientes();
                refrescarTabla();
            }
        });

        refrescarTabla();
    }
}