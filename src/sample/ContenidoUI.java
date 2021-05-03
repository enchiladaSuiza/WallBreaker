package sample;

import javafx.scene.Node;
import javafx.util.Pair;

import java.util.ArrayList;

import static sample.Controller.actualizarPosicionesEnGrid;

public class ContenidoUI {
    protected ArrayList<Pair<Node, Boolean>> nodos;
    protected Controller controller;
    protected String nombreDeLaTabla;

    public ArrayList<Pair<Node, Boolean>> conseguirNodos() { return nodos; }

    public ContenidoUI(Controller controller) {
        this.controller = controller;
    }

    /* ContenidoUI se refiere a la parte derecha de la ventana, donde están los botones, campos, etc. (nodos).
    * Todos estos componentes se acomodan en una Grid, que está en Controller, pero aquí se configura la posición que
    * tendrán. La Grid es de 2 columnas por las filas que sean necesarias. El ArrayList nodos tiene pares con todos
    * los nodos de la pestaña, más un valor booleano para cada uno que indica si ocupa dos espacios o no. He aquí lo
    * interesante. El método actualizarPosicionesEnGrid de Controller va poniendo los nodos de acuerdo a su posición
    * en el arraylist: la posición 0 está en la primera fila y en la primera columna , la 1 está en la primera fila
    * segunda columna, la 2 en la segunda fila primera columna y así. Si hay un nodo que ocupa dos posiciones se
    * incrementa una variable offset para saltarse un espacio. */
    protected void sumarAGrid(Node nodo, boolean dosEspacios) {
        nodos.add(new Pair<>(nodo, dosEspacios));
        controller.agregarNodoAGrid(nodo);
        actualizarPosicionesEnGrid(nodos);
    }

    protected void reemplazarNodosDeGrid(Node[] nodosArray, boolean[] espacios) {
        nodos.clear();
        if (nodosArray.length != espacios.length) {
            return;
        }
        for (int i = 0; i < nodosArray.length; i++) {
            nodos.add(new Pair<>(nodosArray[i], espacios[i]));
        }
        controller.limpiarYAgregarNodosAGrid(nodos);
        actualizarPosicionesEnGrid(nodos);
    }

    protected void insertarAGrid(Node nodo, boolean dosEspacios, int indice) {
        nodos.add(indice, new Pair<>(nodo, dosEspacios));
        controller.agregarNodoAGrid(nodo);
        actualizarPosicionesEnGrid(nodos);
    }

    protected void quitarDeGrid(Node nodo) {
        if (nodos.removeIf(par -> par.getKey().equals(nodo))) {
            controller.quitarNodoDeGrid(nodo);
            actualizarPosicionesEnGrid(nodos);
        }
    }

    protected int conseguirIndice(Node nodo) {
        for (int i = 0; i < nodos.size(); i++) {
            if (nodos.get(i).getKey().equals(nodo)) {
                return i;
            }
        }
        return -1;
    }

    public void editar(ArrayList<String> propiedaes, int columna) { }

    public String conseguirNombreDeLaTabla() { return nombreDeLaTabla; }
}
