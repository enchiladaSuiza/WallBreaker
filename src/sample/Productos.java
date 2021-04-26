package sample;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;

import java.util.ArrayList;

import static sample.Controller.*;

public class Productos extends ContenidoUI {
    private TextField nombre, precio, almacen, categoria, idProducto;

    Productos(Controller controller) {
        super(controller);
        nombreDeLaTabla = "producto";
        nombre = new TextField();
        precio = new TextField();
        almacen = new TextField();
        categoria = new TextField();
        idProducto = new TextField();
        prepararTextField(nombre, "Producto", false);
        prepararTextField(precio, "Precio", true);
        prepararTextField(almacen, "Cantidad", true);
        prepararTextField(categoria, "Categoria (ID)", true);
        prepararTextField(idProducto, "Producto (ID)", true);

        Button agregarProductoBtn = new Button("Agregar producto");
        Button eliminarProductoBtn = new Button("Eliminar producto");
        agregarProductoBtn.setOnAction(actionEvent -> nuevoProducto());
        eliminarProductoBtn.setOnAction(actionEvent -> borrarProducto());

        Node[] nodosArray = {nombre, precio, almacen, categoria, agregarProductoBtn,
                nuevoEspacio(agregarProductoBtn), idProducto, eliminarProductoBtn};
        nodos = new ArrayList<>();
        for (Node nodo : nodosArray) { sumarAGrid(nodo, true); }
    }

    // Ligada a  agregarProducto
    public void nuevoProducto() {
        String nombre = this.nombre.getText();
        String precio = this.precio.getText();
        String cantidad = almacen.getText();
        String categoria = this.categoria.getText();

        if (nombre.isBlank() || precio.isBlank() || cantidad.isBlank() || categoria.isBlank()) {
            Controller.mostrarError("Porfavor ingrese valores para todos los campos.");
            return;
        }
        try {
            Main.conseguirDatos().addProduct(nombre, Double.parseDouble(this.precio.getText()),
                    Integer.parseInt(almacen.getText()), Integer.parseInt(this.categoria.getText()));
            Controller.mostrarInfo("El producto fue añadido.");
            controller.consultaTabla("producto");
        } catch (Exception e) {
            Controller.mostrarError("Se produjo un error al añadir el producto. El mensaje de error es: " + e.getMessage());
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
            controller.consultaTabla("producto");
        } catch (Exception e) {
            Controller.mostrarError("Se produjo un error al eliminar el producto. El mensaje de error es: " + e.getMessage());
        }
    }
}
