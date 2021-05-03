package sample;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.util.Pair;

import java.util.ArrayList;

public class Productos extends ContenidoUI {
    private TextField nombre, precio, almacen, idProducto;
    private ComboBox<String> categoria;

    Productos(Controller controller) {
        super(controller);
        nombreDeLaTabla = "producto";
        nombre = new TextField();
        precio = new TextField();
        almacen = new TextField();
        idProducto = new TextField();
        Controller.prepararTextField(nombre, "Producto", false);
        Controller.prepararTextField(precio, "Precio", true);
        Controller.prepararTextField(almacen, "Cantidad", true);
        Controller.prepararTextField(idProducto, "Producto (ID)", true);

        ArrayList<Pair<String, Integer>> pares = Main.conseguirDatos().conseguirCategorias();
        ArrayList<String> llaves = new ArrayList<>();
        for (Pair<String, Integer> par : pares) {
            llaves.add(par.getKey());
        }
        categoria = new ComboBox<>(FXCollections.observableArrayList(llaves));
        categoria.setPromptText("Categoría");
        categoria.setPrefWidth(300);

        Button agregarProductoBtn = new Button("Agregar producto");
        Button eliminarProductoBtn = new Button("Eliminar producto");
        agregarProductoBtn.setOnAction(actionEvent -> nuevoProducto());
        eliminarProductoBtn.setOnAction(actionEvent -> borrarProducto());

        Node[] nodosArray = {nombre, precio, almacen, categoria, agregarProductoBtn,
                Controller.nuevoEspacio(agregarProductoBtn), idProducto, eliminarProductoBtn};
        nodos = new ArrayList<>();
        for (Node nodo : nodosArray) { sumarAGrid(nodo, true); }
    }

    // Ligada a  agregarProducto
    public void nuevoProducto() {
        String nombre = this.nombre.getText();
        String precio = this.precio.getText();
        String cantidad = almacen.getText();
        int categoria = 0;
        for (Pair<String, Integer> par : Main.conseguirDatos().conseguirCategorias()) {
            if (par.getKey().equals(this.categoria.getValue())) { // Si la cadena seleccionada es igual a la llave...
                categoria = par.getValue(); // Estamos seleccionando la misma categoría
                break; // Quizás no sea el enfoque más efectivo, hay que estar recorriendo
            }
        }

        if (nombre.isBlank() || precio.isBlank() || cantidad.isBlank()) {
            Controller.mostrarError("Porfavor ingrese valores para todos los campos.");
            return;
        }
        try {
            Main.conseguirDatos().addProduct(nombre, Double.parseDouble(this.precio.getText()),
                    Integer.parseInt(almacen.getText()), categoria);
            Controller.mostrarInfo("El producto fue añadido.");
            controller.consultaTabla(nombreDeLaTabla);
        } catch (Exception e) {
            Controller.mostrarError("Se produjo un error al añadir el producto.\n\n" + e.getMessage());
        }
    }

    // Ligada a elimnarProducto
    public void borrarProducto() {
        ButtonType resultado = Controller.mostrarConfirmacion("¿Desea eliminar el producto?");
        if (resultado != ButtonType.OK) {
            return;
        }

        String id = idProducto.getText();
        if (id.isBlank()) {
            Controller.mostrarError("Porfavor ingrese el ID del producto que desea eliminar");
            return;
        }

        try {
            Main.conseguirDatos().deleteProduct(Integer.parseInt(id));
            Controller.mostrarInfo("El producto fue eliminado.");
            controller.consultaTabla(nombreDeLaTabla);
        } catch (Exception e) {
            Controller.mostrarError("Se produjo un error al eliminar el producto.\n\n" + e.getMessage());
        }
    }

    public void editar(ArrayList<String> propiedades, int columna) {
        int id = Integer.parseInt(propiedades.get(0));
        double precio = 0;
        int almacen = 0, idCategoria = 0;
        String nombre = propiedades.get(1);

        try {
            precio = Double.parseDouble(propiedades.get(2));
        } catch (NumberFormatException e) {
            Controller.mostrarError("Por favor ingrese un precio válido.");
            controller.refrescarTabla();
            return;
        }

        try {
            almacen = Integer.parseInt(propiedades.get(3));
        } catch (NumberFormatException e) {
            Controller.mostrarError("Por favor ingrese una cantidad válida.");
            controller.refrescarTabla();
            return;
        }

        try {
            idCategoria = Integer.parseInt(propiedades.get(4));
        } catch (NumberFormatException e) {
            Controller.mostrarError("Por favor ingrese una categoría válida");
            controller.refrescarTabla();
            return;
        }

        try {
            Main.conseguirDatos().editProduct(id, nombre, precio, almacen, idCategoria, new int[]{columna});
        } catch (Exception e) {
            Controller.mostrarError("Algo salió mal al editar el producto.\n" + e.getMessage());
            controller.refrescarTabla();
            return;
        }
    }
}
