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

    public String conseguirNombreDeLaTabla() { return nombreDeLaTabla; }
}
