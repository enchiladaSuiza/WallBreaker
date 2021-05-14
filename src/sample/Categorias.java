package sample;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.util.Pair;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Categorias extends ContenidoUI {
    private TextField nombre, descripcion;
    private Button agregar, eliminar;
    private ComboBox<String> categoriaEliminar;

    public Categorias(Controller controller) {
        super(controller);
        nombreDeLaTabla = "categoria";
        nombre = new TextField();
        descripcion = new TextField();
        Controller.prepararTextField(nombre, "Nombre", Controller.TEXTFIELD_CADENA);
        Controller.prepararTextField(descripcion, "Descripción", Controller.TEXTFIELD_CADENA);

        agregar = new Button("Agregar categoría");
        eliminar = new Button("Eliminar categoría");
        agregar.setOnAction(e -> agregarCategoria());
        eliminar.setOnAction(e -> eliminarCategoria());

        categoriaEliminar = new ComboBox<>();
        actualizarCategorias();
        categoriaEliminar.setPromptText("Categoría");
        categoriaEliminar.setPrefWidth(300);

        // Omae wa mou shindeiru
        LinkedHashMap<Node, Boolean> nodosConEspacios = new LinkedHashMap<>();
        nodosConEspacios.put(nombre, true);
        nodosConEspacios.put(descripcion, true);
        nodosConEspacios.put(agregar, true);
        nodosConEspacios.put(Controller.nuevoEspacio(agregar), true);
        nodosConEspacios.put(categoriaEliminar, true);
        nodosConEspacios.put(eliminar, true);

        nodos = new ArrayList<>();
        reemplazarNodosDeGrid(nodosConEspacios.keySet().toArray(new Node[0]),
                nodosConEspacios.values().toArray(new Boolean[0]));
    }

    public void agregarCategoria() {
        if (nombre.getText().isBlank() || descripcion.getText().isBlank()) {
            Controller.mostrarError("Por favor coloque el nombre y descripción de la nueva categoría");
            return;
        }

        try {
            Main.conseguirDatos().addCategoria(nombre.getText(), descripcion.getText());
            Controller.mostrarInfo("Categoría creada con éxito.");
            controller.consultaTabla(nombreDeLaTabla);
            controller.actualizarCategorias();
            nombre.clear();
            descripcion.clear();
        } catch (SQLException throwables) {
            Controller.mostrarError("Error creando la categoría\n\n" + throwables.getMessage());
        }
    }

    public void eliminarCategoria() {
        int categoria = 0;
        try {
            for (Pair<String, Integer> par : Main.conseguirDatos().consultarCategorias()) {
                if (par.getKey().equals(this.categoriaEliminar.getValue())) {
                    categoria = par.getValue();
                    break;
                }
            }
            if (categoria == 0) {
                Controller.mostrarError("Por favor elija una categoría para el producto.");
                return;
            }
        } catch (SQLException t) {
            Controller.mostrarError("Surgió un error consultado las cateogrías.\n\n" + t.getMessage());
            return;
        }

        try {
            Main.conseguirDatos().deleteCategoria(categoria);
            Controller.mostrarInfo("Categoría eliminada con éxito");
            controller.consultaTabla(nombreDeLaTabla);
            controller.actualizarCategorias();
        } catch (SQLException throwables) {
            Controller.mostrarError("Error eliminando la categoría.\n\n" + throwables.getMessage());
        }
    }

    public void actualizarCategorias() {
        categoriaEliminar.getItems().clear();
        ArrayList<String> llaves = new ArrayList<>();
        try {
            ArrayList<Pair<String, Integer>> pares = Main.conseguirDatos().consultarCategorias();
            for (Pair<String, Integer> par : pares) {
                llaves.add(par.getKey());
            }
        } catch (SQLException t) {
            Controller.mostrarError("Surgió un error consultando las categorías.\n\n" + t.getMessage());
        }
        categoriaEliminar.setItems(FXCollections.observableArrayList(llaves));
    }

    public void editar(ArrayList<String> propiedades, int columna) {
        if (columna == 3) {
            Controller.mostrarError("No es posible modificar esta columna.");
            controller.refrescarTabla();
            return;
        }

        int id = Integer.parseInt(propiedades.get(0));
        try {
            Main.conseguirDatos().editCategoria(id, propiedades.get(1), propiedades.get(2));
            controller.actualizarCategorias();
        } catch (SQLException throwables) {
            Controller.mostrarError("No fue posible editar la categoría.\n\n" + throwables.getMessage());
            controller.refrescarTabla();
        }
    }
}
