package sample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * La clase ConexionBase provee métodos que establecen y terminan una conexión con la base de datos walbreaker en su
 * servidor local de MySQL
 * <p>Llamando al constructor de esta clase y enviandole su usuario MySQL junto con su contraseña de usuario por parámetros
 * generará una conexión y podra utilizar la aplicación y todas sus funcionalidades</p>
 * <p><b>Importante:</b> Antes de cerrar la aplicación se recomienda que si se creó o utilizó un objeto de esta clase
 * se llame al método {@link ConexionBase#closeCNX()} para cerrar la conexión utilizada en su servidor local de MySQL</p>
 * <p><b>Nota:</b> Para un correcto funcionamiento de la aplicación se recomienda usar la versión más reciente Java
 * igulmente junto a la versión más reciente del servidor MySQL</p>
 *
 * @see Datos
 * @since 16
 */
public class ConexionBase {
    private static final String url = "jdbc:mysql://localhost:3306/wallbreaker";
    private final Connection CNX;

    /**
     * Crea el objeto de conexión con la base de datos
     * @param user usuario MySQL
     * @param pswrd contraseña del usuario
     * @throws SQLException posible excepción SQL
     */
    ConexionBase(String user, String pswrd) throws SQLException {
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
