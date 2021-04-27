package sample;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;

import java.util.ArrayList;

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
        Controller.prepararTextField(nombre, "Nombre", false);
        Controller.prepararTextField(apellido, "Apellido", false);
        Controller.prepararTextField(ocupacion, "Ocupación", false);
        Controller.prepararTextField(telefono, "Teléfono", true);
        Controller.prepararTextField(salario, "Salario", true);
        Controller.prepararTextField(idPersonal, "Personal (ID)", true);

        agregar = new Button("Agregar");
        eliminar = new Button ("Eliminar");
        agregar.setOnAction(e -> agregarPersonal());
        eliminar.setOnAction(e -> eliminarPersonal());

        Node[] nodosArray = {nombre, apellido, ocupacion, telefono, salario,
                agregar, Controller.nuevoEspacio(agregar), idPersonal, eliminar};
        boolean[] espacios = {false, false, false, false, false, false, true, false, false};

        nodos = new ArrayList<>();
        reemplazarNodosDeGrid(nodosArray, espacios);
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
}
