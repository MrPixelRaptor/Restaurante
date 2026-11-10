package edu.utj.evnd.poo.restaurante.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import javafx.scene.image.Image;

public class GestorImagenes {

    private static final Path RAIZ = Paths.get("imagenes");

    // Copia la imagen a imagenes/<subcarpeta>/ y devuelve la ruta relativa
    public static String guardar(File origen, String subcarpeta) throws IOException {
        Path carpeta = RAIZ.resolve(subcarpeta);
        Files.createDirectories(carpeta);

        String nombre = origen.getName();
        int punto = nombre.lastIndexOf('.');
        String extension = punto >= 0 ? nombre.substring(punto) : "";
        String nombreNuevo = UUID.randomUUID() + extension;

        Path destino = carpeta.resolve(nombreNuevo);
        Files.copy(origen.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);

        return destino.toString().replace('\\', '/');
    }

    // Carga una imagen a partir de la ruta relativa guardada en el modelo
    public static Image cargar(String rutaRelativa) {
        if (rutaRelativa == null || rutaRelativa.isBlank()) {
            return null;
        }
        File archivo = new File(rutaRelativa);
        if (!archivo.exists()) {
            return null;
        }
        return new Image(archivo.toURI().toString());
    }

    // Borra la imagen anterior cuando se reemplaza o se elimina el registro
    public static void eliminar(String rutaRelativa) {
        if (rutaRelativa == null || rutaRelativa.isBlank()) {
            return;
        }
        try {
            Files.deleteIfExists(Paths.get(rutaRelativa));
        } catch (IOException e) {
            System.err.println("No se pudo borrar la imagen: " + e.getMessage());
        }
    }
}