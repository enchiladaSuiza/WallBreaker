package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private AnchorPane centro, arriba;
    @FXML
    private ScrollPane izquierda;
    @FXML
    private Button productosBtn, ventasBtn, proveedoresBtn, analisisBtn, pedidosBtn, personalBtn;
    @FXML
    private Label titulo;
    @FXML
    private TextArea select;

    private Datos datos;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        datos = Main.conseguirDatos(); // Puede no ser el mejor acercamiento
    }

    public void pantallaProductos() {
        titulo.setText("Productos");
        ArrayList<String> tuplas;
        try {
            tuplas = datos.verTodo("producto");
        } catch (SQLException throwables) {
            select.setText("Error!");
            return;
        }
        StringBuilder consulta = new StringBuilder();
        for (String tupla : tuplas) {
            consulta.append(tupla).append("\n");
        }
        select.setText(consulta.toString());
    }
}
