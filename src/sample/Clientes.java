package sample;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Clientes extends ContenidoUI {
    private TextField nombre, apellido, telefono, id;
    private Button agregar, eliminar;

    public Clientes(Controller controller) {
        super(controller);
        nombreDeLaTabla = "cliente";
        nombre = new TextField();
        apellido = new TextField();
        telefono = new TextField();
        id = new TextField();
        Controller.prepararTextField(nombre, "Nombre", Controller.TEXTFIELD_CADENA);
        Controller.prepararTextField(apellido, "Apellido", Controller.TEXTFIELD_CADENA);
        Controller.prepararTextField(telefono, "Teléfono", Controller.TEXTFIELD_ENTERO);
        Controller.prepararTextField(id, "Cliente (ID)", Controller.TEXTFIELD_ENTERO);

        agregar = new Button("Agregar cliente");
        eliminar = new Button("Eliminar cliente");
        agregar.setOnAction(e -> agregarCliente());
        eliminar.setOnAction(e -> eliminarCliente());

        // Upon pillars of salt
        LinkedHashMap<Node, Boolean> nodosConEspacios = new LinkedHashMap<>();
        nodosConEspacios.put(nombre, true);
        nodosConEspacios.put(apellido, true);
        nodosConEspacios.put(telefono, true);
        nodosConEspacios.put(agregar, true);
        nodosConEspacios.put(Controller.nuevoEspacio(agregar), true);
        nodosConEspacios.put(id, true);
        nodosConEspacios.put(eliminar, true);

        nodos = new ArrayList<>();
        reemplazarNodosDeGrid(nodosConEspacios.keySet().toArray(new Node[0]),
                nodosConEspacios.values().toArray(new Boolean[0]));
    }

    public void agregarCliente() {
        long telefono = Long.parseLong(this.telefono.getText());
        try {
            Main.conseguirDatos().addCliente(nombre.getText(), apellido.getText(), telefono);
            Controller.mostrarInfo("Cliente agregado correctamente.");
            controller.consultaTabla(nombreDeLaTabla);
        } catch (SQLException t) {
            Controller.mostrarError("Error al agregar al cliente.\n\n" + t.getMessage());
        }
    }

    public void eliminarCliente() {
        int id = Integer.parseInt(this.id.getText());
        ButtonType resultado = Controller.mostrarConfirmacion("¿Desea eliminar al cliente?");
        if (resultado == ButtonType.OK) {
            try {
                Main.conseguirDatos().deleteCliente(id);
                Controller.mostrarInfo("Cliente eliminado correctamente.");
                controller.consultaTabla(nombreDeLaTabla);
            } catch (SQLException throwables) {
                Controller.mostrarError("Error al eliminar al cliente.\n\n" + throwables.getMessage());
            }
        }
    }

    public void editar(ArrayList<String> propiedades, int columna) {
        int id = Integer.parseInt(propiedades.get(0));
        long telefono;
        try {
            telefono = Long.parseLong(propiedades.get(3));
        } catch (NumberFormatException e) {
            Controller.mostrarError("Porfavor ingrese un número de teléfono válido.");
            controller.refrescarTabla();
            return;
        }

        try {
            Main.conseguirDatos().editCliente(id, propiedades.get(1), propiedades.get(2), telefono, new int[]{columna});
        } catch (SQLException throwables) {
            Controller.mostrarError("Algo salió mal al editar al cliente.\n\n" + throwables.getMessage());
            controller.refrescarTabla();
        }
    }
}
