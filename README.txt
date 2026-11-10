Restaurante - Modulo Administrador (POO)
Autor: Gael Sebastian Garcia Valdez

Proyecto de escritorio en JavaFX + Maven. Implementa el CRUD de Administrador
del sistema de restaurante, aplicando herencia, encapsulamiento y sobreescritura
de métodos estándar sobre el dominio de clases modelo.

Estructura del paquete edu.utj.evnd.poo.restaurante:
- model       -> Persona (abstracta) y Administrador (final)
- controller  -> AdministradorController, conecta la interfaz con el service
- service     -> reglas de negocio (ej. evitar CURP duplicado)
- repository  -> acceso a datos (lista en memoria)
- enums       -> EstadoAdministrador

Arquitectura por capas:
Interfaz (FXML) -> Controller -> Service -> Repository -> Model

Notas del dominio:
- Persona es abstracta: no existe una persona generica en el sistema.
- Administrador es final: entidad concreta, no pensada para ser extendida.
- equals/hashCode se basan estrictamente en el id.
- toString en Administrador concatena super.toString() con sus propios datos.

El análisis completo (abstraccion, encapsulamiento, herencia, cohesion y
acoplamiento) esta en reporte_tecnico.pdf

Para ejecutar el proyecto (requiere JDK 21 y Maven):
mvn clean javafx:run
