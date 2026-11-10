package edu.utj.evnd.poo.restaurante.controller;

import edu.utj.evnd.poo.restaurante.enums.EstadoPlatillo;
import edu.utj.evnd.poo.restaurante.model.Categoria;
import edu.utj.evnd.poo.restaurante.model.Platillo;
import edu.utj.evnd.poo.restaurante.service.CategoriaService;
import edu.utj.evnd.poo.restaurante.service.PlatilloService;
import edu.utj.evnd.poo.restaurante.util.GestorImagenes;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

/**
 * Controlador de la pantalla de platillos (CRUD conectado a MySQL).
 * El borrado es lógico: el platillo se marca como inactivo y su imagen se conserva.
 *
 * @author Gael Sebastian Garcia Valdez
 */
public class PlatilloController implements Initializable {

    private static final String SUBCARPETA_IMAGENES = "platillos";

    @FXML private TextField txtId;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<Categoria> cmbCategoria;
    @FXML private TextField txtDescripcion;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtExistencia;
    @FXML private TextField txtDescuento;
    @FXML private ComboBox<EstadoPlatillo> cmbEstado;
    @FXML private TextField txtUrlImagen;
    @FXML private ImageView imgPlatillo;
    @FXML private TableView<Platillo> tablaPlatillos;

    @FXML private TableColumn<Platillo, Integer> colId;
    @FXML private TableColumn<Platillo, String> colNombre;
    @FXML private TableColumn<Platillo, String> colDescripcion;
    @FXML private TableColumn<Platillo, String> colCategoria;
    @FXML private TableColumn<Platillo, Double> colPrecio;
    @FXML private TableColumn<Platillo, Integer> colExistencia;
    @FXML private TableColumn<Platillo, Double> colDescuento;
    @FXML private TableColumn<Platillo, EstadoPlatillo> colEstado;
    @FXML private TableColumn<Platillo, String> colUrlImagen;

    private final PlatilloService service = PlatilloService.getInstance();
    private final CategoriaService categoriaService = CategoriaService.getInstance();
    private final ObservableList<Platillo> datosTabla = FXCollections.observableArrayList();

    // Imagen elegida por el usuario que todavía no se copia a la carpeta del proyecto
    private File imagenSeleccionada;

    @FXML
    private void buscar(){
        Integer id = leerId();
        if (id == null) {
            mostrarAlerta("Escribe el ID del platillo que quieres buscar");
            return;
        }
        try {
            Platillo enc = service.buscarPorId(id);
            if (enc != null) {
                cargarFormulario(enc);
            } else {
                mostrarAlerta("No se encontró ningún platillo con ese ID");
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

        Platillo nuevo = leerFormulario();

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
                mostrarExito("Platillo agregado correctamente");
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

        Platillo modificado = leerFormulario();
        String rutaNueva = null;

        try {
            Platillo anterior = service.buscarPorId(modificado.getIdPlatillo());
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
                mostrarExito("Platillo actualizado correctamente");
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
        Integer id = leerId();
        if (id == null) {
            mostrarAlerta("Selecciona un platillo de la tabla o escribe su ID");
            return;
        }

        try {
            if (service.buscarPorId(id) == null) {
                mostrarAlerta("No se encontró para borrar");
                return;
            }

            if (!confirmar("¿Seguro que quieres eliminar este platillo?")) {
                return;
            }

            // Borrado lógico: el platillo se marca como inactivo y su imagen se conserva
            if (service.eliminar(id)) {
                refrescarTabla();
                limpiar();
                mostrarExito("Platillo eliminado correctamente");
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
        txtNombre.clear();
        cmbCategoria.getSelectionModel().clearSelection();
        txtDescripcion.clear();
        txtPrecio.clear();
        txtExistencia.clear();
        txtDescuento.clear();
        cmbEstado.getSelectionModel().clearSelection();
        txtUrlImagen.clear();
        imagenSeleccionada = null;
        imgPlatillo.setImage(null);
        txtId.setDisable(false); // el ID solo se escribe al registrar uno nuevo
        tablaPlatillos.getSelectionModel().clearSelection(); // así la fila se puede volver a seleccionar
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
        File archivo = fileChooser.showOpenDialog(imgPlatillo.getScene().getWindow());
        if (archivo != null) {
            imagenSeleccionada = archivo;
            imgPlatillo.setImage(new Image(archivo.toURI().toString())); // solo vista previa
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

    // Devuelve el ID escrito en el formulario, o null si está vacío o no es un número
    private Integer leerId(){
        try {
            return Integer.parseInt(txtId.getText().trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // Un número decimal válido: solo dígitos y, opcionalmente, un punto con decimales
    private boolean esDecimal(String texto){
        return texto.trim().matches("\\d+(\\.\\d+)?");
    }

    private Platillo leerFormulario(){
        Platillo platillo = new Platillo();
        platillo.setIdPlatillo(Integer.parseInt(txtId.getText().trim()));
        platillo.setNombre(txtNombre.getText().trim());
        platillo.setCategoria(cmbCategoria.getValue());
        platillo.setDescripcion(txtDescripcion.getText().trim());
        platillo.setPrecio(Double.parseDouble(txtPrecio.getText().trim()));
        platillo.setExistencia(Integer.parseInt(txtExistencia.getText().trim()));
        platillo.setDescuento(Double.parseDouble(txtDescuento.getText().trim()));
        platillo.setEstado(cmbEstado.getValue());
        platillo.setUrlImagen(txtUrlImagen.getText());
        return platillo;
    }

    private void cargarFormulario(Platillo p){
        imagenSeleccionada = null; // al cargar un registro no hay imagen nueva pendiente
        txtId.setText(String.valueOf(p.getIdPlatillo()));
        txtId.setDisable(true); // el ID es la llave primaria: no se cambia en un registro existente
        txtNombre.setText(p.getNombre());
        seleccionarCategoria(p.getCategoria());
        txtDescripcion.setText(p.getDescripcion());
        txtPrecio.setText(String.valueOf(p.getPrecio()));
        txtExistencia.setText(String.valueOf(p.getExistencia()));
        txtDescuento.setText(String.valueOf(p.getDescuento()));
        cmbEstado.setValue(p.getEstado());
        txtUrlImagen.setText(p.getUrlImagen());
        imgPlatillo.setImage(GestorImagenes.cargar(p.getUrlImagen())); // la imagen se muestra al seleccionar o buscar
    }

    // Selecciona en el combo la categoría con el mismo ID (el platillo trae su propia copia de la categoría)
    private void seleccionarCategoria(Categoria categoria){
        for (Categoria c : cmbCategoria.getItems()) {
            if (c.getIdCategoria() == categoria.getIdCategoria()) {
                cmbCategoria.setValue(c);
                return;
            }
        }
        cmbCategoria.setValue(categoria);
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

        if (txtNombre.getText().trim().isEmpty()) {
            return "El nombre es obligatorio";
        }

        if (cmbCategoria.getValue() == null) {
            return "Selecciona una categoría (si no hay ninguna, primero registra una en el módulo Categorías)";
        }

        if (txtDescripcion.getText().trim().isEmpty()) {
            return "La descripción es obligatoria";
        }

        if (!esDecimal(txtPrecio.getText()) || Double.parseDouble(txtPrecio.getText().trim()) <= 0) {
            return "El precio debe ser un número mayor a 0 (ejemplo: 85.50)";
        }

        int existencia;
        try {
            existencia = Integer.parseInt(txtExistencia.getText().trim());
            if (existencia < 0) {
                return "La existencia no puede ser negativa";
            }
        } catch (NumberFormatException e) {
            return "La existencia debe ser un número entero";
        }

        if (!esDecimal(txtDescuento.getText()) || Double.parseDouble(txtDescuento.getText().trim()) > 100) {
            return "El descuento debe ser un número entre 0 y 100";
        }

        EstadoPlatillo estado = cmbEstado.getValue();
        if (estado == null) {
            return "Selecciona un estado";
        }

        // Reglas del modelo: el estado debe corresponder con la existencia
        if (estado == EstadoPlatillo.DISPONIBLE && existencia == 0) {
            return "Un platillo DISPONIBLE debe tener existencia mayor a 0";
        }
        if (estado == EstadoPlatillo.AGOTADO && existencia > 0) {
            return "Un platillo AGOTADO debe tener existencia 0";
        }

        return null; // todo está bien
    }

    private void refrescarTabla(){
        try {
            datosTabla.setAll(service.obtenerTodos());
            tablaPlatillos.refresh(); // por si cambió el nombre de alguna categoría
        } catch (SQLException e) {
            mostrarErrorBD(e);
        }
    }

    // Vuelve a cargar las categorías activas, conservando la selección actual
    private void recargarCategorias(){
        Categoria seleccionada = cmbCategoria.getValue();
        try {
            cmbCategoria.getItems().setAll(categoriaService.obtenerTodos());
        } catch (SQLException e) {
            mostrarErrorBD(e);
        }
        cmbCategoria.setValue(seleccionada);
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

        cmbEstado.getItems().setAll(EstadoPlatillo.values());

        // La ruta de la imagen se llena con el botón de cargar imagen, no se escribe a mano
        txtUrlImagen.setEditable(false);

        // El ComboBox de categorías muestra "id - nombre"
        cmbCategoria.setConverter(new StringConverter<Categoria>() {
            @Override
            public String toString(Categoria c) {
                return (c == null) ? "" : c.getIdCategoria() + " - " + c.getNombre();
            }

            @Override
            public Categoria fromString(String texto) {
                return null; // el ComboBox no es editable
            }
        });
        recargarCategorias();

        colId.setCellValueFactory(new PropertyValueFactory<>("idPlatillo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colCategoria.setCellValueFactory(datos -> new SimpleStringProperty(
            datos.getValue().getCategoria() != null ? datos.getValue().getCategoria().getNombre() : ""));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colExistencia.setCellValueFactory(new PropertyValueFactory<>("existencia"));
        colDescuento.setCellValueFactory(new PropertyValueFactory<>("descuento"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colUrlImagen.setCellValueFactory(new PropertyValueFactory<>("urlImagen"));

        tablaPlatillos.setItems(datosTabla);
        tablaPlatillos.getSelectionModel().selectedItemProperty().addListener((obs, viejo, sel) -> {
            if (sel != null) cargarFormulario(sel);
        });

        // Cada vez que la pantalla se muestra, se actualizan categorías y tabla
        tablaPlatillos.sceneProperty().addListener((obs, viejo, nueva) -> {
            if (nueva != null) {
                recargarCategorias();
                refrescarTabla();
            }
        });

        refrescarTabla();
    }
}