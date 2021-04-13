package sample;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import static sample.Controller.posicionEnGrid;
import static sample.Controller.prepararTextField;

public class Ventas {
    private DatePicker fechaVenta;
    private TextField productoVentaTF, cantidadVentaTF, efectivoVentaTF, idClienteTF, idPedidoTF;
    private Label totalVentaLbl, cambioVentaLbl;
    private Node[] nodos;

    public Ventas() {
        fechaVenta = new DatePicker();
        fechaVenta.setPromptText("Fecha");
        productoVentaTF = new TextField();
        cantidadVentaTF = new TextField();
        efectivoVentaTF = new TextField();
        idClienteTF = new TextField();
        idPedidoTF = new TextField();
        prepararTextField(productoVentaTF, "Producto", false);
        prepararTextField(cantidadVentaTF, "Cantidad", true);
        prepararTextField(efectivoVentaTF, "Efectivo", true);
        prepararTextField(idClienteTF, "Cliente (ID)", true);
        prepararTextField(idPedidoTF, "Pedido (ID)", true);
        totalVentaLbl = new Label("Total");
        cambioVentaLbl = new Label("Cambio");
        Button agregarProductoAVentaBtn = new Button("Agregar");
        agregarProductoAVentaBtn.setOnAction(e -> agregarProductoAVenta());
        Button generarVentaBtn = new Button("Generar");
        generarVentaBtn.setOnAction(e -> generarVenta());
        posicionEnGrid(fechaVenta, 0, 0, 2);
        posicionEnGrid(productoVentaTF, 0, 1, 1);
        posicionEnGrid(cantidadVentaTF, 1, 1, 1);
        posicionEnGrid(agregarProductoAVentaBtn, 0, 2, 1);
        posicionEnGrid(totalVentaLbl, 1, 2, 1);
        posicionEnGrid(efectivoVentaTF, 0, 4, 1);
        posicionEnGrid(cambioVentaLbl, 1, 4, 1);
        posicionEnGrid(idClienteTF, 0, 5, 1);
        posicionEnGrid(idPedidoTF, 1, 5, 1);
        posicionEnGrid(generarVentaBtn, 0, 6, 2);
        nodos = new Node[]{fechaVenta, productoVentaTF, cantidadVentaTF, totalVentaLbl,
                agregarProductoAVentaBtn, efectivoVentaTF, cambioVentaLbl, idClienteTF, idPedidoTF, generarVentaBtn};
    }

    public Node[] conseguirNodos() { return nodos; }

    public void agregarProductoAVenta() {
        System.out.println("Urgh");
    }
    public void generarVenta() {
        System.out.println("Argh");
    }
}
