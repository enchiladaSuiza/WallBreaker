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
import java.util.LinkedHashMap;

public class Productos extends ContenidoUI {
    private final TextField nombre, precio, almacen, idProducto;
    private final ComboBox<String> categoria, categoriaConsulta;

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

        categoria = new ComboBox<>();
        categoriaConsulta = new ComboBox<>();
        categoriaConsulta.setPromptText("Categoría");
        categoria.setPromptText("Categoría");
        categoria.setPrefWidth(300);
        categoriaConsulta.setPrefWidth(300);
        actualizarCategorias();

        Button agregarProductoBtn = new Button("Agregar producto");
        Button eliminarProductoBtn = new Button("Eliminar producto");
        Button consultarCategoriaBtn = new Button("Consultar");
        agregarProductoBtn.setOnAction(actionEvent -> nuevoProducto());
        eliminarProductoBtn.setOnAction(actionEvent -> borrarProducto());
        consultarCategoriaBtn.setOnAction(actionEvent -> consultarProductosPorCategoria());

        // Listen as the crowd would sing
        LinkedHashMap<Node, Boolean> nodosConEspacios = new LinkedHashMap<>();
        nodosConEspacios.put(categoriaConsulta, true);
        nodosConEspacios.put(consultarCategoriaBtn, true);
        nodosConEspacios.put(Controller.nuevoEspacio(consultarCategoriaBtn), true);
        nodosConEspacios.put(nombre, true);
        nodosConEspacios.put(precio, true);
        nodosConEspacios.put(almacen, true);
        nodosConEspacios.put(categoria, true);
        nodosConEspacios.put(agregarProductoBtn, true);
        nodosConEspacios.put(Controller.nuevoEspacio(agregarProductoBtn), true);
        nodosConEspacios.put(idProducto, true);
        nodosConEspacios.put(eliminarProductoBtn, true);

        nodos = new ArrayList<>();
        reemplazarNodosDeGrid(nodosConEspacios.keySet().toArray(new Node[0]),
                nodosConEspacios.values().toArray(new Boolean[0]));
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
            return;
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

        int id = Controller.validarProducto(idString);

        try {
            Main.conseguirDatos().deleteProduct(id);
            Controller.mostrarInfo("El producto fue eliminado.");
            controller.consultaTabla(nombreDeLaTabla);
        } catch (Exception e) {
            Controller.mostrarError("Se produjo un error al eliminar el producto.\n\n" + e.getMessage());
        }

        this.idProducto.clear();
    }

    public void consultarProductosPorCategoria() {
        int categoria = 0;
        try {
            for (Pair<String, Integer> par : Main.conseguirDatos().consultarCategorias()) {
                if (par.getKey().equals(this.categoriaConsulta.getValue())) {
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
        controller.consultaEspecial(Controller.PRODUCTOS_POR_CATEGORIA, categoria);
    }

    public void actualizarCategorias() {
        categoria.getItems().clear();
        categoriaConsulta.getItems().clear();
        ArrayList<String> llaves = new ArrayList<>();
        try {
            ArrayList<Pair<String, Integer>> pares = Main.conseguirDatos().consultarCategorias();
            for (Pair<String, Integer> par : pares) {
                llaves.add(par.getKey());
            }
        } catch (SQLException t) {
            Controller.mostrarError("Surgió un error consultado las cateogrías.\n\n" + t.getMessage());
        }
        categoria.setItems(FXCollections.observableArrayList(llaves));
        categoriaConsulta.setItems(FXCollections.observableArrayList(llaves));
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
