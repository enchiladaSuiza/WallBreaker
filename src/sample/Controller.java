package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private ScrollPane centro;
    // @FXML
    // private Button productosBtn, ventasBtn, proveedoresBtn, analisisBtn, pedidosBtn, personalBtn;
    @FXML
    private Label titulo;

    private Datos datos;
    private TableView<ObservableList<StringProperty>> tabla;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        datos = Main.conseguirDatos(); // Puede no ser el mejor acercamiento
        tabla = new TableView<>();
    }

    public void mostrarConsulta(ActionEvent event, String nombreTabla) {
        titulo.setText(((Button)event.getSource()).getText());
        if (centro.getContent() == null) {
            centro.setContent(tabla);
        }
        tabla.getItems().clear();
        tabla.getColumns().clear();
        try {
            ObservableList<ObservableList<String>> consulta = datos.verTodo(nombreTabla);
            ObservableList<String> nombreColumnas = consulta.remove(0);;
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

    public void mostrarProductos(ActionEvent event) { mostrarConsulta(event, "producto"); }
    public void mostrarVentas(ActionEvent event) { mostrarConsulta(event, "venta"); }
    public void mostrarProveedores(ActionEvent event) { mostrarConsulta(event, "proveedor"); }
    public void mostrarPedidos(ActionEvent event) { mostrarConsulta(event, "pedido"); }
    public void mostrarPersonal(ActionEvent event) { mostrarConsulta(event, "personal"); }
}
