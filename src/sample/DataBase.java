package sample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBase {
    private static final String url = "jdbc:mysql://localhost:3306/wallbreaker?serverTimezone=CST";
    private final Connection CNX;

    /**
     * Crea el objeto de conexión con la base de datos
     * @param user usuario MySQL
     * @param pswrd contraseña del usuario
     * @throws SQLException posible excepción SQL
     */
    DataBase(String user, String pswrd) throws SQLException {
        CNX = DriverManager.getConnection(url, user, pswrd);
    }

    /**
     * Devuelve el objeto de conexión creado
     * @return un objeto de tipo @code{Connection}
     */
    public Connection getCNX() {
        return CNX;
    }

    /**
     * Cierra la conexión con MySQL
     * @throws SQLException posible excepción SQL
     */
    public void closeCNX() throws SQLException {
        CNX.close();
    }
}
