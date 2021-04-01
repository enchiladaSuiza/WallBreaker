package sample;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class TestDB {

    public static void main(String[] args) throws SQLException {
        System.out.print("-> ");
        Datos test = new Datos("root", new Scanner(System.in).nextLine());

        show(test);
        test.addProduct("Tornillo", 1.5, 1000, 11);
        show(test);

        test.end();
    }

    private static void show(Datos obj) throws SQLException {
        ArrayList<String> x = obj.verTodo("producto");

        System.out.println("\nProducto");
        x.forEach(v -> {
            String[] temp = v.split(",");
            for (String i: temp) System.out.printf("%25s", i);
            System.out.println();
        });
    }

}
