package sample;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.util.ArrayList;

public class Proveedores extends ContenidoUI {
    private Button consultar, verRelacionBtn, agregar, quitar, agregarProveedorBtn, eliminarProveedorBtn;
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
        Controller.prepararTextField(idProductoConsulta, "Prouducto (ID)", Controller.TEXTFIELD_ENTERO);
        Controller.prepararTextField(nombre, "Nombre", Controller.TEXTFIELD_CADENA);
        Controller.prepararTextField(apellido, "Apellido", Controller.TEXTFIELD_CADENA);
        Controller.prepararTextField(telefono, "Teléfono", Controller.TEXTFIELD_ENTERO);
        Controller.prepararTextField(paginaWeb, "Página web", Controller.TEXTFIELD_CADENA);
        Controller.prepararTextField(idProductoProveedor, "Producto (ID)" , Controller.TEXTFIELD_ENTERO);
        Controller.prepararTextField(idProveedor, "Proveedor (ID)", Controller.TEXTFIELD_ENTERO);

        consultar = new Button("Consultar");
        verRelacionBtn = new Button("Ver relación");
        agregar = new Button("Agregar");
        quitar = new Button("Quitar");
        agregarProveedorBtn = new Button("Agregar proveedor");
        eliminarProveedorBtn = new Button("Eliminar proveedor");
        verRelacionBtn.setOnAction(e -> verRelacion());
        consultar.setOnAction(e -> consultarProveedorProducto());
        agregar.setOnAction(e -> agregarProductoAProveedor());
        quitar.setOnAction(e -> quitarProductoDeProveedor());
        agregarProveedorBtn.setOnAction(e -> agregarProveedor());
        eliminarProveedorBtn.setOnAction(e -> eliminarProveedor());
        quitar.setDisable(true);

        Node[] nodosArray = {idProductoConsulta, consultar, Controller.nuevoEspacio(consultar), verRelacionBtn,
                Controller.nuevoEspacio(verRelacionBtn), nombre, apellido, telefono, paginaWeb, idProductoProveedor,
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
        Controller.prepararTextField(producto, "Producto (ID)", Controller.TEXTFIELD_ENTERO);
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

    private void verRelacion() {
        controller.consultaProveedores();
        verRelacionBtn.setText("Ver solo proveedores");
        verRelacionBtn.setOnAction(e -> verSoloProveedores());
    }
    private void verSoloProveedores() {
        controller.consultaTabla(nombreDeLaTabla);
        verRelacionBtn.setText("Ver relación");
        verRelacionBtn.setOnAction(e -> verRelacion());
    }

    private void agregarProveedor() {
        String nombre = this.nombre.getText();
        String apellido = this.apellido.getText();
        String stringTelefono = this.telefono.getText();
        String paginaWeb = this.paginaWeb.getText();
        ArrayList<Integer> idProductos = new ArrayList<>();
        try {
            for (TextField producto: productos) {
                idProductos.add(Integer.parseInt(producto.getText()));
            }
        } catch (NumberFormatException e) {
            Controller.mostrarError("Por favor coloque IDs válidos.");
        }

        // Aún así se recomienda que llenen todos :)
        if (apellido.isBlank() || stringTelefono.isBlank()) {
            Controller.mostrarError("Se requiere como mínimo los campos Apellido y Teléfono");
            return;
        }

        long telefono = 0;
        try {
            telefono = Long.parseLong(stringTelefono);
        } catch (NumberFormatException e) {
            Controller.mostrarError("Por favor coloque un número de teléfono válido");
        }

        try {
            Main.conseguirDatos().addProveedor(nombre, apellido, telefono, paginaWeb, idProductos);
            Controller.mostrarInfo("El proveedor fue añadido.");
            controller.consultaTabla("proveedor");
        } catch (Exception e) {
            Controller.mostrarError("Se produjo un error al añadir al proveedor.\n" + e.getMessage());
        }

        this.nombre.clear();
        this.apellido.clear();
        this.telefono.clear();
        this.paginaWeb.clear();
        this.idProductoProveedor.clear();
        while (productos.size() > 1) {
            quitarProductoDeProveedor();
        }
    }
    private void eliminarProveedor() {
        String idProveedor = this.idProveedor.getText();
        try {
            Main.conseguirDatos().deleteProveedor(Integer.parseInt(idProveedor));
            Controller.mostrarInfo("Proveedor eliminado correctamente");
            controller.consultaTabla(nombreDeLaTabla);
        } catch (Exception e) {
            Controller.mostrarError("Error al eliminar el proveedor.\n" + e.getMessage());
        }
    }

    public void editar(ArrayList<String> propiedades, int columna) {
        int id = Integer.parseInt(propiedades.get(0));
        String nombre = propiedades.get(1);
        String apellido = propiedades.get(2);
        String paginaWeb = propiedades.get(4);
        long telefono = 0;

        try {
            telefono = Long.parseLong(propiedades.get(3));
        } catch (NumberFormatException e) {
            Controller.mostrarError("Por favor ingrese un teléfono válido.");
            controller.refrescarTabla();
            return;
        }

        try {
            Main.conseguirDatos().editProveedor(id, nombre, apellido, telefono, paginaWeb, new int[]{columna});
        } catch (SQLException e) {
            Controller.mostrarError("Algo salió mal al editar al proveedor.\n" + e.getMessage());
            controller.refrescarTabla();
        }
    }
}
