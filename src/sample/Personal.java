package sample;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Personal extends ContenidoUI {
    private TextField nombre, apellido, ocupacion, telefono, salario, idPersonal;
    private Button agregar, eliminar;

    public Personal(Controller controller) {
        super(controller);
        nombreDeLaTabla = "personal";
        nombre = new TextField();
        apellido = new TextField();
        ocupacion = new TextField();
        telefono = new TextField();
        salario = new TextField();
        idPersonal = new TextField();
        Controller.prepararTextField(nombre, "Nombre", Controller.TEXTFIELD_CADENA);
        Controller.prepararTextField(apellido, "Apellido", Controller.TEXTFIELD_CADENA);
        Controller.prepararTextField(ocupacion, "Ocupación", Controller.TEXTFIELD_CADENA);
        Controller.prepararTextField(telefono, "Teléfono", Controller.TEXTFIELD_ENTERO);
        Controller.prepararTextField(salario, "Salario", Controller.TEXTFILED_FLOTANTE);
        Controller.prepararTextField(idPersonal, "Personal (ID)", Controller.TEXTFIELD_ENTERO);

        agregar = new Button("Agregar personal");
        eliminar = new Button ("Eliminar personal");
        agregar.setOnAction(e -> agregarPersonal());
        eliminar.setOnAction(e -> eliminarPersonal());

        // I hear Jackie Chan singing
        LinkedHashMap<Node, Boolean> nodosConEspacios = new LinkedHashMap<>();
        nodosConEspacios.put(nombre, true);
        nodosConEspacios.put(apellido, true);
        nodosConEspacios.put(ocupacion, true);
        nodosConEspacios.put(telefono, true);
        nodosConEspacios.put(salario, true);
        nodosConEspacios.put(agregar, true);
        nodosConEspacios.put(Controller.nuevoEspacio(agregar), true);
        nodosConEspacios.put(idPersonal, true);
        nodosConEspacios.put(eliminar, true);

        nodos = new ArrayList<>();
        reemplazarNodosDeGrid(nodosConEspacios.keySet().toArray(new Node[0]),
                nodosConEspacios.values().toArray(new Boolean[0]));
    }

    private void agregarPersonal() {
        String nombre = this.nombre.getText();
        String apellido = this.apellido.getText();
        String ocupacion = this.ocupacion.getText();
        String telefono = this.telefono.getText();
        String salario = this.salario.getText();
        if (nombre.isBlank() || apellido.isBlank() || ocupacion.isBlank() || telefono.isBlank() || salario.isBlank()) {
            Controller.mostrarError("Como mínimo se requieren valores para Apellido, Ocupación y Salario");
            return;
        }
        try {
            Main.conseguirDatos().addPersonal(nombre, apellido, ocupacion, Long.parseLong(telefono),
                    Double.parseDouble(salario));
            Controller.mostrarInfo("Personal añadido correctamente.");
            controller.consultaTabla(nombreDeLaTabla);
        } catch (Exception e) {
            Controller.mostrarError("Hubo un problema al añadir al personal.\n" + e.getMessage());
        }
    }

    private void eliminarPersonal() {
        String idPersonalCadena = this.idPersonal.getText();
        try {
            int idPersonalInt = Integer.parseInt(idPersonalCadena);
            ButtonType resultado = Controller.mostrarConfirmacion("¿Desea elminar a este personal?");
            if (resultado != ButtonType.OK) {
                return;
            }
            Main.conseguirDatos().deletePersonal(idPersonalInt);
            Controller.mostrarInfo("Personal eliminado exitosamente");
            controller.consultaTabla(nombreDeLaTabla);
        } catch (Exception e) {
            Controller.mostrarError("No fue posible eliminar al personal.\n" + e.getMessage());
        }
    }

    public void editar(ArrayList<String> propiedades, int columna) {
        int id = Integer.parseInt(propiedades.get(0));
        String nombre = propiedades.get(1);
        String apellido = propiedades.get(2);
        String ocupacion = propiedades.get(3);
        long telefono = 0;
        double salario = 0;

        try {
            telefono = Long.parseLong(propiedades.get(4));
        } catch (NumberFormatException e) {
            Controller.mostrarError("Por favor ingrese un teléfono válido.");
            controller.refrescarTabla();
            return;
        }

        try {
            salario = Double.parseDouble(propiedades.get(5));
        } catch (NumberFormatException e) {
            Controller.mostrarError("Por favor ingrese un monto salarial válido");
            controller.refrescarTabla();
            return;
        }

        try {
            Main.conseguirDatos().editPersonal(id, nombre, apellido, ocupacion, telefono, salario, new int[]{columna});
        } catch (SQLException e) {
            Controller.mostrarError("Algo salió mal al editar al personal.\n" + e.getMessage());
            controller.refrescarTabla();
        }
    }
}
