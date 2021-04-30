package sample;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.time.LocalDate;
import java.util.*;

public class Ventas extends ContenidoUI {
    private DatePicker fecha;
    private TextField idProducto, cantidad, efectivo, idCliente, idPedido;
    private Button agregar, quitar, generar;
    private Label total, cambio;
    private int posicionParaAgregarProducto;
    private ArrayList<Pair<TextField, TextField>> productos;


    public Ventas(Controller controller) {
        super(controller);
        nombreDeLaTabla = "venta";
        fecha = new DatePicker();
        fecha.setPrefWidth(300);
        fecha.setPromptText("Fecha");
        fecha.setValue(LocalDate.now());
        idProducto = new TextField();
        cantidad = new TextField();
        efectivo = new TextField();
        idCliente = new TextField();
        idPedido = new TextField();
        Controller.prepararTextField(idProducto, "Producto (ID)", true);
        Controller.prepararTextField(cantidad, "Cantidad", true);
        Controller.prepararTextField(efectivo, "Efectivo", true);
        Controller.prepararTextField(idCliente, "Cliente (ID)", true);
        Controller.prepararTextField(idPedido, "Pedido (ID)", true);
        total = new Label("Total");
        cambio = new Label("Cambio");
        total.setTextFill(Color.WHITE);
        cambio.setTextFill(Color.WHITE);

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

        productos = new ArrayList<>();
        productos.add(new Pair<>(idProducto, cantidad));
    }

    // Ligado a agregar
    public void agregarProductoAVenta() {
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

    // Ligado a quitar
    public void quitarProductoDeVenta() {
        Pair<TextField, TextField> par = productos.remove(productos.size() - 1);
        quitarDeGrid(par.getKey());
        quitarDeGrid(par.getValue());
        posicionParaAgregarProducto -= 2;
        if (productos.size() <= 1) {
            quitar.setDisable(true);
        }
    }

    // Ligado a generar
    public void generarVenta() {
        ArrayList<Pair<Integer, Integer>> productosConCantidad = new ArrayList<>();
        try {
            for (Pair<TextField, TextField> producto : productos) {
                Pair<Integer, Integer> par =
                        new Pair<>(Integer.parseInt(producto.getKey().getText()),
                                Integer.parseInt(producto.getValue().getText()));
                productosConCantidad.add(par);
                // System.out.println(par);
            }
            int idCliente = Integer.parseInt(this.idCliente.getText());
            Pair<Integer, Double> pedidoConMonto =
                    Main.conseguirDatos().generarTotalVenta(idCliente, productosConCantidad);
            double total = pedidoConMonto.getValue();
            double efectivo = Double.parseDouble(this.efectivo.getText());
            this.total.setText(String.valueOf(total));

            Double cambio = (Double)Main.conseguirDatos().generarVenta(pedidoConMonto.getKey(), efectivo);
            if (cambio == null) {
                Controller.mostrarError("El pedido ya fue vendido antes.");
                return;
            }
            else if (cambio == -1.0) {
                Controller.mostrarError("El cambio no cumple en monto");
                return;
            }

            this.cambio.setText(String.valueOf(cambio));
            Controller.mostrarInfo("El pedido y la venta fueron generados con éxito.");

        } catch (Exception e) {
            Controller.mostrarError("Algo salió mal al generar la venta. El error es: "
                    + e.getLocalizedMessage());
        }
    }
}
