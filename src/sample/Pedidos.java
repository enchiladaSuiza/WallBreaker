package sample;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.time.LocalDate;
import java.util.*;

public class Pedidos extends ContenidoUI {
    private DatePicker fecha;
    private TextField idProducto, cantidad, idCliente;
    private Label total;
    private Button agregar, quitar, generar;
    private int posicionParaAgregarProducto;
    private ArrayList<Pair<TextField, TextField>> productos;

    Pedidos(Controller controller) {
        super(controller);
        nombreDeLaTabla = "pedido";
        fecha = new DatePicker();
        fecha.setPromptText("Fecha");
        fecha.setPrefWidth(300);
        fecha.setValue(LocalDate.now());
        idProducto = new TextField();
        cantidad = new TextField();
        idCliente = new TextField();
        Controller.prepararTextField(idProducto, "Producto (ID)", true);
        Controller.prepararTextField(cantidad, "Cantidad", true);
        Controller.prepararTextField(idCliente, "Cliente (ID)", true);
        total = new Label("Total");
        total.setTextFill(Color.WHITE);

        agregar = new Button("Agregar");
        quitar = new Button("Quitar");
        generar = new Button("Generar");
        agregar.setOnAction(e -> agregarProductoAPedido());
        quitar.setOnAction(e -> quitarProductoDePedido());
        generar.setOnAction(e -> generarPedido());
        quitar.setDisable(true);

        Node[] nodosArray = {fecha, idProducto, cantidad, total, agregar, quitar, idCliente, generar};
        boolean[] espacios = {true, false, false, true, false, false, true, true};

        nodos = new ArrayList<>();
        reemplazarNodosDeGrid(nodosArray, espacios);
        posicionParaAgregarProducto = conseguirIndice(total);

        productos = new ArrayList<>();
        productos.add(new Pair<>(idProducto, cantidad));
    }

    public void agregarProductoAPedido() {
        TextField producto = new TextField();
        TextField cantidad = new TextField();
        Controller.prepararTextField(producto, "Producto (ID)", true);
        Controller.prepararTextField(cantidad, "Cantidad", true);
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
                        new Pair<>(Integer.parseInt(producto.getKey().getText()),
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
