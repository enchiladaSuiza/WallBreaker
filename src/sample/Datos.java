package sample;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Datos {
    private final HashMap<String, Integer> numcols;
    private final ConexionBase obj;
    private final Connection conexion;

    /**
     * Crea objeto para interactuar con la base de datos
     * @param user  usuario MySQL
     * @param pswrd contraseña de usuario MySQL
     * @throws SQLException posible excepción SQL
     */
    Datos(String user, String pswrd) throws SQLException {
        obj = new ConexionBase(user, pswrd);
        conexion = obj.getCNX();
        numcols = new HashMap<>();
        {
            numcols.put("analisis", 3);
            numcols.put("categoria", 3);
            numcols.put("cliente", 4);
            numcols.put("pedido", 6);
            numcols.put("pedido_producto", 4);
            numcols.put("personal", 6);
            numcols.put("producto", 5);
            numcols.put("proveedor", 6);
            numcols.put("venta", 5);
        }
    }

    /**
     * Método que regresa los registros completos de determinada tabla
     * @param tabla nombre de la tabla que se desea consultar
     * @return Devuelve un ArrayList con los registros. Campos separados por comas. Lineas terminadas por saltos.
     * @throws SQLException posible excepción SQL
     */
    public ArrayList<String> verTodo(String tabla) throws SQLException {
        ArrayList<String> resutados = new ArrayList<>();
        String instruccion = "select * from " + tabla;
        StringBuilder s;

        Statement st = conexion.createStatement();
        ResultSet rs = st.executeQuery(instruccion);

        while (rs.next()) {
            s = new StringBuilder();
            for (int v = 1; v <= numcols.get(tabla.toLowerCase()); ++v) s.append(rs.getString(v)).append(",");
            s.deleteCharAt(s.lastIndexOf(","));
            resutados.add(new String(s));
        }

        rs.close();
        st.close();
        return resutados;
    }

    /**
     * Método para agregar un producto a la base de datos
     * @param producto nombre del producto a agregar
     * @param precio precio del producto a agregar
     * @param almacen cantidad almacenada del producto a agregar
     * @param categoria categoria del producto a agregar
     * @throws SQLException posible excepción SQL
     */
    public void addProduct(String producto, double precio, int almacen, int categoria) throws SQLException {
        PreparedStatement ps;
        StringBuilder x = new StringBuilder("insert into producto (nomProd, precio, almacen, id_categoria)");
        x.append(" values (?, ?, ?, ?)");

        ps = conexion.prepareStatement(new String(x));
        ps.setString(1, producto);
        ps.setString(2, String.format("%.1f", precio));
        ps.setInt(3, almacen);
        ps.setInt(4, categoria);

        ps.execute();
        ps.close();
    }

    /**
     * Método para eliminar un producto de la base de datos
     * @param idProducto clave del producto a ser eliminado
     * @throws SQLException posible excepción SQL
     */
    public void deleteProduct(int idProducto) throws SQLException {
        Statement st = conexion.createStatement();
        StringBuilder x = new StringBuilder("delete from producto");
        x.append(" where id_producto = ").append(idProducto);

        st.executeUpdate(new String(x));
        st.close();
    }


    /**
     * Cierra la conexión con el servidor de MySQL
     * Siempre llamar este método antes de terminar el método main
     * @throws SQLException posible excepción SQL
     */
    public void end() throws SQLException {
        obj.closeCNX();
    }

}
