package sample;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;

import static sample.Controller.*;

public class Productos {
    private TextField nombreProductoTF, precioProductoTF, almacenProductoTF, categoriaProductoTF, idProductoTF;
    private Node[] nodos;

    Productos() {
        nombreProductoTF = new TextField();
        precioProductoTF = new TextField();
        almacenProductoTF = new TextField();
        categoriaProductoTF = new TextField();
        idProductoTF = new TextField();
        prepararTextField(nombreProductoTF, "Producto", false);
        prepararTextField(precioProductoTF, "Precio", true);
        prepararTextField(almacenProductoTF, "Cantidad", true);
        prepararTextField(categoriaProductoTF, "Categoria (ID)", true);
        prepararTextField(idProductoTF, "Producto (ID)", true);

        Button agregarProductoBtn = new Button("Agregar producto");
        Button eliminarProductoBtn = new Button("Eliminar producto");
        agregarProductoBtn.setOnAction(actionEvent -> nuevoProducto());
        eliminarProductoBtn.setOnAction(actionEvent -> borrarProducto());

        nodos = new Node[]{nombreProductoTF, precioProductoTF, almacenProductoTF, categoriaProductoTF,
                agregarProductoBtn, nuevoEspacio(agregarProductoBtn), idProductoTF, eliminarProductoBtn};
        for (int i = 0; i < nodos.length; i++) {
            posicionEnGrid(nodos[i], 0, i, 2);
        }
    }

    public Node[] conseguirNodos() { return nodos; }

    // Ligada a  agregarProducto
    public void nuevoProducto() {
        String nombre = nombreProductoTF.getText();
        String precio = precioProductoTF.getText();
        String cantidad = almacenProductoTF.getText();
        String categoria = categoriaProductoTF.getText();

        if (nombre.isBlank() || precio.isBlank() || cantidad.isBlank() || categoria.isBlank()) {
            Controller.mostrarError("Porfavor ingrese valores para todos los campos.");
            return;
        }
        try {
            Main.conseguirDatos().addProduct(nombre, Double.parseDouble(precioProductoTF.getText()),
                    Integer.parseInt(almacenProductoTF.getText()), Integer.parseInt(categoriaProductoTF.getText()));
            Controller.mostrarInfo("El producto fue añadido.");
            Main.conseguirContorller().consulta("producto");
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

        String id = idProductoTF.getText();
        if (id.isBlank()) {
            Controller.mostrarError("Porfavor ingrese el ID del producto que desea eliminar");
            return;
        }

        try {
            Main.conseguirDatos().deleteProduct(Integer.parseInt(id));
            Controller.mostrarInfo("El producto fue eliminado.");
            Main.conseguirContorller().consulta("producto");
        } catch (Exception e) {
            Controller.mostrarError("Se produjo un error al eliminar el producto. El mensaje de error es: " + e.getMessage());
        }
    }
}
