package edu.utj.evnd.poo.restaurante.controller;

import edu.utj.evnd.poo.restaurante.model.Categoria;
import edu.utj.evnd.poo.restaurante.service.CategoriaService;
import edu.utj.evnd.poo.restaurante.util.GestorImagenes;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

/**
 * Controlador de la pantalla de categorías (CRUD conectado a MySQL).
 * El borrado es lógico: la categoría se marca como inactiva y su imagen se conserva.
 *
 * @author Gael Sebastian Garcia Valdez
 */
public class CategoriaController implements Initializable {

    private static final String SUBCARPETA_IMAGENES = "categorias";

    @FXML private TextField txtId;
    @FXML private TextField txtNombre;
    @FXML private TextField txtUrlImagen;
    @FXML private ImageView imgCategoria;
    @FXML private TableView<Categoria> tablaCategorias;

    @FXML private TableColumn<Categoria, Integer> colId;
    @FXML private TableColumn<Categoria, String> colNombre;
    @FXML private TableColumn<Categoria, String> colUrlImagen;

    private final CategoriaService service = CategoriaService.getInstance();
    private final ObservableList<Categoria> datosTabla = FXCollections.observableArrayList();

    // Imagen elegida por el usuario que todavía no se copia a la carpeta del proyecto
    private File imagenSeleccionada;

    @FXML
    private void buscar(){
        Integer id = leerId();
        if (id == null) {
            mostrarAlerta("Escribe el ID de la categoría que quieres buscar");
            return;
        }
        try {
            Categoria enc = service.buscarPorId(id);
            if (enc != null) {
                cargarFormulario(enc);
            } else {
                mostrarAlerta("No se encontró ninguna categoría con ese ID");
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

        Categoria nueva = leerFormulario();

        String rutaNueva = null;
        if (imagenSeleccionada != null) {
            rutaNueva = copiarImagenSeleccionada();
            if (rutaNueva == null) {
                return; // ya se mostró la alerta de error
            }
            nueva.setUrlImagen(rutaNueva);
        }

        try {
            String errorServicio = service.agregar(nueva);
            if (errorServicio == null) {
                refrescarTabla();
                limpiar();
                mostrarExito("Categoría agregada correctamente");
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

        Categoria modificada = leerFormulario();
        String rutaNueva = null;

        try {
            Categoria anterior = service.buscarPorId(modificada.getIdCategoria());
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
                modificada.setUrlImagen(rutaNueva);
            } else {
                modificada.setUrlImagen(rutaAnterior);
            }

            String errorServicio = service.actualizar(modificada);
            if (errorServicio == null) {
                if (rutaNueva != null && !rutaNueva.equals(rutaAnterior)) {
                    GestorImagenes.eliminar(rutaAnterior); // se reemplazó: la imagen vieja ya no la usa ningún registro
                }
                refrescarTabla();
                limpiar();
                mostrarExito("Categoría actualizada correctamente");
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
            mostrarAlerta("Selecciona una categoría de la tabla o escribe su ID");
            return;
        }

        try {
            if (service.buscarPorId(id) == null) {
                mostrarAlerta("No se encontró para borrar");
                return;
            }

            if (service.tienePlatillosActivos(id)) {
                mostrarAlerta("No se puede eliminar: hay platillos que pertenecen a esta categoría");
                return;
            }

            if (!confirmar("¿Seguro que quieres eliminar esta categoría?")) {
                return;
            }

            // Borrado lógico: la categoría se marca como inactiva y su imagen se conserva
            if (service.eliminar(id)) {
                refrescarTabla();
                limpiar();
                mostrarExito("Categoría eliminada correctamente");
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
        txtUrlImagen.clear();
        imagenSeleccionada = null;
        imgCategoria.setImage(null);
        txtId.setDisable(false); // el ID solo se escribe al registrar una nueva
        tablaCategorias.getSelectionModel().clearSelection(); // así la fila se puede volver a seleccionar
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
        File archivo = fileChooser.showOpenDialog(imgCategoria.getScene().getWindow());
        if (archivo != null) {
            imagenSeleccionada = archivo;
            imgCategoria.setImage(new Image(archivo.toURI().toString())); // solo vista previa
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

    private Categoria leerFormulario(){
        Categoria categoria = new Categoria();
        categoria.setIdCategoria(Integer.parseInt(txtId.getText().trim()));
        categoria.setNombre(txtNombre.getText().trim());
        categoria.setUrlImagen(txtUrlImagen.getText());
        return categoria;
    }

    private void cargarFormulario(Categoria c){
        imagenSeleccionada = null; // al cargar un registro no hay imagen nueva pendiente
        txtId.setText(String.valueOf(c.getIdCategoria()));
        txtId.setDisable(true); // el ID es la llave primaria: no se cambia en un registro existente
        txtNombre.setText(c.getNombre());
        txtUrlImagen.setText(c.getUrlImagen());
        imgCategoria.setImage(GestorImagenes.cargar(c.getUrlImagen())); // la imagen se muestra al seleccionar o buscar
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

        // La ruta de la imagen se llena con el botón de cargar imagen, no se escribe a mano
        txtUrlImagen.setEditable(false);

        colId.setCellValueFactory(new PropertyValueFactory<>("idCategoria"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colUrlImagen.setCellValueFactory(new PropertyValueFactory<>("urlImagen"));

        tablaCategorias.setItems(datosTabla);
        tablaCategorias.getSelectionModel().selectedItemProperty().addListener((obs, viejo, sel) -> {
            if (sel != null) cargarFormulario(sel);
        });

        refrescarTabla();
    }
}