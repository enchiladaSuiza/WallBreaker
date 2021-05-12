package sample;

import java.util.ArrayList;

public class Clientes extends ContenidoUI {

    public Clientes(Controller controller) {
        super(controller);
        nombreDeLaTabla = "cliente";
        nodos = new ArrayList<>();
    }
}
