package sample;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Proveedores extends ContenidoUI {
    private Button consultar, verRelacionBtn, agregar, quitar, generarProveedorBtn;
    private Button eliminarProveedorBtn, editarBtn, guardarBtn;
    private TextField productoConsulta, nombre, apellido, telefono, paginaWeb, productoDeProveedor;
    private TextField proveedorEliminar, proveedorConsultar;
    private ArrayList<TextField> productosPrevistos, productosConsultados;
    private int posicionParaAgregarProducto, posicionParaMostrarPorductos, idProveedorActual;

    public Proveedores(Controller controller) {
        super(controller);
        nombreDeLaTabla = "proveedor";
        productoConsulta = new TextField();
        nombre = new TextField();
        apellido = new TextField();
        telefono = new TextField();
        paginaWeb = new TextField();
        productoDeProveedor = new TextField();
        proveedorEliminar = new TextField();
        proveedorConsultar = new TextField();
        Controller.prepararTextField(productoConsulta, "Prouducto", Controller.TEXTFIELD_CADENA);
        Controller.prepararTextField(nombre, "Nombre", Controller.TEXTFIELD_CADENA);
        Controller.prepararTextField(apellido, "Apellido", Controller.TEXTFIELD_CADENA);
        Controller.prepararTextField(telefono, "Teléfono", Controller.TEXTFIELD_ENTERO);
        Controller.prepararTextField(paginaWeb, "Página web", Controller.TEXTFIELD_CADENA);
        Controller.prepararTextField(productoDeProveedor, "Producto" , Controller.TEXTFIELD_CADENA);
        Controller.prepararTextField(proveedorEliminar, "Proveedor (ID)", Controller.TEXTFIELD_ENTERO);
        Controller.prepararTextField(proveedorConsultar, "Proveedor (ID)", Controller.TEXTFIELD_ENTERO);

        consultar = new Button("Consultar");
        verRelacionBtn = new Button("Ver relación");
        agregar = new Button("Agregar");
        quitar = new Button("Quitar");
        generarProveedorBtn = new Button("Generar proveedor");
        eliminarProveedorBtn = new Button("Eliminar proveedor");
        editarBtn = new Button("Editar proveedor");
        guardarBtn = new Button("Guardar");
        verRelacionBtn.setOnAction(e -> verRelacion());
        consultar.setOnAction(e -> consultarProveedorProducto());
        agregar.setOnAction(e -> agregarProductoAProveedor());
        quitar.setOnAction(e -> quitarProductoDeProveedor());
        generarProveedorBtn.setOnAction(e -> agregarProveedor());
        eliminarProveedorBtn.setOnAction(e -> eliminarProveedor());
        editarBtn.setOnAction(e -> editarProductosProvistos());
        guardarBtn.setOnAction(e -> guardarCambios());
        quitar.setDisable(true);

        // I used to roll the dice
        LinkedHashMap<Node, Boolean> nodosConEspacios = new LinkedHashMap<>();
        nodosConEspacios.put(productoConsulta, false);
        nodosConEspacios.put(consultar, false);
        nodosConEspacios.put(Controller.nuevoEspacio(consultar), true);
        nodosConEspacios.put(verRelacionBtn, true);
        nodosConEspacios.put(Controller.nuevoEspacio(verRelacionBtn), true);
        nodosConEspacios.put(nombre, false);
        nodosConEspacios.put(apellido, false);
        nodosConEspacios.put(telefono, false);
        nodosConEspacios.put(paginaWeb, false);
        nodosConEspacios.put(productoDeProveedor, true);
        nodosConEspacios.put(agregar, false);
        nodosConEspacios.put(quitar, false);
        nodosConEspacios.put(generarProveedorBtn, true);
        nodosConEspacios.put(Controller.nuevoEspacio(generarProveedorBtn), true);
        nodosConEspacios.put(proveedorConsultar, true);
        nodosConEspacios.put(editarBtn, true);
        nodosConEspacios.put(Controller.nuevoEspacio(editarBtn), true);
        nodosConEspacios.put(proveedorEliminar, true);
        nodosConEspacios.put(eliminarProveedorBtn, true);

        nodos = new ArrayList<>();
        reemplazarNodosDeGrid(nodosConEspacios.keySet().toArray(new Node[0]),
                nodosConEspacios.values().toArray(new Boolean[0]));
        posicionParaAgregarProducto = conseguirIndice(agregar);
        posicionParaMostrarPorductos = conseguirIndice(editarBtn) + 1;

        productosPrevistos = new ArrayList<>();
        productosPrevistos.add(productoDeProveedor);
        productosConsultados = new ArrayList<>();
    }

    public void consultarProveedorProducto() {
        String idString = productoConsulta.getText();
        if (idString.isBlank()) {
            Controller.mostrarError("Por favor coloque el ID del producto cuyo proveedor desea consultar.");
            return;
        }
        int id = Controller.validarProducto(idString);
        controller.consultaEspecial(Controller.PROVEEDOR_POR_PRODUCTO, id);
    }

    private void agregarProductoAProveedor() {
        TextField producto = new TextField();
        Controller.prepararTextField(producto, "Producto", Controller.TEXTFIELD_CADENA);
        productosPrevistos.add(producto);
        insertarAGrid(producto, true, posicionParaAgregarProducto);
        posicionParaAgregarProducto++;
        if (quitar.isDisabled()) {
            quitar.setDisable(false);
        }
    }
    private void quitarProductoDeProveedor() {
        TextField producto = productosPrevistos.get(productosPrevistos.size() - 1);
        productosPrevistos.remove(producto);
        quitarDeGrid(producto);
        posicionParaAgregarProducto--;
        if (productosPrevistos.size() <= 1) {
            quitar.setDisable(true);
        }
    }

    private void verRelacion() {
        controller.consultaEspecial(Controller.PROVEEDOR, 0); // El parámetro aquí no importa
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
            for (TextField producto: productosPrevistos) {
                idProductos.add(Controller.validarProducto(producto.getText()));
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
        this.productoDeProveedor.clear();
        while (productosPrevistos.size() > 1) {
            quitarProductoDeProveedor();
        }
    }

    private void eliminarProveedor() {
        ButtonType resultado = Controller.mostrarConfirmacion("¿Desea eliminar el producto?");
        if (resultado != ButtonType.OK) {
            return;
        }

        String idProveedor = this.proveedorEliminar.getText();
        if (idProveedor.isBlank()) {
            Controller.mostrarError("Por favor ingrese el ID del proveedor que desea eliminar.");
            return;
        }

        try {
            Main.conseguirDatos().deleteProveedor(Integer.parseInt(idProveedor));
            Controller.mostrarInfo("Proveedor eliminado correctamente");
            controller.consultaTabla(nombreDeLaTabla);
        } catch (Exception e) {
            Controller.mostrarError("Error al eliminar el proveedor.\n\n" + e.getMessage());
        }

        this.proveedorEliminar.clear();
    }

    private void editarProductosProvistos() {
        idProveedorActual = Integer.parseInt(this.proveedorConsultar.getText());

        for (TextField producto : productosConsultados) {
            quitarDeGrid(producto);
        }
        quitarDeGrid(guardarBtn);
        productosConsultados.clear();

        try {
            ArrayList<Integer> productos = Main.conseguirDatos().conseguirProductosDeUnProveedor(idProveedorActual);
            if (productos.isEmpty()) {
                Controller.mostrarError("El proveedor no existe, o no provee ningún producto.");
                return;
            }

            for (int i = 0; i < productos.size(); i++) {
                TextField productoTf = new TextField(String.valueOf(productos.get(i)));
                Controller.prepararTextField(productoTf, "Producto", Controller.TEXTFIELD_CADENA);
                productosConsultados.add(productoTf);
                insertarAGrid(productoTf, true, posicionParaMostrarPorductos + i);
            }
            insertarAGrid(guardarBtn, true, posicionParaMostrarPorductos + productos.size());

        } catch (SQLException t) {
            Controller.mostrarError("Error al consultar los productos en relación al proveedor.\n\n"
                    + t.getMessage());
        }
    }

    private void guardarCambios() {
        ArrayList<Integer> nuevosProductos = new ArrayList<>();
        for (TextField productoTf : productosConsultados) {
            nuevosProductos.add(Integer.parseInt(productoTf.getText()));
        }

        try {
            Main.conseguirDatos().editProdsProv(idProveedorActual,
                    Main.conseguirDatos().conseguirProductosDeUnProveedor(idProveedorActual),
                    nuevosProductos);
            Controller.mostrarInfo("Productos actualizados correctamente.");

            for (TextField producto : productosConsultados) {
                quitarDeGrid(producto);
            }
            quitarDeGrid(guardarBtn);
            productosConsultados.clear();
            proveedorConsultar.clear();
        } catch (SQLException t) {
            Controller.mostrarError("Error al conseguir los productos del proveedor.\n\n" + t.getMessage());
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
