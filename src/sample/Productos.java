package sample;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.util.Pair;

import java.sql.SQLException;
import java.util.ArrayList;

public class Productos extends ContenidoUI {
    private final TextField nombre, precio, almacen, idProducto;
    private final ComboBox<String> categoria;

    Productos(Controller controller) {
        super(controller);
        nombreDeLaTabla = "producto";
        nombre = new TextField();
        precio = new TextField();
        almacen = new TextField();
        idProducto = new TextField();
        Controller.prepararTextField(nombre, "Producto", Controller.TEXTFIELD_CADENA);
        Controller.prepararTextField(precio, "Precio", Controller.TEXTFILED_FLOTANTE);
        Controller.prepararTextField(almacen, "Cantidad", Controller.TEXTFIELD_ENTERO);
        Controller.prepararTextField(idProducto, "Producto", Controller.TEXTFIELD_CADENA);

        ArrayList<String> llaves = new ArrayList<>();
        try {
            ArrayList<Pair<String, Integer>> pares = Main.conseguirDatos().consultarCategorias();
            for (Pair<String, Integer> par : pares) {
                llaves.add(par.getKey());
            }
        } catch (SQLException t) {
            Controller.mostrarError("Surgió un error consultado las cateogrías.\n\n" + t.getMessage());
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
        try {
            for (Pair<String, Integer> par : Main.conseguirDatos().consultarCategorias()) {
                if (par.getKey().equals(this.categoria.getValue())) { // Si la cadena seleccionada es igual a la llave...
                    categoria = par.getValue(); // Estamos seleccionando la misma categoría
                    break; // Quizás no sea el enfoque más efectivo, hay que estar recorriendo
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

        this.nombre.clear();
        this.precio.clear();
        this.almacen.clear();
    }

    // Ligada a elimnarProducto
    public void borrarProducto() {
        ButtonType resultado = Controller.mostrarConfirmacion("¿Desea eliminar el producto?");
        if (resultado != ButtonType.OK) {
            return;
        }

        String idString = idProducto.getText();
        if (idString.isBlank()) {
            Controller.mostrarError("Porfavor ingrese el ID o nombre del producto que desea eliminar");
            return;
        }

        int id = 0;
        try {
            id = Integer.parseInt(idString);
        } catch (NumberFormatException e) {
            // Escribió el nombre del producto
            try {
                ArrayList<Pair<String, Integer>> pares;
                pares = Main.conseguirDatos().consultarProductos();
                for (Pair<String, Integer> par : pares) {
                    if (idString.equalsIgnoreCase(par.getKey())) {
                        id = par.getValue();
                        break;
                    }
                }
            } catch (SQLException t) {
                Controller.mostrarError("Surgió un error al consultar los productos\n\n" + t.getMessage());
                return;
            }
        }

        try {
            Main.conseguirDatos().deleteProduct(id);
            Controller.mostrarInfo("El producto fue eliminado.");
            controller.consultaTabla(nombreDeLaTabla);
        } catch (Exception e) {
            Controller.mostrarError("Se produjo un error al eliminar el producto.\n\n" + e.getMessage());
        }

        this.idProducto.clear();
    }

    public void editar(ArrayList<String> propiedades, int columna) {
        int id = Integer.parseInt(propiedades.get(0));
        double precio;
        int almacen, idCategoria;
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
        }
    }
}
