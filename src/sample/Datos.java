package sample;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

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
            numcols.put("pedido", 7);
            numcols.put("pedido_producto", 4);
            numcols.put("personal", 6);
            numcols.put("producto", 5);
            numcols.put("proveedor", 6);
            numcols.put("venta", 7);
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
     * @return Devuelve 0 si se agregó correctamente el producto
     * @throws SQLException posible excepción SQL<p>Excepción al tratar de agregar producto</p>
     */
    public int addProduct(String producto, double precio, int almacen, int categoria) throws SQLException {
        PreparedStatement ps;
        StringBuilder x = new StringBuilder("insert into producto (nomProd, precio, almacen, id_categoria)");
        x.append(" values (?, ?, ?, ?)");

        // Se prepara el producto
        ps = conexion.prepareStatement(new String(x));
        ps.setString(1, producto);
        ps.setString(2, String.format("%.1f", precio));
        ps.setInt(3, almacen);
        ps.setInt(4, categoria);

        ps.execute();
        ps.close();
        return 0;
    }

    /**
     * Método para eliminar un producto de la base de datos
     * @param idProducto clave del producto a ser eliminado
     * @return Devuelve 0 si el producto se eliminó correctamente
     * @throws SQLException posible excepción SQL<p>Excepción al tratar de eliminar producto</p>
     */
    public int deleteProduct(int idProducto) throws SQLException {
        Statement st = conexion.createStatement();
        StringBuilder x = new StringBuilder("delete from producto");
        x.append(" where id_producto = ").append(idProducto);

        st.executeUpdate(new String(x));
        st.close();
        return 0;
    }

    /**
     * Método que agrega un pedido a la base de datos
     * @param idProducto producto que se aparta para vender
     * @param cantidadProd número de productos que se apartarán
     * @param idCliente cliente que realiza el pedido
     * @return Devuelve -1 si faltan artículos en almacén<p>Devuelve 0 si la transacción salio bien</p>
     * @throws SQLException posible excepción SQL<p>Excepción al tratar de agreagr pedido</p>
     */
    public int addPedido(int idProducto, int cantidadProd, int idCliente) throws SQLException {
        PreparedStatement ps;
        Statement st;
        ResultSet rs;

        StringBuilder queryProd = new StringBuilder("select * from producto where id_producto = ").append(idProducto);
        StringBuilder requestProd = new StringBuilder();
        String[] producto;

        StringBuilder pedido = new StringBuilder("insert into pedido (fecha_pedido, monto, descuento, montoF, id_cliente, status)");
        pedido.append(" values (?, ?, ?, ?, ?, ?)");

        StringBuilder ped_prod = new StringBuilder("insert into pedido_producto (id_pedido, id_producto, cantidad)");
        ped_prod.append(" values (?, ?, ?)");

        StringBuilder idPedido = new StringBuilder("select auto_increment from information_schema.tables");
        idPedido.append(" where table_schema = 'wallbreaker' AND table_name = 'pedido' ");

        StringBuilder updateProd = new StringBuilder("update producto set almacen = ");

        // Se obtiene información del producto
        st = conexion.createStatement();
        rs = st.executeQuery(new String(queryProd));

        while (rs.next()) {
            for (int i = 1; i <= numcols.get("producto"); ++i) requestProd.append(rs.getString(i)).append(",");
            requestProd.deleteCharAt(requestProd.lastIndexOf(","));
        }
        producto = new String(requestProd).split(",");
        rs.close();
        st.close();

        // Falta de productos para el pedido
        if (Integer.parseInt(producto[3]) < cantidadProd) return -1; // Faltan productos en almacén

        // Informacion del pedido
        Random r = new Random();
        double monto = Double.parseDouble(producto[2]) * cantidadProd;
        double descuento = (r.nextInt(21) / 100.0);
        double montoF = monto - (monto * descuento);

        // Se prepara el pedido
        ps = conexion.prepareStatement(new String(pedido)); // Conecta el objeto PreparedStatement
        ps.setDate(1, new java.sql.Date(System.currentTimeMillis())); // fecha_pedido
        ps.setString(2, String.format("%.1f", monto)); // monto
        ps.setString(3, String.format("%.2f", descuento)); // descuento
        ps.setString(4, String.format("%.1f", montoF)); // montoF
        ps.setInt(5, idCliente); // id_cliente
        ps.setNull(6, Types.NULL); // status = NULL because is not sold yet

        ps.execute(); // Guarda el pedido en la BASE DE DATOS
        ps.close(); // Cierra el objeto PreparedStatement

        // Se obtiene el id_pedido generado
        st = conexion.createStatement();
        rs = st.executeQuery(new String(idPedido));
        int id_ped = 0;
        while (rs.next()) {
            id_ped = Integer.parseInt(rs.getString("AUTO_INCREMENT"));
        }

        rs.close();
        st.close();

        // Se prepara la relación pedido_producto
        ps = conexion.prepareStatement(new String(ped_prod)); // Conecta el objeto PreparedStatement
        ps.setInt(1, id_ped); // id_pedido recien gusrdado en la BASE DE DATOS
        ps.setInt(2, idProducto); // id_producto
        ps.setInt(3, cantidadProd); // cantidad

        ps.execute(); // Guarda la relación pedido_producto en la BASE DE DATOS
        ps.close(); // Cierra el objeto PreparedStatement

        // Actualizar almacén el tabla producto
        st = conexion.createStatement();
        int newAlmacen = Integer.parseInt(producto[3]) - cantidadProd;
        updateProd.append(newAlmacen).append(" where id_producto = ").append(producto[0]);
        st.executeUpdate(new String(updateProd)); // Nuevo número de productos almacenados tras el pedido
        st.close();

        return 0; // Everything OK
    }

    /**
     * Método que realiza la venta de un pedido previo
     * @param idPedido clave del pedido a ser vendido
     * @param efectivo cantidad con la que se paga el montoF
     * @return Devuelve null si el pedido ya fue vendido antes<p>Devuelve -1.0 si el efectivo no cubre el montoF</p><p>Devuelve un double con el cambio</p>
     * @throws SQLException posible excepción SQL<p>Excepción al tratar de realizar venta</p>
     */
    public Object generarVenta(int idPedido, double efectivo) throws SQLException {
        PreparedStatement ps;
        Statement st;
        StringBuilder s = new StringBuilder();
        String[] pedido;
        double cambio;

        StringBuilder x = new StringBuilder("select * from pedido where id_pedido = ").append(idPedido);
        StringBuilder y = new StringBuilder("update pedido set status = 1 where id_pedido = ").append(idPedido);

        // Se obtiene el pedido
        st = conexion.createStatement();
        ResultSet rs = st.executeQuery(new String(x));

        while (rs.next()) {
            for (int i = 1; i <= numcols.get("pedido"); ++i) s.append(rs.getString(i)).append(",");
            s.deleteCharAt(s.lastIndexOf(","));
        }

        rs.close();
        st.close();

        pedido = new String(s).split(",");
        if (pedido[pedido.length-1].equals("1")) return null; // Venta ya realizada previamente
        if (efectivo < Double.parseDouble(pedido[4])) return -1.0D; // Efectivo no suficiente para cubrir montoF
        cambio = efectivo - Double.parseDouble(pedido[4]);

        // Se prepara la venta
        StringBuilder z = new StringBuilder("insert into venta (fecha_venta, montoF, efectivo, cambio, id_cliente, id_pedido)");
        z.append("values (?, ?, ?, ?, ?, ?)");

        ps = conexion.prepareStatement(new String(z));
        ps.setDate(1, new java.sql.Date(System.currentTimeMillis())); // fecha_venta
        ps.setString(2, pedido[4]); // montoF
        ps.setString(3, String.format("%.1f", efectivo)); // efectivo
        ps.setString(4, String.format("%.1f", cambio)); // cambio
        ps.setString(5, pedido[5]);// id_cliente
        ps.setString(6, pedido[0]);// id_pedido

        // Se realiza la venta
        ps.execute();
        ps.close();

        // Actualiza estado del pedido
        st = conexion.createStatement();
        st.executeUpdate(new String(y));
        st.close();

        return cambio;
    }

    /**
     * Método que obtiene información de todos los proveedores que proveen productos
     * @return Un ArrayList cn la información solicitada
     * @throws SQLException posible excepción SQL<p>Excepción al solicitar la información</p>
     */
    public ArrayList<String> proveedor() throws SQLException {
        Statement st;
        ResultSet rs;
        StringBuilder reg;
        ArrayList<String> resultados = new ArrayList<>();

        StringBuilder queryProveedores = new StringBuilder();
        queryProveedores.append("select nomProveedor, apelProveedor, telefonoProveedor, pag_web, nomProd, almacen");
        queryProveedores.append(" from proveedor, producto where proveedor.id_producto = producto.id_producto");

        // Obtener información de todos los proveedores que proveen productos
        st = conexion.createStatement();
        rs = st.executeQuery(new String(queryProveedores));

        while (rs.next()) {
            reg = new StringBuilder();

            for (int p = 1; p <= 6; ++p) reg.append(rs.getString(p)).append(",");
            reg.deleteCharAt(reg.lastIndexOf(","));

            resultados.add(new String(reg));
        }

        rs.close();
        st.close();
        return resultados;
    }

    /**
     * Método que obtiene información del proveedor que provee el producto especificado
     * @param idProducto clave del producto para ver su proveedor
     * @return Una cadena con la información solicitada
     * @throws SQLException posible excepción SQL<p>Excepción al solicitar la información</p>
     */
    public String proveedor(int idProducto) throws SQLException {
        Statement st;
        ResultSet rs;
        StringBuilder resultado = new StringBuilder();

        StringBuilder query = new StringBuilder();
        query.append("select nomProveedor, apelProveedor, telefonoProveedor, pag_web, nomProd, almacen");
        query.append(" from proveedor, producto where proveedor.id_producto = producto.id_producto");
        query.append(" AND producto.id_producto = ").append(idProducto);

        // Obtener información de los proveedores que preveen los productos especificados
        st = conexion.createStatement();
        rs = st.executeQuery(new String(query));

        while (rs.next()) {
            for (int v = 1; v <= 6; ++v) resultado.append(rs.getString(v)).append(",");
            resultado.deleteCharAt(resultado.lastIndexOf(","));
        }

        rs.close();
        st.close();
        return new String(resultado);
    }

    /**
     * Método que agrega un proveedor a la base de datos<p>Si se pasa por parámetro una clave no existente en la BD
     * en idProd, se agregará un valor nulo en su lugar</p><p>Se tendrá que modificar el registro de éste proveedor
     * cuando el producto sea agregado a la BD</p>
     * @param nomProv nombre del proveedor (Nullable en la BD)
     * @param apelProv apellido del proveedor
     * @param telProv telefono del proveedor (long)
     * @param pagWeb página web del proveedor (Nullable en la BD)
     * @param idProd clave del producto que se provee (Nullable en la BD)
     * @param setNull booleano para indicar que el id_producto es null
     * @return Devuelve 0 si salio bien la operación
     * @throws SQLException posible excepción SQL<p>Excepción al tratar de agreagr un proveeedor</p>
     */
    public int addProveedor(String nomProv, String apelProv, long telProv, String pagWeb, int idProd, boolean setNull)
            throws SQLException
    {
        Statement st;
        ResultSet rs;
        PreparedStatement ps;
        ArrayList<Integer> products = new ArrayList<>();
        boolean id_prod = false; // id_producto será una clave de producto existente

        String queryProd = "select id_producto from producto";
        StringBuilder p = new StringBuilder("insert into proveedor (nomProveedor, apelProveedor, telefonoProveedor");

        // Se obtiene información de los productos existentes
        st = conexion.createStatement();
        rs = st.executeQuery(queryProd);

        while (rs.next()) {
            products.add(rs.getInt("id_producto"));
        }

        rs.close();
        st.close();

        // Verifica si se agrega null al id_producto
        if (setNull) id_prod = true; // id_producto será una clave de producto null

        // Verifica que el producto que provee el proveedor existe en la base de datos
        if (!products.contains(idProd)) id_prod = true; // No existe el producto que se provee por lo que se asignará null


        // Se prepara el proveedor
        p.append(", pag_web, id_producto)");
        p.append(" values (?, ?, ?, ?, ?)");

        ps = conexion.prepareStatement(new String(p));
        ps.setString(1, nomProv); // nomProveedor
        ps.setString(2, apelProv); // apelProveedor
        ps.setString(3, String.valueOf(telProv)); // telefonoProveedor
        ps.setString(4, pagWeb); // pag_web
        if (id_prod) {
            ps.setNull(5, Types.NULL); // id_producto
        } else {
            ps.setInt(5, idProd); // id_producto
        }

        // Se agrega el proveedor a la base de datos
        ps.execute();
        ps.close();
        return 0; // Everithing OK
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
