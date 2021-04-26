package sample;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.util.ArrayList;

public class Proveedores extends ContenidoUI {
    private Button consultar, verProductosBtn, agregar, quitar, agregarProveedorBtn, eliminarProveedorBtn;
    private TextField idProductoConsulta, nombre, apellido, telefono, paginaWeb, idProductoProveedor, idProveedor;
    private ArrayList<TextField> productos;
    private int posicionParaAgregarProducto;

    public Proveedores(Controller controller) {
        super(controller);
        nombreDeLaTabla = "proveedor";
        idProductoConsulta = new TextField();
        nombre = new TextField();
        apellido = new TextField();
        telefono = new TextField();
        paginaWeb = new TextField();
        idProductoProveedor = new TextField();
        idProveedor = new TextField();
        Controller.prepararTextField(idProductoConsulta, "Prouducto (ID)", true);
        Controller.prepararTextField(nombre, "Nombre", false);
        Controller.prepararTextField(apellido, "Apellido", false);
        Controller.prepararTextField(telefono, "Teléfono", true);
        Controller.prepararTextField(paginaWeb, "Página web", false);
        Controller.prepararTextField(idProductoProveedor, "Producto (ID)" , true);
        Controller.prepararTextField(idProveedor, "Proveedor (ID)", true);

        consultar = new Button("Consultar");
        verProductosBtn = new Button("Ver productos");
        agregar = new Button("Agregar");
        quitar = new Button("Quitar");
        agregarProveedorBtn = new Button("Agregar proveedor");
        eliminarProveedorBtn = new Button("Eliminar proveedor");
        verProductosBtn.setOnAction(e -> verProductos());
        consultar.setOnAction(e -> consultarProveedorProducto());
        agregar.setOnAction(e -> agregarProductoAProveedor());
        quitar.setOnAction(e -> quitarProductoDeProveedor());
        agregarProveedorBtn.setOnAction(e -> agregarProveedor());
        eliminarProveedorBtn.setOnAction(e -> eliminarProveedor());
        quitar.setDisable(true);

        Node[] nodosArray = {idProductoConsulta, consultar, Controller.nuevoEspacio(consultar), verProductosBtn,
                Controller.nuevoEspacio(verProductosBtn), nombre, apellido, telefono, paginaWeb, idProductoProveedor,
                agregar, quitar, agregarProveedorBtn, Controller.nuevoEspacio(agregarProveedorBtn), idProveedor,
                eliminarProveedorBtn};
        boolean[] spans = {false, false, true, true, true, false, false, false, false, true, false, false, true, true,
                true, true};

        nodos = new ArrayList<>();
        reemplazarNodosDeGrid(nodosArray, spans);
        posicionParaAgregarProducto = conseguirIndice(agregar);

        productos = new ArrayList<>();
        productos.add(idProductoProveedor);
    }

    public void consultarProveedorProducto() {
        String idString = idProductoConsulta.getText();
        if (idString.isBlank()) {
            Controller.mostrarError("Por favor coloque el ID del producto cuyo proveedor desea consultar.");
            return;
        }
        int id;
        try {
            id = Integer.parseInt(idString);
        } catch (Exception e) {
            Controller.mostrarError("Por favor ingrese un ID válido.");
            return;
        }
        controller.consultaProveedorPorProducto(id);
    }

    private void agregarProductoAProveedor() {
        TextField producto = new TextField();
        Controller.prepararTextField(producto, "Producto (ID)", true);
        productos.add(producto);
        insertarAGrid(producto, true, posicionParaAgregarProducto);
        posicionParaAgregarProducto++;
        if (quitar.isDisabled()) {
            quitar.setDisable(false);
        }
    }

    private void quitarProductoDeProveedor() {
        TextField producto = productos.get(productos.size() - 1);
        productos.remove(producto);
        quitarDeGrid(producto);
        posicionParaAgregarProducto--;
        if (productos.size() <= 1) {
            quitar.setDisable(true);
        }
    }

    private void verProductos() {
        System.out.println("Aquí es donde mostraría mi relación proveedor_producto, si tuviera una");
        verProductosBtn.setText("Ver proveedores");
        verProductosBtn.setOnAction(e -> verProveedores());
    }

    private void verProveedores() {
        System.out.println("I showed you my urgh please respond");
        verProductosBtn.setText("Ver productos");
        verProductosBtn.setOnAction(e -> verProductos());
    }

    private void agregarProveedor() {
        String nombre = this.nombre.getText();
        String apellido = this.apellido.getText();
        String telefono = this.telefono.getText();
        String paginaWeb = this.paginaWeb.getText();
        int idProducto = 0;
        if (!this.idProductoProveedor.getText().isBlank()) {
            try {
                idProducto = Integer.parseInt(this.idProductoProveedor.getText());
            } catch (Exception e) {
                Controller.mostrarError("Porfavor ingrese un ID válido, o deje el campo en blanco para no relacionar " +
                        "un procuto con el proveedor.");
            }
        }

        // Aún así se recomienda que llenen todos :)
        if (apellido.isBlank() || telefono.isBlank()) {
            Controller.mostrarError("Se requiere como mínimo los campos Apellido y Teléfono");
            return;
        }
        try {
            Main.conseguirDatos().addProveedor(nombre, apellido, Long.parseLong(telefono), paginaWeb,
                    idProducto, this.idProductoProveedor.getText().isBlank());
            Controller.mostrarInfo("El proveedor fue añadido.");
            controller.consultaTabla("proveedor");
        } catch (Exception e) {
            Controller.mostrarError("Se produjo un error al añadir al proveedor. El mensaje de error es: " + e.getMessage());
        }
    }

    private void eliminarProveedor() {
        System.out.println("You messed up the linguine");
    }
}
