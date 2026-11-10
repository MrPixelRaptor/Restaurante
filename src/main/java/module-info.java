module edu.utj.evnd.poo.restaurante {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires java.sql;

    opens edu.utj.evnd.poo.restaurante to javafx.fxml;
    opens edu.utj.evnd.poo.restaurante.controller to javafx.fxml;
    opens edu.utj.evnd.poo.restaurante.model to javafx.base;

    exports edu.utj.evnd.poo.restaurante;
}
