package sample;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.util.Pair;

import java.sql.SQLException;
import java.util.*;

public class Pedidos extends ContenidoUI {
    private TextField productoGenerar, cantidadGenerar, idClienteGenerar,
            idPedidoEditar, idClienteEditar, productoEditar, cantidadEditar, idPedidoCancelar;
    private Button agregarGenerar, quitarGenerar, generar, agregarEditar, quitarEditar, editar, cancelar;
    private int posicionParaProductoGenerar, posicionParaProductoEditar;
    private ArrayList<Pair<TextField, TextField>> productosGenerar, productosEditar;

    Pedidos(Controller controller) {
        super(controller);
        nombreDeLaTabla = "pedido";
        productoGenerar = new TextField();
        cantidadGenerar = new TextField();
        idClienteGenerar = new TextField();
        idPedidoEditar = new TextField();
        idClienteEditar = new TextField();
        productoEditar = new TextField();
        cantidadEditar = new TextField();
        idPedidoCancelar = new TextField();
        Controller.prepararTextField(productoGenerar, "Producto", Controller.TEXTFIELD_CADENA);
        Controller.prepararTextField(cantidadGenerar, "Cantidad", Controller.TEXTFIELD_ENTERO);
        Controller.prepararTextField(idClienteGenerar, "Cliente (ID)", Controller.TEXTFIELD_ENTERO);
        Controller.prepararTextField(idPedidoEditar, "Pedido (ID)", Controller.TEXTFIELD_ENTERO);
        Controller.prepararTextField(idClienteEditar, "Nuevo Cliente (ID)", Controller.TEXTFIELD_ENTERO);
        Controller.prepararTextField(productoEditar, "Producto", Controller.TEXTFIELD_CADENA);
        Controller.prepararTextField(cantidadEditar, "Cantidad", Controller.TEXTFIELD_ENTERO);
        Controller.prepararTextField(idPedidoCancelar, "Pedido (ID)", Controller.TEXTFIELD_ENTERO);

        agregarGenerar = new Button("Agregar");
        quitarGenerar = new Button("Quitar");
        generar = new Button("Generar pedido");
        agregarEditar = new Button("Agregar");
        quitarEditar = new Button("Quitar");
        editar = new Button("Editar pedido");
        cancelar = new Button("Cancelar pedido");
        agregarGenerar.setOnAction(e -> agregarProductoEnGenerar());
        quitarGenerar.setOnAction(e -> quitarProductoEnGenerar());
        generar.setOnAction(e -> generarPedido());
        agregarEditar.setOnAction(e -> agregarProductoEnEditar());
        quitarEditar.setOnAction(e -> quitarProductoEnEditar());
        editar.setOnAction(e -> editarPedido());
        cancelar.setOnAction(e -> cancelarPedido());
        quitarGenerar.setDisable(true);
        quitarEditar.setDisable(true);

        // And I discovered Sans...
        LinkedHashMap<Node, Boolean>nodosConEspacios = new LinkedHashMap<>();
        nodosConEspacios.put(agregarGenerar, false);
        nodosConEspacios.put(quitarGenerar, false);
        nodosConEspacios.put(productoGenerar, false);
        nodosConEspacios.put(cantidadGenerar, false);
        nodosConEspacios.put(idClienteGenerar, true);
        nodosConEspacios.put(generar, true);
        nodosConEspacios.put(Controller.nuevoEspacio(generar), true);
        nodosConEspacios.put(agregarEditar, false);
        nodosConEspacios.put(quitarEditar, false);
        nodosConEspacios.put(productoEditar, false);
        nodosConEspacios.put(cantidadEditar, false);
        nodosConEspacios.put(idPedidoEditar, true);
        nodosConEspacios.put(idClienteEditar, true);
        nodosConEspacios.put(editar, true);
        nodosConEspacios.put(Controller.nuevoEspacio(editar), true);
        nodosConEspacios.put(idPedidoCancelar, true);
        nodosConEspacios.put(cancelar, true);

        nodos = new ArrayList<>();
        reemplazarNodosDeGrid(nodosConEspacios.keySet().toArray(new Node[0]),
                nodosConEspacios.values().toArray(new Boolean[0]));
        posicionParaProductoGenerar = conseguirIndice(idClienteGenerar);
        posicionParaProductoEditar = conseguirIndice(idPedidoEditar);

        productosGenerar = new ArrayList<>();
        productosEditar = new ArrayList<>();
        productosGenerar.add(new Pair<>(productoGenerar, cantidadGenerar));
        productosEditar.add(new Pair<>(productoEditar, cantidadEditar));
    }

    public void agregarProductoEnGenerar() {
        TextField producto = new TextField();
        TextField cantidad = new TextField();
        Controller.prepararTextField(producto, "Producto", Controller.TEXTFIELD_CADENA);
        Controller.prepararTextField(cantidad, "Cantidad", Controller.TEXTFIELD_ENTERO);
        productosGenerar.add(new Pair<>(producto, cantidad));
        insertarAGrid(producto, false, posicionParaProductoGenerar);
        insertarAGrid(cantidad, false, posicionParaProductoGenerar + 1);
        posicionParaProductoGenerar += 2;
        posicionParaProductoEditar += 2; // Porque generar está antes
        if (quitarGenerar.isDisabled()) {
            quitarGenerar.setDisable(false);
        }
    }

    public void quitarProductoEnGenerar() {
        Pair<TextField, TextField> par = productosGenerar.remove(productosGenerar.size() - 1);
        quitarDeGrid(par.getKey());
        quitarDeGrid(par.getValue());
        posicionParaProductoGenerar -= 2;
        posicionParaProductoEditar -= 2;
        if (productosGenerar.size() <= 1) {
            quitarGenerar.setDisable(true);
        }
    }

    public void agregarProductoEnEditar() {
        TextField producto = new TextField();
        TextField cantidad = new TextField();
        Controller.prepararTextField(producto, "Producto", Controller.TEXTFIELD_CADENA);
        Controller.prepararTextField(cantidad, "Cantidad", Controller.TEXTFIELD_ENTERO);
        productosEditar.add(new Pair<>(producto, cantidad));
        insertarAGrid(producto, false, posicionParaProductoEditar);
        insertarAGrid(cantidad, false, posicionParaProductoEditar + 1);
        posicionParaProductoEditar += 2;
        if (quitarEditar.isDisabled()) {
            quitarEditar.setDisable(false);
        }
    }

    public void quitarProductoEnEditar() {
        Pair<TextField, TextField> par = productosEditar.remove(productosEditar.size() - 1);
        quitarDeGrid(par.getKey());
        quitarDeGrid(par.getValue());
        posicionParaProductoEditar -= 2;
        if (productosEditar.size() <= 1) {
            quitarEditar.setDisable(true);
        }
    }

    public void generarPedido() {
        ArrayList<Pair<Integer, Integer>> productosConCantidad = new ArrayList<>();
        try {
            for (Pair<TextField, TextField> producto : productosGenerar) {
                Pair<Integer, Integer> par =
                        new Pair<>(Controller.validarProducto(producto.getKey().getText()),
                                Integer.parseInt(producto.getValue().getText()));
                productosConCantidad.add(par);
            }
            String idCliente = this.idClienteGenerar.getText();
            Main.conseguirDatos().addPedido(productosConCantidad, Integer.parseInt(idCliente));
            Controller.mostrarInfo("El pedido fue generado con éxito.");
            while (productosGenerar.size() > 1) {
                quitarProductoEnGenerar();
            }
            productoGenerar.clear();
            cantidadGenerar.clear();
            idClienteGenerar.clear();
            controller.consultaTabla(nombreDeLaTabla);
        } catch (Exception e) {
            Controller.mostrarError("Error al generar el pedido. Mensaje:\n" + e.getMessage());
        }
    }

    public void editarPedido() {
        int idPedido = Integer.parseInt(this.idPedidoEditar.getText());
        int idCliente = Integer.parseInt(this.idClienteEditar.getText());
        ArrayList<Pair<Integer, Integer>> relacion = new ArrayList<>();
        for (Pair<TextField, TextField> par : productosEditar) {
            relacion.add(new Pair<>(Controller.validarProducto(par.getKey().getText()),
                    Integer.parseInt(par.getValue().getText())));
        }
        try {
            Pair<String, Integer> par = Main.conseguirDatos().editPedido(idPedido, idCliente, relacion);
            if (par.getValue() == -1) {
                Controller.mostrarError(par.getKey());
                return;
            }
            Controller.mostrarInfo("Pedido editado con éxito. El nuevo ID es " + par.getValue());
            controller.consultaTabla(nombreDeLaTabla);
            for (int i = 0; i < productosEditar.size() - 1; i++) {
                quitarDeGrid(productosEditar.get(i + 1).getValue());
                quitarDeGrid(productosEditar.get(i + 1).getKey());
            }
            while (productosEditar.size() > 1) {
                quitarProductoEnEditar();
            }
            this.idPedidoEditar.clear();
            idClienteEditar.clear();
            productoEditar.clear();
            cantidadEditar.clear();
        } catch (SQLException throwables) {
            Controller.mostrarError("Error al editar el pedido.\n\n" + throwables.getMessage());
        }
    }

    public void cancelarPedido() {
        int id = Integer.parseInt(idPedidoCancelar.getText());
        try {
            Main.conseguirDatos().cancelPedido(id);
            Controller.mostrarInfo("Pedido cancelado exitosamente.");
            controller.consultaTabla(nombreDeLaTabla);
            idPedidoCancelar.clear();
        } catch (SQLException throwables) {
            Controller.mostrarError("Fallo al cancelar el pedido.\n\n" + throwables.getMessage());
        }
    }
}
