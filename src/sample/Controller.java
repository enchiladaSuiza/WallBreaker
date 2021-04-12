package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private ScrollPane centro;
    @FXML
    private VBox vBox;
    @FXML
    private Label titulo;

    private Datos datos;
    private TableView<ObservableList<StringProperty>> tabla;
    private Alert error, informacion, confirmacion;

    private TextField textoProducto, precioProducto, almacenProducto, categoriaProducto, idProducto;
    private Button agregarProducto, eliminarProducto;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        datos = Main.conseguirDatos(); // Puede no ser el mejor acercamiento
        tabla = new TableView<>();

        error = new Alert(Alert.AlertType.ERROR);
        error.setTitle("Wall Breaker");
        error.setHeaderText("Error");

        informacion = new Alert(Alert.AlertType.INFORMATION);
        informacion.setTitle("Wall Breaker");
        informacion.setHeaderText("Éxito");

        confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Wall Breaker");
        confirmacion.setHeaderText("Confirmación");

        textoProducto = new TextField();
        prepararTextField(textoProducto, "Producto", false);
        precioProducto = new TextField();
        prepararTextField(precioProducto, "Precio", true);
        almacenProducto = new TextField();
        prepararTextField(almacenProducto, "Cantidad", true);
        categoriaProducto = new TextField();
        prepararTextField(categoriaProducto, "Categoria (ID)", true);
        agregarProducto = new Button("Agregar producto");
        VBox.setMargin(agregarProducto, new Insets(0, 0, 10, 0));
        agregarProducto.setOnAction(actionEvent -> nuevoProducto());

        idProducto = new TextField();
        prepararTextField(idProducto, "Producto (ID)", true);
        eliminarProducto = new Button("Eliminar producto");
        eliminarProducto.setOnAction(actionEvent -> borrarProducto());
    }

    public void mostrarConsulta(@Nullable Button boton, String nombreTabla) {
        if (boton != null) {
            titulo.setText(boton.getText());
        }
        if (centro.getContent() == null) {
            centro.setContent(tabla);
        }
        tabla.getItems().clear();
        tabla.getColumns().clear();
        try {
            ObservableList<ObservableList<String>> consulta = datos.verTodo(nombreTabla);
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

    public void mostrarProductos(ActionEvent event) {
        mostrarConsulta(((Button)event.getSource()), "producto");
        vBox.getChildren().clear();
        vBox.getChildren().addAll(textoProducto, precioProducto, almacenProducto, categoriaProducto,
                agregarProducto, idProducto, eliminarProducto);

    }
    public void mostrarVentas(ActionEvent event) { mostrarConsulta((Button)event.getSource(), "venta"); }
    public void mostrarProveedores(ActionEvent event) { mostrarConsulta((Button)event.getSource(), "proveedor"); }
    public void mostrarPedidos(ActionEvent event) { mostrarConsulta((Button)event.getSource(), "pedido"); }
    public void mostrarPersonal(ActionEvent event) { mostrarConsulta((Button)event.getSource(), "personal"); }

    public void prepararTextField(TextField tf, String prompt, boolean numero) {
        tf.setPromptText(prompt);
        if (numero) {
            tf.textProperty().addListener((observableValue, s, t1) -> {
                if (!t1.matches("\\d*(\\.\\d*)?")) { // No tengo la menor idea
                    tf.setText(s);
                }
            });
        }
    }

    public void nuevoProducto() {
        String nombre = textoProducto.getText();
        String precio = precioProducto.getText();
        String cantidad = almacenProducto.getText();
        String categoria = categoriaProducto.getText();

        if (nombre.isBlank() || precio.isBlank() || cantidad.isBlank() || categoria.isBlank()) {
            error.setContentText("Porfavor ingrese valores para todos los campos.");
            error.showAndWait();
            return;
        }

        try {
            datos.addProduct(nombre, Double.parseDouble(precioProducto.getText()),
                    Integer.parseInt(almacenProducto.getText()), Integer.parseInt(categoriaProducto.getText()));
            informacion.setContentText("El producto fue añadido.");
            informacion.showAndWait();
            mostrarConsulta(null, "producto");
        } catch (Exception e) {
            error.setContentText("Se produjo un error al añadir el producto. El mensaje de error es: " + e.getMessage());
            error.showAndWait();
        }
    }
    public void borrarProducto() {
        confirmacion.setContentText("¿Desea eliminar el producto?");
        confirmacion.showAndWait();
        if (confirmacion.getResult() != ButtonType.OK) {
            return;
        }

        String id = idProducto.getText();
        if (id.isBlank()) {
            error.setContentText("Porfavor ingrese el ID del producto que desea eliminar");
            error.showAndWait();
            return;
        }

        try {
            datos.deleteProduct(Integer.parseInt(id));
            informacion.setContentText("El producto fue eliminado.");
            informacion.showAndWait();
            mostrarConsulta(null, "producto");
        } catch (Exception e) {
            error.setContentText("Se produjo un error al eliminar el producto. El mensaje de error es: " + e.getMessage());
            error.showAndWait();
        }
    }
}
