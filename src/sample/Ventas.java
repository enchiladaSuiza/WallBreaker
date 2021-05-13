package sample;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.util.Pair;

import java.sql.SQLException;
import java.util.*;

public class Ventas extends ContenidoUI {
    private TextField idProducto, cantidad, efectivo, idCliente, idClienteConsultar;
    private Button agregar, quitar, generar, consultar;
    private int posicionParaAgregarProducto;
    private ArrayList<Pair<TextField, TextField>> productos;

    public Ventas(Controller controller) {
        super(controller);
        nombreDeLaTabla = "venta";
        idProducto = new TextField();
        cantidad = new TextField();
        efectivo = new TextField();
        idCliente = new TextField();
        idClienteConsultar = new TextField();
        Controller.prepararTextField(idProducto, "Producto", Controller.TEXTFIELD_CADENA);
        Controller.prepararTextField(cantidad, "Cantidad", Controller.TEXTFIELD_ENTERO);
        Controller.prepararTextField(efectivo, "Efectivo", Controller.TEXTFILED_FLOTANTE);
        Controller.prepararTextField(idCliente, "Cliente (ID)", Controller.TEXTFIELD_ENTERO);
        Controller.prepararTextField(idClienteConsultar, "Cliente (ID)", Controller.TEXTFIELD_ENTERO);

        agregar = new Button("Agregar");
        quitar = new Button("Quitar");
        generar = new Button("Generar venta");
        consultar = new Button("Consultar");
        agregar.setOnAction(e -> agregarProductoAVenta());
        quitar.setOnAction(e -> quitarProductoDeVenta());
        generar.setOnAction(e -> generarVenta());
        consultar.setOnAction(e -> consultarVentasDeUnCliente());
        quitar.setDisable(true);

        // Now the old king is dead, LOL
        LinkedHashMap<Node, Boolean> nodosConEspacios = new LinkedHashMap<>();
        nodosConEspacios.put(idClienteConsultar, false);
        nodosConEspacios.put(consultar, false);
        nodosConEspacios.put(Controller.nuevoEspacio(consultar), true);
        nodosConEspacios.put(idProducto, false);
        nodosConEspacios.put(cantidad, false);
        nodosConEspacios.put(agregar, false);
        nodosConEspacios.put(quitar, false);
        nodosConEspacios.put(efectivo, true);
        nodosConEspacios.put(idCliente, true);
        nodosConEspacios.put(generar, true);

        nodos = new ArrayList<>();
        reemplazarNodosDeGrid(nodosConEspacios.keySet().toArray(new Node[0]),
                nodosConEspacios.values().toArray(new Boolean[0]));
        posicionParaAgregarProducto = conseguirIndice(agregar);

        productos = new ArrayList<>();
        productos.add(new Pair<>(idProducto, cantidad));
    }

    // Ligado a agregar
    public void agregarProductoAVenta() {
        TextField producto = new TextField();
        TextField cantidad = new TextField();
        Controller.prepararTextField(producto, "Producto (ID)", Controller.TEXTFIELD_CADENA);
        Controller.prepararTextField(cantidad, "Cantidad", Controller.TEXTFIELD_ENTERO);
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
                        new Pair<>(Controller.validarProducto(producto.getKey().getText()),
                                Integer.parseInt(producto.getValue().getText()));
                productosConCantidad.add(par);
            }
            int idCliente = Integer.parseInt(this.idCliente.getText());

            Pair<Integer, Double> pedidoConMonto =
                    Main.conseguirDatos().generarTotalVenta(idCliente, productosConCantidad);
            double efectivo = Double.parseDouble(this.efectivo.getText());
            Double cambio = (Double)Main.conseguirDatos().generarVenta(pedidoConMonto.getKey(), efectivo);
            if (cambio == null) {
                Controller.mostrarError("El pedido ya fue vendido antes.");
                return;
            }
            else if (cambio == -1.0) {
                Controller.mostrarError("El efectivo no cumple con el monto.");
                return;
            }

            Controller.mostrarInfo("La venta y el pedido fueron generados con éxito.");
            controller.consultaTabla(nombreDeLaTabla);
        } catch (Exception e) {
            Controller.mostrarError("Algo salió mal al generar la venta.\n\n" + e.getLocalizedMessage());
        }

        while (productos.size() > 1) {
            quitarProductoDeVenta();
        }
        this.idProducto.clear();
        this.cantidad.clear();
        this.efectivo.clear();
        this.idCliente.clear();
    }

    public void consultarVentasDeUnCliente() {
        int idCliente = Integer.parseInt(this.idClienteConsultar.getText());
        controller.consultaEspecial(Controller.VENTAS_POR_CLIENTE, idCliente);
    }

    public void editar(ArrayList<String> propiedades, int columna) {
        if (columna == 1 ||  columna == 2 || columna == 3 || columna == 4 || columna == 6) {
            Controller.mostrarError("Solo es posible modificar el ID del cliente, el ID del pedido o el efectivo.");
            controller.refrescarTabla();
            return;
        }

        int id = Integer.parseInt(propiedades.get(2));
        int cliente;
        int pedido;
        double efectivo;

        try {
            cliente = Integer.parseInt(propiedades.get(0));
        } catch (NumberFormatException exception) {
            Controller.mostrarError("Por favor ingrese un ID válido.");
            controller.refrescarTabla();
            return;
        }

        try {
            efectivo = Double.parseDouble(propiedades.get(5));
        } catch (NumberFormatException exception) {
            Controller.mostrarError("Por favor ingrese una cantidad válida.");
            controller.refrescarTabla();
            return;
        }

        try {
            pedido = Integer.parseInt(propiedades.get(7));
        } catch (NumberFormatException exception) {
            Controller.mostrarError("Por favor ingrese un ID válido");
            controller.refrescarTabla();
            return;
        }

        int aModificar;
        switch (columna) {
            case 0 -> aModificar = 1;
            case 5 -> aModificar = 2;
            case 7 -> aModificar = 3;
            default -> aModificar = 0;
        }

        try {
            Main.conseguirDatos().editVenta(id, cliente, efectivo, pedido, new int[]{aModificar});
        } catch (SQLException throwables) {
            Controller.mostrarError("Error al editar la venta.\n\n" + throwables.getMessage());
            controller.refrescarTabla();
        }
    }
}
