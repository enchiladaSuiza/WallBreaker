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
    private Button eliminarProveedorBtn, editarBtn, guardarBtn, agregarEditar, quitarEditar;
    private TextField productoConsulta, nombre, apellido, telefono, paginaWeb, productoDeProveedor;
    private TextField proveedorEliminar, proveedorConsultar;
    private ArrayList<TextField> productosPrevistos, productosConsultados;
    private ArrayList<Integer> productosAnteriores;
    private int posicionAgregarNuevo, posicionMostrar, posicionAgregarEditar, idProveedorActual;

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
        agregarEditar = new Button("Agregar");
        quitarEditar = new Button("Quitar");
        generarProveedorBtn = new Button("Generar proveedor");
        eliminarProveedorBtn = new Button("Eliminar proveedor");
        editarBtn = new Button("Editar");
        guardarBtn = new Button("Guardar");
        verRelacionBtn.setOnAction(e -> verRelacion());
        consultar.setOnAction(e -> consultarProveedorProducto());
        agregar.setOnAction(e -> agregarProductoAProveedor());
        quitar.setOnAction(e -> quitarProductoDeProveedor());
        generarProveedorBtn.setOnAction(e -> agregarProveedor());
        eliminarProveedorBtn.setOnAction(e -> eliminarProveedor());
        editarBtn.setOnAction(e -> editarProductosProvistos());
        guardarBtn.setOnAction(e -> guardarCambios());
        agregarEditar.setOnAction(e -> agregarProductoAEditar());
        quitarEditar.setOnAction(e -> quitarProductoDeEditar());
        quitar.setDisable(true);
        agregarEditar.setDisable(true);
        quitarEditar.setDisable(true);
        guardarBtn.setDisable(true);

        // I used to roll the dice
        LinkedHashMap<Node, Boolean> nodosConEspacios = new LinkedHashMap<>();
        nodosConEspacios.put(verRelacionBtn, true);
        nodosConEspacios.put(Controller.nuevoEspacio(consultar), true);
        nodosConEspacios.put(productoConsulta, true);
        nodosConEspacios.put(consultar, true);
        nodosConEspacios.put(Controller.nuevoEspacio(verRelacionBtn), true);
        nodosConEspacios.put(agregar, false);
        nodosConEspacios.put(quitar, false);
        nodosConEspacios.put(nombre, true);
        nodosConEspacios.put(apellido, true);
        nodosConEspacios.put(telefono, true);
        nodosConEspacios.put(paginaWeb, true);
        nodosConEspacios.put(productoDeProveedor, true);
        nodosConEspacios.put(generarProveedorBtn, true);
        nodosConEspacios.put(Controller.nuevoEspacio(generarProveedorBtn), true);
        nodosConEspacios.put(agregarEditar, false);
        nodosConEspacios.put(quitarEditar, false);
        nodosConEspacios.put(proveedorConsultar, false);
        nodosConEspacios.put(editarBtn, false);
        nodosConEspacios.put(guardarBtn, true);
        nodosConEspacios.put(Controller.nuevoEspacio(editarBtn), true);
        nodosConEspacios.put(proveedorEliminar, true);
        nodosConEspacios.put(eliminarProveedorBtn, true);

        nodos = new ArrayList<>();
        reemplazarNodosDeGrid(nodosConEspacios.keySet().toArray(new Node[0]),
                nodosConEspacios.values().toArray(new Boolean[0]));
        posicionAgregarNuevo = conseguirIndice(generarProveedorBtn);
        posicionMostrar = conseguirIndice(editarBtn) + 1;
        posicionAgregarEditar = posicionMostrar;

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
        insertarAGrid(producto, true, posicionAgregarNuevo);
        posicionAgregarNuevo++;
        posicionMostrar++;
        posicionAgregarEditar++; // Lord please end it all
        if (quitar.isDisabled()) {
            quitar.setDisable(false);
        }
    }

    private void quitarProductoDeProveedor() {
        TextField producto = productosPrevistos.get(productosPrevistos.size() - 1);
        productosPrevistos.remove(producto);
        quitarDeGrid(producto);
        posicionAgregarNuevo--;
        posicionMostrar--;
        posicionAgregarEditar--;
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
        try {
            idProveedorActual = Integer.parseInt(this.proveedorConsultar.getText());
        } catch (NumberFormatException e) {
            return;
        }

        for (TextField producto : productosConsultados) {
            quitarDeGrid(producto);
        }
        guardarBtn.setDisable(false);
        productosConsultados.clear();

        try {
            productosAnteriores = Main.conseguirDatos().conseguirProductosDeUnProveedor(idProveedorActual);
            if (productosAnteriores.isEmpty()) {
                Controller.mostrarError("El proveedor no existe, o no provee ningún producto.");
                return;
            }

            for (int i = 0; i < productosAnteriores.size(); i++) {
                TextField productoTf = new TextField(String.valueOf(productosAnteriores.get(i)));
                Controller.prepararTextField(productoTf, "Producto", Controller.TEXTFIELD_CADENA);
                productosConsultados.add(productoTf);
                insertarAGrid(productoTf, true, posicionMostrar + i);
            }
            posicionAgregarEditar = posicionMostrar + productosAnteriores.size();

            agregarEditar.setDisable(false);
            if (productosConsultados.size() > 1) {
                quitarEditar.setDisable(false);
            }

        } catch (SQLException t) {
            Controller.mostrarError("Error al consultar los productos en relación al proveedor.\n\n"
                    + t.getMessage());
        }
    }

    private void guardarCambios() {
        ArrayList<Integer> productosNuevos = new ArrayList<>();
        for (TextField productoTf : productosConsultados) {
            productosNuevos.add(Controller.validarProducto(productoTf.getText()));
        }


        if (productosAnteriores.size() == productosNuevos.size()) {
            try {
                Main.conseguirDatos().editProdsProv(idProveedorActual,
                        Main.conseguirDatos().conseguirProductosDeUnProveedor(idProveedorActual),
                        productosNuevos);
                Controller.mostrarInfo("Productos actualizados correctamente.");
                productosAnteriores = productosNuevos;
            } catch (SQLException t) {
                Controller.mostrarError("Error al conseguir los productos del proveedor.\n\n" + t.getMessage());
                return;
            }
        } else {
            for (int producto : productosAnteriores) { // Quitar
                if (!productosNuevos.contains(producto)) {
                    try {
                        Main.conseguirDatos().deleteProdFromProvee(idProveedorActual, producto);
                    } catch (SQLException t) {
                        Controller.mostrarError("Error al eliminar productos de proveedor.\n\n" + t.getMessage());
                        return;
                    }
                }
            }
            ArrayList<Integer> productosParaAgregar = new ArrayList<>();
            productosNuevos.forEach(producto -> { // Agregar
                if (!productosAnteriores.contains(producto)) {
                    productosParaAgregar.add(producto);
                }
            });

            try {
                Main.conseguirDatos().addProdToProveedor(idProveedorActual, productosParaAgregar);
            } catch (SQLException t) {
                Controller.mostrarError("Error al agregar productos al proveedor.\n\n" + t.getMessage());
                return;
            }

            Controller.mostrarInfo("Productos actualizados correctamente.");
        }

        for (TextField producto : productosConsultados) {
            quitarDeGrid(producto);
        }
        guardarBtn.setDisable(true);
        productosConsultados.clear();
        proveedorConsultar.clear();
    }

    private void agregarProductoAEditar() {
        TextField producto = new TextField();
        Controller.prepararTextField(producto, "Producto", Controller.TEXTFIELD_CADENA);
        productosConsultados.add(producto);
        insertarAGrid(producto, true, posicionAgregarEditar);
        posicionAgregarEditar++;
        if (quitarEditar.isDisabled()) {
            quitarEditar.setDisable(false);
        }
    }

    private void quitarProductoDeEditar() {
        TextField producto = productosConsultados.get(productosConsultados.size() - 1);
        productosConsultados.remove(producto);
        quitarDeGrid(producto);
        posicionAgregarEditar--;
        if (productosConsultados.size() <= 1) {
            quitarEditar.setDisable(true);
        }
    }

    public void editar(ArrayList<String> propiedades, int columna) {
        int id = Integer.parseInt(propiedades.get(0));
        String nombre = propiedades.get(1);
        String apellido = propiedades.get(2);
        String paginaWeb = propiedades.get(4);
        long telefono;

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
