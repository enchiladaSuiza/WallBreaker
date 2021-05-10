package sample;

import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;

public class TestDB {

    public static void main(String[] args) throws SQLException {
        System.out.print("-> ");
        Datos test = new Datos("root", new Scanner(System.in).nextLine());

        /*int[] c = new int[]{1, 2, 3, 4, 5, 6, 7};
        ArrayList<Integer> oc = (ArrayList<Integer>) Arrays.stream(c).boxed().collect(Collectors.toList());
        System.out.println(oc);
        System.out.println(oc.contains(5));*/

        ObservableList<ObservableList<String>> ventasG = test.verVentas();
        ventasG.forEach(v -> {
            StringBuilder reg = new StringBuilder();
            v.forEach(w -> reg.append(w).append("\t\t"));
            System.out.println(new String(reg));
        });

        System.out.println("\n\n\n");

        ObservableList<ObservableList<String>> pedidosG = test.verPedidos();
        pedidosG.forEach(v -> {
            StringBuilder reg = new StringBuilder();
            v.forEach(w -> reg.append(w).append("\t\t"));
            System.out.println(new String(reg));
        });

        test.end();
    }

    /*private static void show(Datos obj, String tabla) throws SQLException {
        ArrayList<String> x = obj.verTodo(tabla);

        System.out.println("\n" + tabla);
        x.forEach(v -> {
            String[] temp = v.split(",");
            for (String i: temp) System.out.printf("%23s", i);
            System.out.println();
        });
    }*/

}



        /* PRUEBA AGREGAR PRODUCTO
        show(test);
        test.addProduct("Tornillo", 1.5, 1000, 11);
        show(test);
        */


        /* PRUEBA BORRAR PRODUCTO
        test.deleteProduct(15);
        show(test);
        */



        /* PRUEBA REALIZAR VENTA (MARCAR PEDIDO.STATUS = 1, AGREGAR REGISTRO A VENTA) CON CUANTO PAGA Y CAMBIO
        show(test, "pedido");
        show(test, "venta");
        Object result = test.generarVenta(21, 5000);

        if (result == null) System.out.println("\nPedido ya vendido, intente de nuevo\n");
        else if ((double) result != -1.0) System.out.println("Venta realizada. Cambio " + ((double) result));
        else System.out.println("\nEfectivo insuiciente\n");

        show(test, "pedido");
        show(test, "venta");
         */



        /* PRUEBA AGREGAR PEDIDO Y MODIFICAR LAS RESPECTIVAS TABLAS PRODUCTO Y PEDIDO_PRODUCTO
        show(test, "pedido");
        show(test, "producto");
        show(test, "pedido_producto");

        int worked = test.addPedido(12, 10, 9);

        if (worked == -1) System.out.println("\nPedido no realizado, faltan productos");
        else {
            System.out.println("\nPedido realizado con éxito\n");
            show(test, "pedido");
            show(test, "producto");
            show(test, "pedido_producto");
        }
         */
