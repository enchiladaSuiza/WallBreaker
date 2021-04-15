package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.util.Pair;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
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
    private Pedidos pedidos;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tabla = new TableView<>();
        /*productos = new Productos(this);
        ventas = new Ventas(this);
        pedidos = new Pedidos(this);*/

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
            if (productos == null) {
                productos = new Productos(this);
            }
            consulta("producto");
            limpiarYAgregarNodosAGrid(productos.conseguirNodos());
        } else if (boton.equals(ventasBtn)) {
            if (ventas == null) {
                ventas = new Ventas(this);
            }
            consulta("venta");
            limpiarYAgregarNodosAGrid(ventas.conseguirNodos());
        } else if (boton.equals(pedidosBtn)) {
            if (pedidos == null) {
                pedidos = new Pedidos(this);
            }
            consulta("pedido");
            limpiarYAgregarNodosAGrid(pedidos.conseguirNodos());
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

    public void limpiarYAgregarNodosAGrid(ArrayList<Pair<Node, Boolean>> nodos) {
        grid.getChildren().clear();
        for (Pair<Node, Boolean> nodo : nodos) {
            grid.getChildren().addAll(nodo.getKey());
        }
    }
    public void agregarNodoAGrid(Node nodo) {
        grid.getChildren().add(nodo);
    }
    public void quitarNodoDeGrid(Node nodo) {
        grid.getChildren().remove(nodo);
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

    public static void actualizarPosicionesEnGrid(ArrayList<Pair<Node, Boolean>> nodos) {
        int offset = 0;
        for (int i = 0; i < nodos.size(); i++) {
            int fila = (i + offset) / 2;
            int columna = (i + offset) % 2 == 0 ? 0 : 1;
            Pair<Node, Boolean> par = nodos.get(i);
            GridPane.setConstraints(par.getKey(), columna, fila);
            GridPane.setHalignment(par.getKey(), HPos.CENTER);
            if (par.getValue()) {
                GridPane.setColumnSpan(par.getKey(), 2);
                offset++;
            }
            else {
                GridPane.setColumnSpan(par.getKey(), 1);
            }

        }
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
