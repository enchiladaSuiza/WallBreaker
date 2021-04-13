package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private ScrollPane centro;
    @FXML
    private GridPane grid;
    @FXML
    private Label titulo;
    @FXML
    private Button productosBtn, ventasBtn, proveedoresBtn, analisisBtn, pedidosBtn, personalBtn;

    private TableView<ObservableList<StringProperty>> tabla;
    private static Alert error, informacion, confirmacion;
    private Productos productos;
    private Ventas ventas;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tabla = new TableView<>();
        productos = new Productos();
        ventas = new Ventas();

        // Ventanas
        error = new Alert(Alert.AlertType.ERROR);
        error.setTitle("Wall Breaker");
        error.setHeaderText("Error");
        informacion = new Alert(Alert.AlertType.INFORMATION);
        informacion.setTitle("Wall Breaker");
        informacion.setHeaderText("Éxito");
        confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Wall Breaker");
        confirmacion.setHeaderText("Confirmación");
    }

    // Funciones de consultas
    @FXML
    private void eventoConsultar(ActionEvent event) {
        Button boton = (Button)event.getSource();
        titulo.setText(boton.getText());
        if (boton.equals(productosBtn)) {
            consulta("producto");
            grid.getChildren().addAll(productos.conseguirNodos());
        } else if (boton.equals(ventasBtn)) {
            consulta("venta");
            grid.getChildren().addAll(ventas.conseguirNodos());
        } else if (boton.equals(pedidosBtn)) {
            consulta("pedido");
        } else if (boton.equals(personalBtn)) {
            consulta("personal");
        }
    }
    public void consulta(String nombreTabla) {
        if (centro.getContent() == null) {
            centro.setContent(tabla);
        }
        tabla.getItems().clear();
        tabla.getColumns().clear();
        try {
            ObservableList<ObservableList<String>> consulta = Main.conseguirDatos().verTodo(nombreTabla);
            ObservableList<String> nombreColumnas = consulta.remove(0);
            for (int i = 0; i < nombreColumnas.size(); i++) { // Crear y añadir columnas a la tabla
                int indice = i;
                TableColumn<ObservableList<StringProperty>, String> columna =
                        new TableColumn<>(nombreColumnas.get(indice));
                columna.setCellValueFactory(observableListStringCellDataFeatures ->
                        observableListStringCellDataFeatures.getValue().get(indice));
                tabla.getColumns().add(columna);
            }

            // Añadir los datos a la tabla
            for (ObservableList<String> fila : consulta) {
                ObservableList<StringProperty> tuplas = FXCollections.observableArrayList();
                for (String registro : fila) {
                    tuplas.add(new SimpleStringProperty(registro));
                }
                tabla.getItems().add(tuplas);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    // Funciones de ayuda
    public static void prepararTextField(TextField tf, String prompt, boolean numero) {
        tf.setPromptText(prompt);
        if (numero) {
            tf.textProperty().addListener((observableValue, s, t1) -> {
                if (!t1.matches("\\d*(\\.\\d*)?")) { // No tengo la menor idea
                    tf.setText(s);
                }
            });
        }
    }
    public static void posicionEnGrid(Node nodo, int columna, int fila, int span) {
        GridPane.setConstraints(nodo, columna, fila);
        GridPane.setColumnSpan(nodo, span);
    }
    public static Pane nuevoEspacio(Region referencia) {
        Pane espacio = new Pane();
        espacio.minHeightProperty().bind(referencia.heightProperty());
        return espacio;
    }

    // Funciones de ventanas
    public static ButtonType mostrarError(String contenido) { return mostrarVentana(error, contenido); }
    public static ButtonType mostrarInfo(String contenido) { return mostrarVentana(informacion, contenido); }
    public static ButtonType mostrarConfirmacion(String contenido) { return mostrarVentana(confirmacion, contenido); }
    private static ButtonType mostrarVentana(Alert ventana, String contenido) {
        ventana.setContentText(contenido);
        ventana.showAndWait();
        return ventana.getResult();
    }
}
