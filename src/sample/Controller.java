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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private ImageView logoView;
    @FXML
    private GridPane grid;
    @FXML
    private Label titulo;
    @FXML
    private Button productosBtn, ventasBtn, proveedoresBtn, pedidosBtn, personalBtn;
    @FXML
    private TableView<ObservableList<StringProperty>> tabla;

    private Image logoNormal, logoOscuro;
    private static Alert error, informacion, confirmacion;
    private ContenidoUI productos, ventas, pedidos, proveedores, personal;
    private ArrayList<Pair<Button, ContenidoUI>> botonesUi;
    private String tablaActual;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Tabla
        tabla.setPlaceholder(new Label());
        logoNormal = new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/Logo.png")));
        logoOscuro = new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/LogoBlanco.png")));

        // Contenidos
        productos = new Productos(this);
        ventas = new Ventas(this);
        pedidos = new Pedidos(this);
        proveedores = new Proveedores(this);
        personal = new Personal(this);
        botonesUi = new ArrayList<>();
        botonesUi.add(new Pair<>(productosBtn, productos));
        botonesUi.add(new Pair<>(ventasBtn, ventas));
        botonesUi.add(new Pair<>(proveedoresBtn, proveedores));
        botonesUi.add(new Pair<>(pedidosBtn, pedidos));
        botonesUi.add(new Pair<>(personalBtn, personal));
        grid.getChildren().clear();

        // Ventanas
        error = new Alert(Alert.AlertType.ERROR);
        informacion = new Alert(Alert.AlertType.INFORMATION);
        confirmacion = new Alert(Alert.AlertType.CONFIRMATION);

        Alert[] alertas = {error, informacion, confirmacion};
        for (Alert alerta : alertas) {
            alerta.setTitle("Wall Breaker");
            Stage temp = (Stage)alerta.getDialogPane().getScene().getWindow();
            temp.getIcons().add(Main.conseguirIcono());
        }

        error.setHeaderText("Error");
        informacion.setHeaderText("Éxito");
        confirmacion.setHeaderText("Confirmación");
    }

    // Funciones de consultas
    @FXML
    private void eventoConsultar(ActionEvent event) {
        Button boton = (Button)event.getSource();
        titulo.setText(boton.getText());
        /* if (boton.equals(proveedoresBtn)) {
            tablaActual = proveedores.conseguirNombreDeLaTabla();
            consultaProveedores();
            limpiarYAgregarNodosAGrid(proveedores.conseguirNodos());
            return;
        } */
        for (Pair<Button, ContenidoUI> botonUi : botonesUi) {
            if (boton.equals(botonUi.getKey())) {
                tablaActual = botonUi.getValue().conseguirNombreDeLaTabla();
                consultaTabla(tablaActual);
                limpiarYAgregarNodosAGrid(botonUi.getValue().conseguirNodos());
            }
        }
    }
    private void consulta(ObservableList<ObservableList<String>> consulta) {
        tabla.getItems().clear();
        tabla.getColumns().clear();
        tabla.refresh();
        tabla.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        ObservableList<String> nombreColumnas = consulta.remove(0);
        for (int i = 0; i < nombreColumnas.size(); i++) { // Crear y añadir columnas a la tabla
            int indice = i;
            TableColumn<ObservableList<StringProperty>, String> columna =
                    new TableColumn<>(nombreColumnas.get(indice));
            columna.setCellValueFactory(celda -> celda.getValue().get(indice));
            columna.setCellFactory(TextFieldTableCell.forTableColumn());
            columna.setOnEditCommit(celda -> editarCelda(celda.getTablePosition(), celda.getNewValue()));
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
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
    public void consultaTabla(String nombreTabla) {
        try {
            ObservableList<ObservableList<String>> consulta = Main.conseguirDatos().verTodo(nombreTabla);
            consulta(consulta);
        } catch (SQLException e) {
            mostrarError("No fue posible realizar la consulta. Error: " + e.getMessage());
        }
    }
    public void consultaProveedores() {
        try {
            ObservableList<ObservableList<String>> consulta = Main.conseguirDatos().proveedor();
            consulta(consulta);
        } catch (SQLException throwables) {
            mostrarError("No fue posible realizar la consulta. Error: " + throwables.getMessage());
        }
    }
    public void consultaProveedorPorProducto(int idProducto) {
        try {
            ObservableList<ObservableList<String>> consulta = Main.conseguirDatos().proveedor(idProducto);
            consulta(consulta);
        } catch (SQLException throwables) {
            mostrarError("No fue posible realizar la consulta. Error: " + throwables.getMessage());
        }
    }

    public void editarCelda(TablePosition<ObservableList<StringProperty>, String> posicion, String valor) {
        // No estoy enteramente seguro si este método es el mejor en enfoque
        ObservableList<StringProperty> fila = posicion .getTableView().getItems().get(posicion.getRow());
        int columna = posicion.getColumn();
        ArrayList<String> propiedades = new ArrayList<>();
        for (int i = 0; i < fila.size(); i++) {
            if (i == columna) {
                propiedades.add(valor);
            }
            else {
                propiedades.add(fila.get(i).getValue());
            }
        }

        if (tablaActual.equals(productos.conseguirNombreDeLaTabla())) {
            try {
                Main.conseguirDatos().editProduct(Integer.parseInt(propiedades.get(0)),
                        propiedades.get(1), Double.parseDouble(propiedades.get(2)),
                        Integer.parseInt(propiedades.get(3)), Integer.parseInt(propiedades.get(4)),
                        new int[]{columna});
            } catch (Exception e) {
                mostrarError("Algo salió mal al editar el producto.\n" + e.getMessage());
                tabla.refresh();
            }
        }
        else if (tablaActual.equals(personal.conseguirNombreDeLaTabla())) {
            try {
                Main.conseguirDatos().editPersonal(Integer.parseInt(propiedades.get(0)), propiedades.get(1),
                        propiedades.get(2), propiedades.get(3), Long.parseLong(propiedades.get(4)),
                        Double.parseDouble(propiedades.get(5)), new int[]{columna});
            } catch (Exception e) {
                mostrarError("Algo salió mal al editar al personal.\n" + e.getMessage());
                tabla.refresh();
            }
        }
    }

    // Gird stuff
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
        error.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        ventana.showAndWait();
        return ventana.getResult();
    }

    public void cambiarTema() {
        if (Main.cambiarCss()) {
            logoView.setImage(logoNormal);
        }
        else {
            logoView.setImage(logoOscuro);
        }
    }
}
