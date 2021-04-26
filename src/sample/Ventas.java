package sample;

import javafx.scene.Node;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.*;

import static sample.Controller.*;

public class Ventas extends ContenidoUI {
    private DatePicker fecha;
    private TextField idProducto, cantidad, efectivo, idCliente, idPedido;
    private Button agregar, quitar, generar;
    private Label total, cambio;
    private int posicionParaAgregarProducto;
    private LinkedHashMap<TextField, TextField> productos;


    public Ventas(Controller controller) {
        super(controller);
        nombreDeLaTabla = "venta";
        fecha = new DatePicker();
        fecha.setPromptText("Fecha");
        fecha.setValue(LocalDate.now());
        idProducto = new TextField();
        cantidad = new TextField();
        efectivo = new TextField();
        idCliente = new TextField();
        idPedido = new TextField();
        prepararTextField(idProducto, "Producto (ID)", true);
        prepararTextField(cantidad, "Cantidad", true);
        prepararTextField(efectivo, "Efectivo", true);
        prepararTextField(idCliente, "Cliente (ID)", true);
        prepararTextField(idPedido, "Pedido (ID)", true);
        total = new Label("Total");
        cambio = new Label("Cambio");

        agregar = new Button("Agregar");
        quitar = new Button("Quitar");
        generar = new Button("Generar");
        agregar.setOnAction(e -> agregarProductoAVenta());
        quitar.setOnAction(e -> quitarProductoDeVenta());
        generar.setOnAction(e -> generarVenta());
        quitar.setDisable(true);

        Node[] nodosArray = {fecha, idProducto, cantidad, total, agregar, quitar, efectivo, cambio, idCliente,
                idPedido, generar};
        boolean[] espacios = {true, false, false, true, false, false, false, false, false, false, true};

        nodos = new ArrayList<>();
        reemplazarNodosDeGrid(nodosArray, espacios);
        posicionParaAgregarProducto = conseguirIndice(total);

        productos = new LinkedHashMap<>();
        productos.put(idProducto, cantidad);
    }

    // Ligado a agregar
    public void agregarProductoAVenta() {
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

    // Ligado a quitar
    public void quitarProductoDeVenta() {
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

    // Ligado a generar
    public void generarVenta() {
        System.out.println("Ka-chin!");
    }
}
