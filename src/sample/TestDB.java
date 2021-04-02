package sample;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class TestDB {

    public static void main(String[] args) throws SQLException {
        System.out.print("-> ");
        Datos test = new Datos("root", new Scanner(System.in).nextLine());

        // PROBAR MÉTODO **addPedido**

        test.end();
    }

    private static void show(Datos obj, String tabla) throws SQLException {
        ArrayList<String> x = obj.verTodo(tabla);

        System.out.println("\n" + tabla);
        x.forEach(v -> {
            String[] temp = v.split(",");
            for (String i: temp) System.out.printf("%20s", i);
            System.out.println();
        });
    }

}



        /* PRUEBA AGREGAR PRODUCTO
        show(test);
        test.addProduct("Tornillo", 1.5, 1000, 11);
        show(test);

        test.deleteProduct(15);
        show(test);
        */

        /* PRUEBA REALIZAR VENTA (MARCAR PEDIDO.STATUS = 1, AGREGAR REGISTRO A VENTA)
        show(test, "pedido");
        show(test, "venta");
        Object result = test.generarVenta(2, 200);

        if (result == null) System.out.println("\nPedido ya vendido, intente de nuevo\n");
        else if ((double) result != -1) System.out.println("Venta realizada. Cambio " + ((double) result));
        else System.out.println("\nEfectivo insuiciente\n");

        show(test, "pedido");
        show(test, "venta");
         */
