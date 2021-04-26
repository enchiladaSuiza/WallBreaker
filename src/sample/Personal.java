package sample;

import javafx.scene.Node;
import javafx.scene.control.Button;
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
        System.out.println("Le funny string has not arrived");
    }

    private void eliminarPersonal() {
        System.out.println("Nunca impriman a consola en producción muchcachos");
    }
}
