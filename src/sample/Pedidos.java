package sample;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.util.*;

import static sample.Controller.prepararTextField;

public class Pedidos extends ContenidoUI {
    private DatePicker fecha;
    private TextField idProducto, cantidad, idCliente;
    private Label total;
    private Button agregar, quitar, generar;
    private int posicionParaAgregarProducto;
    private LinkedHashMap<TextField, TextField> productos;

    Pedidos(Controller controller) {
        super(controller);
        fecha = new DatePicker();
        fecha.setPromptText("Fecha");
        fecha.setValue(LocalDate.now());
        idProducto = new TextField();
        cantidad = new TextField();
        idCliente = new TextField();
        prepararTextField(idProducto, "Producto (ID)", true);
        prepararTextField(cantidad, "Cantidad", true);
        prepararTextField(idCliente, "Cliente (ID)", true);
        total = new Label("Total");

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

        productos = new LinkedHashMap<>();
        productos.put(idProducto, cantidad);
    }

    public void agregarProductoAPedido() {
        TextField producto = new TextField();
        TextField cantidad = new TextField();
        prepararTextField(producto, "Producto (ID)", true);
        prepararTextField(cantidad, "Cantidad", true);
        productos.put(producto, cantidad);
        insertarAGrid(producto, false, posicionParaAgregarProducto);
        insertarAGrid(cantidad, false, posicionParaAgregarProducto + 1);
        posicionParaAgregarProducto += 2;
        if (quitar.isDisabled()) {
            quitar.setDisable(false);
        }
    }

    public void quitarProductoDePedido() {
        List<TextField> textFieldsProductos = new ArrayList<>(productos.keySet());
        int magnitud = textFieldsProductos.size();
        TextField producto = textFieldsProductos.get(magnitud - 1);
        TextField cantidad = productos.remove(producto);
        quitarDeGrid(producto);
        quitarDeGrid(cantidad);
        posicionParaAgregarProducto -= 2;
        if (productos.keySet().size() <= 1) {
            quitar.setDisable(true);
        }
    }

    public void generarPedido() {
        System.out.println("When the low heavy sky weighs like a lid on the spirit, aching for the light");
    }
}
