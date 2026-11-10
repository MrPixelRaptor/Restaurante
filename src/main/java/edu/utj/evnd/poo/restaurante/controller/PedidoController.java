package edu.utj.evnd.poo.restaurante.controller;

import edu.utj.evnd.poo.restaurante.enums.EstadoPedido;
import edu.utj.evnd.poo.restaurante.enums.EstadoPlatillo;
import edu.utj.evnd.poo.restaurante.model.DetallePedido;
import edu.utj.evnd.poo.restaurante.model.Pedido;
import edu.utj.evnd.poo.restaurante.model.Platillo;
import edu.utj.evnd.poo.restaurante.service.PedidoService;
import edu.utj.evnd.poo.restaurante.service.PlatilloService;
import java.net.URL;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

/**
 * Controlador de la pantalla de gestión de pedidos (CRUD) con sus detalles.
 *
 * @author Gael Sebastian Garcia Valdez
 */
public class PedidoController implements Initializable {

    @FXML private TextField txtId;
    @FXML private DatePicker dpFecha;
    @FXML private ComboBox<EstadoPedido> cmbEstado;

    @FXML private ComboBox<Platillo> cmbPlatillo;
    @FXML private TextField txtCantidad;

    @FXML private TableView<DetallePedido> tablaDetalles;
    @FXML private TableColumn<DetallePedido, String> colDetPlatillo;
    @FXML private TableColumn<DetallePedido, Integer> colDetCantidad;
    @FXML private TableColumn<DetallePedido, Double> colDetPrecio;
    @FXML private TableColumn<DetallePedido, Double> colDetDescuento;
    @FXML private TableColumn<DetallePedido, Double> colDetSubtotal;

    @FXML private Label lblSubtotal;
    @FXML private Label lblDescuento;
    @FXML private Label lblIva;
    @FXML private Label lblTotal;

    @FXML private TableView<Pedido> tablaPedidos;
    @FXML private TableColumn<Pedido, Integer> colId;
    @FXML private TableColumn<Pedido, LocalDate> colFecha;
    @FXML private TableColumn<Pedido, String> colPlatillos;
    @FXML private TableColumn<Pedido, Double> colDescuento;
    @FXML private TableColumn<Pedido, Double> colIva;
    @FXML private TableColumn<Pedido, Double> colTotal;
    @FXML private TableColumn<Pedido, EstadoPedido> colEstado;

    private final PedidoService service = new PedidoService();
    private final PlatilloService platilloService = PlatilloService.getInstance(); // mismos platillos que el módulo Platillos
    private final ObservableList<Pedido> datosPedidos = FXCollections.observableArrayList();
    private final ObservableList<DetallePedido> datosDetalles = FXCollections.observableArrayList();

    // Pedido que se está armando en pantalla (todavía no se guarda hasta pulsar Agregar o Actualizar)
    private Pedido actual = new Pedido();

    @FXML
    private void buscar(){
        Integer id = leerId();
        if (id == null) {
            mostrarAlerta("Escribe el ID del pedido que quieres buscar");
            return;
        }
        Pedido enc = service.buscarPorId(id);
        if (enc != null) {
            cargarPedido(enc);
        } else {
            mostrarAlerta("No se encontró ningún pedido con ese ID");
        }
    }

    @FXML
    private void agregar(){
        String error = validar();
        if (error != null) {
            mostrarAlerta(error);
            return;
        }

        leerFormularioEnPedido();

        if (service.agregar(actual)) {
            refrescarTabla();
            limpiar();
            mostrarExito("Pedido agregado correctamente");
        } else {
            mostrarAlerta("Ya existe un pedido con ese ID");
        }
    }

    @FXML
    private void actualizar(){
        String error = validar();
        if (error != null) {
            mostrarAlerta(error);
            return;
        }

        leerFormularioEnPedido();

        if (service.actualizar(actual, actual.getIdPedido())) {
            refrescarTabla();
            limpiar();
            mostrarExito("Pedido actualizado correctamente");
        } else {
            mostrarAlerta("No se encontró para actualizar");
        }
    }

    @FXML
    private void borrar(){
        Integer id = leerId();
        if (id == null) {
            mostrarAlerta("Selecciona un pedido de la tabla o escribe su ID");
            return;
        }

        if (!confirmar("¿Seguro que quieres eliminar este pedido?")) {
            return;
        }

        if (service.eliminar(id)) {
            refrescarTabla();
            limpiar();
            mostrarExito("Pedido eliminado correctamente");
        } else {
            mostrarAlerta("No se encontró para borrar");
        }
    }

    @FXML
    private void limpiar(){
        actual = new Pedido(); // se descarta el pedido en edición
        txtId.clear();
        dpFecha.setValue(LocalDate.now());
        cmbEstado.setValue(EstadoPedido.PENDIENTE);
        cmbPlatillo.setValue(null);
        txtCantidad.clear();
        refrescarDetalles();
        tablaPedidos.getSelectionModel().clearSelection(); // así la fila se puede volver a seleccionar
    }

    @FXML
    private void cancelar(){
        limpiar();
    }

    @FXML
    private void agregarPlatillo(){
        Platillo platillo = cmbPlatillo.getValue();
        if (platillo == null) {
            mostrarAlerta("Selecciona un platillo");
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(txtCantidad.getText().trim());
        } catch (NumberFormatException e) {
            mostrarAlerta("La cantidad debe ser un número entero");
            return;
        }

        String error = service.agregarPlatillo(actual, platillo.getIdPlatillo(), cantidad);
        if (error != null) {
            mostrarAlerta(error);
            return;
        }

        cmbPlatillo.setValue(null);
        txtCantidad.clear();
        refrescarDetalles();
    }

    @FXML
    private void quitarPlatillo(){
        DetallePedido seleccionado = tablaDetalles.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Selecciona de la tabla de platillos el que quieres quitar");
            return;
        }
        service.quitarDetalle(actual, seleccionado.getIdDetalle());
        refrescarDetalles();
    }

    // Devuelve el ID escrito en el formulario, o null si está vacío o no es un número
    private Integer leerId(){
        try {
            return Integer.parseInt(txtId.getText().trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // Pasa al pedido en edición los datos que se escriben en el formulario
    private void leerFormularioEnPedido(){
        actual.setIdPedido(Integer.parseInt(txtId.getText().trim()));
        actual.setFecha(dpFecha.getValue());
        actual.setEstado(cmbEstado.getValue());
    }

    // Carga un pedido guardado en el formulario. Se trabaja con una copia para no
    // modificar el pedido guardado hasta que se pulse Actualizar.
    private void cargarPedido(Pedido p){
        actual = copiarPedido(p);
        txtId.setText(String.valueOf(p.getIdPedido()));
        dpFecha.setValue(p.getFecha());
        cmbEstado.setValue(p.getEstado());
        cmbPlatillo.setValue(null);
        txtCantidad.clear();
        refrescarDetalles();
    }

    private Pedido copiarPedido(Pedido original){
        Pedido copia = new Pedido(original.getIdPedido(), original.getFecha(), original.getIva(),
                original.getDescuento(), original.getTotal(), original.getEstado());
        for (DetallePedido d : original.getDetalles()) {
            copia.agregarDetalle(new DetallePedido(d.getIdDetalle(), d.getIdPedido(), d.getIdPlatillo(),
                    d.getCantidad(), d.getPrecio(), d.getDescuento()));
        }
        return copia;
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

        if (dpFecha.getValue() == null) {
            return "Selecciona la fecha del pedido";
        }
        if (dpFecha.getValue().isAfter(LocalDate.now())) {
            return "La fecha del pedido no puede ser futura";
        }

        if (cmbEstado.getValue() == null) {
            return "Selecciona el estado del pedido";
        }

        if (actual.getDetalles().isEmpty()) {
            return "Agrega al menos un platillo al pedido";
        }

        return null; // todo está bien
    }

    // Actualiza la tabla de platillos del pedido en edición y sus totales en pantalla
    private void refrescarDetalles(){
        datosDetalles.setAll(actual.getDetalles());
        tablaDetalles.refresh(); // por si cambió el nombre de algún platillo
        lblSubtotal.setText(pesos(actual.getSubtotal()));
        lblDescuento.setText(pesos(actual.getDescuento()));
        lblIva.setText(pesos(actual.getIva()));
        lblTotal.setText(pesos(actual.getTotal()));
    }

    private void refrescarTabla(){
        datosPedidos.setAll(service.obtenerTodos());
    }

    // Solo se ofrecen los platillos DISPONIBLES, conservando la selección si sigue disponible
    private void recargarPlatillos(){
        Platillo seleccionado = cmbPlatillo.getValue();
        try {
            cmbPlatillo.getItems().setAll(platilloService.obtenerTodos().stream()
                    .filter(p -> p.getEstado() == EstadoPlatillo.DISPONIBLE)
                    .collect(Collectors.toList()));
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        if (seleccionado != null && cmbPlatillo.getItems().contains(seleccionado)) {
            cmbPlatillo.setValue(seleccionado);
        } else {
            cmbPlatillo.setValue(null);
        }
    }

    private String pesos(double valor){
        return String.format(Locale.US, "$%,.2f", valor);
    }

    private String nombrePlatillo(int idPlatillo){
        try {
            Platillo p = platilloService.buscarPorId(idPlatillo);
            return (p != null) ? p.getNombre() : "(platillo eliminado)";
        } catch (java.sql.SQLException e) {
            return "(sin conexión)";
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

    private boolean confirmar(String mensaje){
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        Optional<ButtonType> respuesta = alerta.showAndWait();
        return respuesta.isPresent() && respuesta.get() == ButtonType.OK;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        cmbEstado.getItems().setAll(EstadoPedido.values());
        cmbEstado.setValue(EstadoPedido.PENDIENTE);
        dpFecha.setValue(LocalDate.now());

        // El ComboBox de platillos muestra "id - nombre ($precio)" y se recarga al desplegarse
        cmbPlatillo.setConverter(new StringConverter<Platillo>() {
            @Override
            public String toString(Platillo p) {
                return (p == null) ? "" : p.getIdPlatillo() + " - " + p.getNombre() + " ($" + p.getPrecio() + ")";
            }

            @Override
            public Platillo fromString(String texto) {
                return null; // el ComboBox no es editable
            }
        });
        cmbPlatillo.setOnShowing(e -> recargarPlatillos());
        recargarPlatillos();

        // Tabla de platillos del pedido en edición
        colDetPlatillo.setCellValueFactory(datos -> new SimpleStringProperty(nombrePlatillo(datos.getValue().getIdPlatillo())));
        colDetCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colDetPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colDetDescuento.setCellValueFactory(new PropertyValueFactory<>("descuento"));
        colDetSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        tablaDetalles.setItems(datosDetalles);

        // Tabla de pedidos guardados
        colId.setCellValueFactory(new PropertyValueFactory<>("idPedido"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colPlatillos.setCellValueFactory(datos -> new SimpleStringProperty(String.valueOf(datos.getValue().getDetalles().size())));
        colDescuento.setCellValueFactory(new PropertyValueFactory<>("descuento"));
        colIva.setCellValueFactory(new PropertyValueFactory<>("iva"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        tablaPedidos.setItems(datosPedidos);
        tablaPedidos.getSelectionModel().selectedItemProperty().addListener((obs, viejo, sel) -> {
            if (sel != null) cargarPedido(sel);
        });

        // Cada vez que la pantalla se muestra, se actualizan los platillos disponibles y los nombres
        tablaPedidos.sceneProperty().addListener((obs, viejo, nueva) -> {
            if (nueva != null) {
                recargarPlatillos();
                refrescarDetalles();
            }
        });

        refrescarDetalles();
        refrescarTabla();
    }
}