// Gael Sebastian Garcia Valdez
package edu.utj.evnd.poo.restaurante.controller;

import edu.utj.evnd.poo.restaurante.enums.EstadoReserva;
import edu.utj.evnd.poo.restaurante.model.Cliente;
import edu.utj.evnd.poo.restaurante.model.Reserva;
import edu.utj.evnd.poo.restaurante.service.ClienteService;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

/**
 * Controlador de la práctica de CRUD con ArrayList sobre la clase Reserva.
 * Los registros se guardan temporalmente en un ArrayList (sin base de datos
 * ni archivos). La tabla es de solo lectura: para actualizar o eliminar hay
 * que buscar primero y seleccionar un registro.
 *
 * @author Gael Sebastian Garcia Valdez
 */
public class ReservaPracticaController implements Initializable {

    private static final int MAX_MESAS = 20;
    private static final int MAX_PERSONAS = 20;

    private enum Modo { NUEVO, CONSULTA, EDICION }

    @FXML private TextField txtId;
    @FXML private ComboBox<Cliente> cmbCliente;
    @FXML private TextField txtMesa;
    @FXML private DatePicker dpFecha;
    @FXML private ComboBox<LocalTime> cmbHora;
    @FXML private TextField txtPersonas;
    @FXML private ComboBox<EstadoReserva> cmbEstado;

    @FXML private Button btnAgregar;
    @FXML private Button btnEditar;
    @FXML private Button btnActualizar;
    @FXML private Button btnBorrar;

    @FXML private ComboBox<String> cmbCriterio;
    @FXML private TextField txtBuscar;

    @FXML private TableView<Reserva> tablaReservas;
    @FXML private TableColumn<Reserva, Integer> colId;
    @FXML private TableColumn<Reserva, String> colCliente;
    @FXML private TableColumn<Reserva, Integer> colMesa;
    @FXML private TableColumn<Reserva, LocalDate> colFecha;
    @FXML private TableColumn<Reserva, LocalTime> colHora;
    @FXML private TableColumn<Reserva, Integer> colPersonas;
    @FXML private TableColumn<Reserva, EstadoReserva> colEstado;

    private final ArrayList<Reserva> reservas = new ArrayList<>();

    private final ClienteService clienteService = ClienteService.getInstancia();
    private final ObservableList<Reserva> datosTabla = FXCollections.observableArrayList();

    private Reserva seleccionada = null;

    private boolean busquedaRealizada = false;

    @FXML
    private void buscar(){
        String criterio = cmbCriterio.getValue();
        if (criterio == null) {
            invalido("Selecciona el criterio de búsqueda", cmbCriterio);
            return;
        }
        String texto = txtBuscar.getText().trim();
        if (texto.isEmpty()) {
            invalido("Escribe lo que quieres buscar", txtBuscar);
            return;
        }

        ArrayList<Reserva> resultados = new ArrayList<>();
        for (Reserva r : reservas) {
            if (coincide(r, criterio, texto)) {
                resultados.add(r);
            }
        }

        if (resultados.isEmpty()) {
            mostrarAlerta("No se encontró ninguna reserva con ese criterio");
            return;
        }

        limpiarCampos();
        busquedaRealizada = true;
        datosTabla.setAll(resultados);
        if (resultados.size() == 1) {
            tablaReservas.getSelectionModel().select(0);
        }
    }

    @FXML
    private void mostrarTodos(){
        busquedaRealizada = false;
        txtBuscar.clear();
        limpiarCampos();
        refrescarTabla();
    }

    @FXML
    private void agregar(){
        if (!validar(true)) {
            return;
        }

        Reserva nueva = leerFormulario();

        if (indiceDe(nueva.getIdReserva()) != -1) {
            invalido("Ya existe una reserva con ese ID", txtId);
            return;
        }
        if (hayConflicto(nueva, -1)) {
            invalido("Esa mesa ya está reservada en esa fecha y hora", txtMesa);
            return;
        }

        reservas.add(nueva);
        mostrarTodos();
        mostrarExito("Reserva agregada correctamente");
    }

    @FXML
    private void editar(){
        if (!hayRegistroListo("editar")) {
            return;
        }
        aplicarModo(Modo.EDICION);
        cmbCliente.requestFocus();
    }

    @FXML
    private void actualizar(){
        if (!hayRegistroListo("actualizar")) {
            return;
        }
        if (!validar(false)) {
            return;
        }

        Reserva modificada = leerFormulario();
        int id = seleccionada.getIdReserva();
        modificada.setIdReserva(id);

        if (hayConflicto(modificada, id)) {
            invalido("Esa mesa ya está reservada en esa fecha y hora", txtMesa);
            return;
        }

        int indice = indiceDe(id);
        if (indice == -1) {
            mostrarAlerta("No se encontró la reserva para actualizar");
            return;
        }

        reservas.set(indice, modificada);
        mostrarTodos();
        mostrarExito("Reserva actualizada correctamente");
    }

    @FXML
    private void borrar(){
        if (!hayRegistroListo("eliminar")) {
            return;
        }

        if (!confirmar("¿Está seguro de eliminar el registro seleccionado?")) {
            return;
        }

        int id = seleccionada.getIdReserva();
        if (reservas.removeIf(r -> r.getIdReserva() == id)) {
            mostrarTodos();
            mostrarExito("Reserva eliminada correctamente");
        } else {
            mostrarAlerta("No se encontró la reserva para eliminar");
        }
    }

    @FXML
    private void limpiar(){
        limpiarCampos();
    }

    private int indiceDe(int id){
        for (int i = 0; i < reservas.size(); i++) {
            if (reservas.get(i).getIdReserva() == id) {
                return i;
            }
        }
        return -1;
    }

    private boolean hayConflicto(Reserva nueva, int idExcluir){
        for (Reserva r : reservas) {
            if (r.getIdReserva() == idExcluir) continue;
            if (r.getEstado() == EstadoReserva.CANCELADA) continue;
            if (r.getMesa() == nueva.getMesa()
                    && r.getFecha().equals(nueva.getFecha())
                    && r.getHora().equals(nueva.getHora())) {
                return true;
            }
        }
        return false;
    }

    private boolean hayRegistroListo(String accion){
        if (!busquedaRealizada) {
            mostrarAlerta("Para " + accion + " primero busca la reserva con el campo de búsqueda");
            txtBuscar.requestFocus();
            return false;
        }
        if (seleccionada == null) {
            mostrarAlerta("Selecciona de la tabla la reserva que quieres " + accion);
            return false;
        }
        return true;
    }

    private boolean coincide(Reserva r, String criterio, String texto){
        if (criterio.equals("ID")) {
            return String.valueOf(r.getIdReserva()).equals(texto);
        }
        if (criterio.equals("Mesa")) {
            return String.valueOf(r.getMesa()).equals(texto);
        }
        return textoCliente(r.getCliente()).toLowerCase().contains(texto.toLowerCase());
    }

    private void limpiarCampos(){
        seleccionada = null;
        txtId.clear();
        cmbCliente.setValue(null);
        txtMesa.clear();
        dpFecha.setValue(null);
        cmbHora.setValue(null);
        txtPersonas.clear();
        cmbEstado.setValue(EstadoReserva.PENDIENTE);
        tablaReservas.getSelectionModel().clearSelection();
        aplicarModo(Modo.NUEVO);
    }

    private void aplicarModo(Modo modo){
        boolean capturando = (modo == Modo.NUEVO);
        boolean editando = (modo == Modo.EDICION);
        boolean camposActivos = capturando || editando;

        txtId.setDisable(!capturando);
        cmbCliente.setDisable(!camposActivos);
        txtMesa.setDisable(!camposActivos);
        dpFecha.setDisable(!camposActivos);
        cmbHora.setDisable(!camposActivos);
        txtPersonas.setDisable(!camposActivos);
        cmbEstado.setDisable(!camposActivos);

        btnAgregar.setDisable(!capturando);
        btnEditar.setDisable(modo != Modo.CONSULTA);
        btnActualizar.setDisable(!editando);
        btnBorrar.setDisable(capturando);
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
        cmbCliente.setValue(r.getCliente());
        txtMesa.setText(String.valueOf(r.getMesa()));
        dpFecha.setValue(r.getFecha());
        cmbHora.setValue(r.getHora());
        txtPersonas.setText(String.valueOf(r.getNumeroPersonas()));
        cmbEstado.setValue(r.getEstado());
    }

    private boolean validar(boolean esNueva){
        String id = txtId.getText().trim();
        if (id.isEmpty()) {
            return invalido("El ID es obligatorio", txtId);
        }
        try {
            if (Integer.parseInt(id) <= 0) {
                return invalido("El ID debe ser mayor a 0", txtId);
            }
        } catch (NumberFormatException e) {
            return invalido("El ID debe ser un número entero", txtId);
        }

        if (cmbCliente.getValue() == null) {
            return invalido("Selecciona un cliente (si no hay ninguno, primero registra uno en el módulo Clientes)", cmbCliente);
        }

        String mesa = txtMesa.getText().trim();
        if (mesa.isEmpty()) {
            return invalido("La mesa es obligatoria", txtMesa);
        }
        try {
            int m = Integer.parseInt(mesa);
            if (m < 1 || m > MAX_MESAS) {
                return invalido("La mesa debe estar entre 1 y " + MAX_MESAS, txtMesa);
            }
        } catch (NumberFormatException e) {
            return invalido("La mesa debe ser un número entero", txtMesa);
        }

        if (dpFecha.getValue() == null) {
            return invalido("Selecciona la fecha de la reserva", dpFecha);
        }
        if (esNueva && dpFecha.getValue().isBefore(LocalDate.now())) {
            return invalido("La fecha no puede ser anterior a hoy", dpFecha);
        }

        if (cmbHora.getValue() == null) {
            return invalido("Selecciona la hora de la reserva", cmbHora);
        }

        String personas = txtPersonas.getText().trim();
        if (personas.isEmpty()) {
            return invalido("El número de personas es obligatorio", txtPersonas);
        }
        try {
            int p = Integer.parseInt(personas);
            if (p < 1 || p > MAX_PERSONAS) {
                return invalido("El número de personas debe estar entre 1 y " + MAX_PERSONAS, txtPersonas);
            }
        } catch (NumberFormatException e) {
            return invalido("El número de personas debe ser un número entero", txtPersonas);
        }

        if (cmbEstado.getValue() == null) {
            return invalido("Selecciona el estado de la reserva", cmbEstado);
        }

        return true;
    }

    private boolean invalido(String mensaje, Node campo){
        mostrarAlerta(mensaje);
        campo.requestFocus();
        return false;
    }

        // Vuelve a leer los clientes registrados (la pantalla se carga una sola vez)
    private void cargarClientes(){
        Cliente actual = cmbCliente.getValue();
        try {
            cmbCliente.getItems().setAll(clienteService.obtenerTodos());
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        if (actual != null && cmbCliente.getItems().contains(actual)) {
            cmbCliente.setValue(actual);
        } else if (actual != null) {
            cmbCliente.setValue(null);
        }
    }

    private String textoCliente(Cliente c){
        if (c == null) {
            return "";
        }
        return c.getNombre() + " " + c.getApellido() + " (" + c.getCurp() + ")";
    }

    private void refrescarTabla(){
        datosTabla.setAll(reservas);
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

    private boolean confirmar(String mensaje){
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        Optional<ButtonType> respuesta = alerta.showAndWait();
        return respuesta.isPresent() && respuesta.get() == ButtonType.OK;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        cmbCriterio.getItems().setAll("ID", "Cliente", "Mesa");
        cmbCriterio.setValue("ID");

        cmbCliente.setConverter(new StringConverter<Cliente>() {
            @Override
            public String toString(Cliente c) {
                return textoCliente(c);
            }

            @Override
            public Cliente fromString(String texto) {
                return null;
            }
        });
        cmbCliente.setOnShowing(e -> cargarClientes());
        cargarClientes();

        for (LocalTime h = LocalTime.of(12, 0); !h.isAfter(LocalTime.of(22, 0)); h = h.plusMinutes(30)) {
            cmbHora.getItems().add(h);
        }

        cmbEstado.getItems().setAll(EstadoReserva.values());

        tablaReservas.setEditable(false);
        colId.setCellValueFactory(new PropertyValueFactory<>("idReserva"));
        colCliente.setCellValueFactory(data -> new SimpleStringProperty(textoCliente(data.getValue().getCliente())));
        colMesa.setCellValueFactory(new PropertyValueFactory<>("mesa"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
        colPersonas.setCellValueFactory(new PropertyValueFactory<>("numeroPersonas"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        tablaReservas.setItems(datosTabla);

        tablaReservas.getSelectionModel().selectedItemProperty().addListener((obs, viejo, sel) -> {
            if (sel != null) {
                seleccionada = sel;
                cargarFormulario(sel);
                aplicarModo(Modo.CONSULTA);
            }
        });

        tablaReservas.sceneProperty().addListener((obs, viejo, nueva) -> {
            if (nueva != null) {
                cargarClientes();
            }
        });

        limpiarCampos();
        refrescarTabla();
    }
}