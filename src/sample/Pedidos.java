package sample;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.util.Pair;

import java.util.*;

public class Pedidos extends ContenidoUI {
    private TextField idProducto, cantidad, idCliente;
    private Button agregar, quitar, generar;
    private int posicionParaAgregarProducto;
    private ArrayList<Pair<TextField, TextField>> productos;

    Pedidos(Controller controller) {
        super(controller);
        nombreDeLaTabla = "pedido";
        idProducto = new TextField();
        cantidad = new TextField();
        idCliente = new TextField();
        Controller.prepararTextField(idProducto, "Producto", Controller.TEXTFIELD_CADENA);
        Controller.prepararTextField(cantidad, "Cantidad", Controller.TEXTFIELD_ENTERO);
        Controller.prepararTextField(idCliente, "Cliente (ID)", Controller.TEXTFIELD_ENTERO);

        agregar = new Button("Agregar");
        quitar = new Button("Quitar");
        generar = new Button("Generar pedido");
        agregar.setOnAction(e -> agregarProductoAPedido());
        quitar.setOnAction(e -> quitarProductoDePedido());
        generar.setOnAction(e -> generarPedido());
        quitar.setDisable(true);

        // And I discovered Sans...
        LinkedHashMap<Node, Boolean>nodosConEspacios = new LinkedHashMap<>();
        nodosConEspacios.put(idProducto, false);
        nodosConEspacios.put(cantidad, false);
        nodosConEspacios.put(agregar, false);
        nodosConEspacios.put(quitar, false);
        nodosConEspacios.put(idCliente, true);
        nodosConEspacios.put(generar, true);

        nodos = new ArrayList<>();
        reemplazarNodosDeGrid(nodosConEspacios.keySet().toArray(new Node[0]),
                nodosConEspacios.values().toArray(new Boolean[0]));
        posicionParaAgregarProducto = conseguirIndice(agregar);

        productos = new ArrayList<>();
        productos.add(new Pair<>(idProducto, cantidad));
    }

    public void agregarProductoAPedido() {
        TextField producto = new TextField();
        TextField cantidad = new TextField();
        Controller.prepararTextField(producto, "Producto", Controller.TEXTFIELD_CADENA);
        Controller.prepararTextField(cantidad, "Cantidad", Controller.TEXTFIELD_ENTERO);
        productos.add(new Pair<>(producto, cantidad));
        insertarAGrid(producto, false, posicionParaAgregarProducto);
        insertarAGrid(cantidad, false, posicionParaAgregarProducto + 1);
        posicionParaAgregarProducto += 2;
        if (quitar.isDisabled()) {
            quitar.setDisable(false);
        }
    }

    public void quitarProductoDePedido() {
        Pair<TextField, TextField> par = productos.remove(productos.size() - 1);
        quitarDeGrid(par.getKey());
        quitarDeGrid(par.getValue());
        posicionParaAgregarProducto -= 2;
        if (productos.size() <= 1) {
            quitar.setDisable(true);
        }
    }

    public void generarPedido() {
        ArrayList<Pair<Integer, Integer>> productosConCantidad = new ArrayList<>();
        try {
            for (Pair<TextField, TextField> producto : productos) {
                Pair<Integer, Integer> par =
                        new Pair<>(Controller.validarProducto(producto.getKey().getText()),
                                Integer.parseInt(producto.getValue().getText()));
                productosConCantidad.add(par);
            }
            String idCliente = this.idCliente.getText();
            Main.conseguirDatos().addPedido(productosConCantidad, Integer.parseInt(idCliente));
            Controller.mostrarInfo("El pedido fue generado con éxito.");
            controller.consultaTabla(nombreDeLaTabla);
        } catch (Exception e) {
            Controller.mostrarError("Error al generar el pedido. Mensaje:\n" + e.getMessage());
        }
    }
}
