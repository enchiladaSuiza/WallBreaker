package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;

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
            numcols.put("proveedor", 5);
            numcols.put("proveedor_producto", 3);
            numcols.put("venta", 7);
        }
    }

    /**
     * Método que regresa los registros completos de determinada tabla
     * @param tabla nombre de la tabla que se desea consultar
     * @return Devuelve un ObservableList de ObservableList de Strings, cada ObservableList es una fila, cada String es
     * un registro. La priemra fila contiene los nombres de las columnas.
     * @throws SQLException posible excepción SQL
     */
    public ObservableList<ObservableList<String>> verTodo(String tabla) throws SQLException {
        ObservableList<ObservableList<String>> resultados = FXCollections.observableArrayList();
        String instruccion = "select * from " + tabla;
        // StringBuilder s;

        Statement st = conexion.createStatement();
        ResultSet rs = st.executeQuery(instruccion);
        ResultSetMetaData md = rs.getMetaData();
        int cols = md.getColumnCount();

        resultados.add(FXCollections.observableArrayList()); // La primera fila serán los nombres de las columnas
        for (int i = 1; i <= cols; i++) {
            resultados.get(0).add(md.getColumnLabel(i));
        }

        int indiceFila = 0;
        while (rs.next()) {
            resultados.add(FXCollections.observableArrayList()); // Añade una tupla
            indiceFila++;
            for (int v = 1; v <= cols; ++v) {
                resultados.get(indiceFila).add(rs.getString(v)); // Añade un registro
            }
        }

        rs.close();
        st.close();
        return resultados;
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
     * Método que edita el nombre, precio, almacen y categoria de un producto específico
     * @param idProd Llave primaria del producto a ser modificado
     * @param nomProd String con el nuevo nombre del producto
     * @param precio Double con el nuevo precio del producto
     * @param almac Entero con el nuevo valor de la cantidad del producto
     * @param idCateg Entero que representa la categoría del producto
     * @param toModify Arreglo de enteros con el número de la(s) columnas a modificar (máximo 4)
     *                 <li>1 = nomProd</li>
     *                 <li>2 = precio</li>
     *                 <li>3 = almacen</li>
     *                 <li>4 = id_categoria</li>
     * @return Devuelve 0 si la operación se realizó exitosamente
     * @throws SQLException posible excepción SQL<p>Excepción al tratar de editar un producto</p>
     */
    public int editProduct(int idProd, String nomProd, double precio, int almac, int idCateg, int[] toModify) throws SQLException {
        Statement st;
        StringBuilder updNom = new StringBuilder("update producto");
        StringBuilder updPre = new StringBuilder("update producto");
        StringBuilder updAlm = new StringBuilder("update producto");
        StringBuilder updCat = new StringBuilder("update producto");

        updNom.append(" set nomProd = '").append(nomProd).append("' where id_producto = ").append(idProd);
        updPre.append(" set precio = ").append(String.format("%.1f", precio)).append(" where id_producto = ").append(idProd);
        updAlm.append(" set almacen = ").append(almac).append(" where id_producto = ").append(idProd);
        updCat.append(" set id_categoria = ").append(idCateg).append(" where id_producto = ").append(idProd);

        StringBuilder[] toM = new StringBuilder[] {updNom, updPre, updAlm, updCat};

        st = conexion.createStatement();
        for (int i : toModify) {
            st.executeUpdate(new String(toM[(i - 1)]));
        }
        st.close();
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
     * Método que realiza la venta de un pedido previo
     * <p>Este método se puede llamar para pagar un pedido realizado anteriormente enviando como parámetros el (id_pedido)
     * y el <b>efectivo suficiente ó</b> justo despues de llamar el método generarTotalVenta() el cual devuelve ya el
     * (id_pedido) que se debe enviar por párametro, más el <b>efectivo suficente</b></p>
     * @param idPedido clave del pedido a ser vendido
     * @param efectivo cantidad con la que se paga el montoF
     * @return Devuelve null si el pedido ya fue vendido antes<p>Devuelve -1.0 si el efectivo no cubre el montoF</p>
     * <p>Devuelve un double con el cambio</p><p><b>NOTA: </b>Sí se devuelve -1.0D significa que el efectivo enviado por
     * parámetro no cubre el monto del pedido; por consiguiente se necesita llamar de nuevo éste método para completar
     * la venta</p>
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
        cambio = efectivo - Double.parseDouble(pedido[4]); // Cambio cliente

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
     * Método que se llama cuando en la pestaña venta se seleccionan los diferentes productos (id_producto) y
     * sus cantidades para ser vendidos en ese mismo momento y se requiere el total a pagar (montoF).
     * <p>Este método se llama <b>antes</b> del método generarVenta() ya que se necesita el (id_pedido) como parámetro
     * que devuelve este método</p>
     * @param idClien Entero que representa el id_cliente
     * @param prods_cant ArrayList de objtos Pair que contienen la relación de los productos y la cantidad que se piden<p>
     *                   Pair<id_producto, cantidad>
     *  </p>
     * @return Un objeto Pair que contiene el (id_pedido, generado por los prods que solicita en el momento y que se
     * necesitará para realizar la venta) y un double que representa el total a pagar por el cliente
     * @throws SQLException posible excepción SQL<p>Excepción al tratar de generar el total de una venta</p>
     */
    public Pair<Integer, Double> generarTotalVenta(int idClien, ArrayList<Pair<Integer, Integer>> prods_cant) throws SQLException {
        Statement st;
        ResultSet rs;
        int id_ped = 0;
        double tot = 0;

        // AGREGA UN PEDIDO CON LA INFORMACIÓN ENTRANTE
        addPedido(prods_cant, idClien);


        // SELECT ÚLTIMO id_pedido
        StringBuilder idPedido = new StringBuilder("select auto_increment from information_schema.tables");
        idPedido.append(" where table_schema = 'wallbreaker' AND table_name = 'pedido' ");

        // SELECT montoF DEL ÚLTIMO PEDIDO
        StringBuilder montoF = new StringBuilder("select montoF from pedido");
        montoF.append(" where id_pedido = ");


        // SE OBTIENE EL id_pedido GENERADO
        st = conexion.createStatement();
        rs = st.executeQuery(new String(idPedido));
        while (rs.next()) {
            id_ped = Integer.parseInt(rs.getString("AUTO_INCREMENT"));
        }
        montoF.append(id_ped);
        rs.close();
        st.close();


        // OBTIENE montoF DEL PEDIDO REALIZADO
        st = conexion.createStatement();
        rs = st.executeQuery(new String(montoF));
        while (rs.next()) {
            tot = Double.parseDouble(rs.getString("AUTO_INCREMENT"));
        }
        rs.close();
        st.close();

        return new Pair<>(id_ped, tot);
    }

    public int editVenta(int idVenta, double efectivo, int idCliente, int idPedido) {
        // TODO completar método

        return 0;
    }

    /**
     * Método que obtine la información de los productos que conformarán el pedido
     * @param prods_cant ArrayList de objtos Pair que contienen la relación de los productos y la cantidad que se piden<p>
     *                   Pair<\id_producto, cantidad>
     * </p>
     * @return Devuelve un ArrayList de arrgelos de String donde cada arreglo contiene la información de las
     *         columnas de cada producto del pedido
     * @throws SQLException posible excepción SQL<p>Excepción al tratar de obtener info de productos</p>
     */
    private ArrayList<String[]> infoProds(ArrayList<Pair<Integer, Integer>> prods_cant) throws SQLException {
        Statement st;
        ResultSet rs;
        ArrayList<String[]> infoProds = new ArrayList<>();
        StringBuilder queryProd = new StringBuilder("select * from producto where id_producto = ");
        StringBuilder registro;

        st = conexion.createStatement();
        for (Pair<Integer, Integer> p : prods_cant) {
            queryProd.append(p.getKey());
            rs = st.executeQuery(new String(queryProd));

            while (rs.next()) {
                registro = new StringBuilder();
                for (int i = 1; i <= numcols.get("producto"); ++i) registro.append(rs.getString(i)).append(",");
                registro.deleteCharAt(registro.lastIndexOf(","));
                infoProds.add(new String(registro).split(","));
            }
        }
        st.close();

        return infoProds;
    }

    /**
     * Método que agrega un pedido a la base de datos
     * @param prods_cant ArrayList de objtos Pair que contienen la relación de los productos y la cantidad que se piden<p>
     *                   Pair<id_producto, cantidad>
     * </p>
     * @param idCliente Entero con el ID de cliente que realiza el pedido
     * @return Devuelve 0 si la transacción salio bien<p>Si la cantidad de productos en almacen no es suficinte para
     *         realizar el pedido se asignara 0</p>
     * @throws SQLException posible excepción SQL<p>Excepción al tratar de agreagr pedido</p>
     */
    public int addPedido(ArrayList<Pair<Integer, Integer>> prods_cant, int idCliente) throws SQLException {
        PreparedStatement ps;
        Statement st;
        ResultSet rs;
        ArrayList<Boolean> outOfStock = new ArrayList<>(); // Banderas que indican si hay suficiente de un producto para un pedido

        // INSERT INTO PEDIDO
        StringBuilder pedido = new StringBuilder("insert into pedido (fecha_pedido, monto, descuento, montoF, id_cliente, status)");
        pedido.append(" values (?, ?, ?, ?, ?, ?)");

        // INSERT INTO PEDIDO_PRODUCTO
        StringBuilder ped_prod = new StringBuilder("insert into pedido_producto (id_pedido, id_producto, cantidad)");
        ped_prod.append(" values (?, ?, ?)");

        // SELECT ÚLTIMO id_pedido
        StringBuilder idPedido = new StringBuilder("select auto_increment from information_schema.tables");
        idPedido.append(" where table_schema = 'wallbreaker' AND table_name = 'pedido' ");

        // UPDATE almacen EN PRODUCTO
        StringBuilder updateProd = new StringBuilder("update producto set almacen = ");



        // INFO DE LOS PRODUCTOS PARA EL PEDIDO
        ArrayList<String[]> infoProds = infoProds(prods_cant);



        // INFORMACIÓN DEL PEDIDO
        Random r = new Random();
        ArrayList<Integer> cantPerProd = new ArrayList<>(); // Cantidad pedida de cada producto
        double monto = 0;
        double descuento = (r.nextInt(21) / 100.0);
        double montoF;
        int i = 0, j = 0; // Indice cantPerProd

        for (Pair<Integer, Integer> p : prods_cant) {
            cantPerProd.add(p.getValue()); // Cantidades de cada producto pedido
        }

        for (String[] b : infoProds) {
            outOfStock.add(cantPerProd.get(j) <= Integer.parseInt(b[3])); // Evalua si hay suficiente stock para el pedido
            ++j;
        }

        for (String[] p : infoProds) {
            if (outOfStock.get(i)) { // Alcanzan los productos en almacen
                monto += Double.parseDouble(p[2]) * cantPerProd.get(i); // monto por todos los productos pedidos
            } else { // NO alcanzan los productos en almacen
                monto += Double.parseDouble(p[2]) * Integer.parseInt(p[3]); // monto el máx de prods en almacen
                cantPerProd.set(i, Integer.parseInt(p[3])); // Actualiza la cant de prods por el que se le cobra
            }
            ++i;
        }
        montoF = monto - (monto * descuento); // monto final del pedido



        // PREPARACIÓN DEL PEDIDO
        ps = conexion.prepareStatement(new String(pedido)); // Conecta el objeto PreparedStatement
        ps.setDate(1, new java.sql.Date(System.currentTimeMillis())); // fecha_pedido
        ps.setString(2, String.format("%.1f", monto)); // monto
        ps.setString(3, String.format("%.2f", descuento)); // descuento
        ps.setString(4, String.format("%.1f", montoF)); // montoF
        ps.setInt(5, idCliente); // id_cliente
        ps.setNull(6, Types.NULL); // status = NULL because is not sold yet

        ps.execute(); // Guarda el pedido en la BASE DE DATOS
        ps.close(); // Cierra el objeto PreparedStatement



        // SE OBTIENE EL id_pedido GENERADO
        st = conexion.createStatement();
        rs = st.executeQuery(new String(idPedido));
        int id_ped = 0;
        while (rs.next()) {
            id_ped = Integer.parseInt(rs.getString("AUTO_INCREMENT"));
        }
        rs.close();
        st.close();



        // SE PREPARA LA RELACIÓN pedido_producto
        for (int v = 0; v < infoProds.size(); ++v) {
            ps = conexion.prepareStatement(new String(ped_prod)); // Conecta el objeto PreparedStatement

            // infoProds, cantPerProd
            int id_producto = Integer.parseInt(infoProds.get(v)[0]);
            int cant = cantPerProd.get(v);

            ps.setInt(1, id_ped); // id_pedido recien guardado en la BASE DE DATOS
            ps.setInt(2, id_producto); // id_producto
            ps.setInt(3, cant); // cantidad

            ps.execute(); // Guarda la relación pedido_producto en la BASE DE DATOS
            ps.close(); // Cierra el objeto PreparedStatement
        }



        // ACTUALIZAR almacen EN TABLA producto
        for (int w = 0; w < infoProds.size(); ++w) {
            st = conexion.createStatement();

            int newAlmacen = Integer.parseInt(infoProds.get(w)[3]) - cantPerProd.get(w);

            updateProd.append(newAlmacen).append(" where id_producto = ").append(infoProds.get(w)[0]);
            st.executeUpdate(new String(updateProd)); // Nuevo número de productos almacenados tras el pedido
            st.close();
        }

        return 0; // Everything OK
    }

    public int editPedido(int idClien, int idPed, ArrayList<Pair<Integer, Integer>> prods_cant) {
        // TODO completar método

        return 0;
    }

    /**
     * Método que obtiene información de todos los proveedores que proveen productos
     * @return Un ArrayList cn la información solicitada
     * @throws SQLException posible excepción SQL<p>Excepción al solicitar la información</p>
     */ // TODO Actualizar querys con la tabla proveedor_producto
    public ObservableList<ObservableList<String>> proveedor() throws SQLException {
        Statement st;
        ResultSet rs;
        ResultSetMetaData md;
        ObservableList<ObservableList<String>> resultados = FXCollections.observableArrayList();

        StringBuilder queryProveedores = new StringBuilder();
        queryProveedores.append("select nomProveedor, apelProveedor, telefonoProveedor, pag_web, nomProd, almacen");
        queryProveedores.append(" from proveedor, producto where proveedor.id_producto = producto.id_producto");

        // Obtener información de todos los proveedores que proveen productos
        st = conexion.createStatement();
        rs = st.executeQuery(new String(queryProveedores));
        md = rs.getMetaData();
        int columnas = md.getColumnCount();
        resultados.add(FXCollections.observableArrayList()); // Nombres de columnas
        for (int i = 1; i <= columnas; i++) {
            resultados.get(0).add(md.getColumnLabel(i));
        }

        int indiceFila = 0;
        while (rs.next()) {
            resultados.add(FXCollections.observableArrayList());
            indiceFila++;
            for (int p = 1; p <= 6; ++p) {
                resultados.get(indiceFila).add(rs.getString(p));
            }
            // reg.deleteCharAt(reg.lastIndexOf(","));
            // resultados.add(new String(reg));
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
     */ // TODO Actualizar querys con la tabla proveedor_producto
    public ObservableList<ObservableList<String>> proveedor(int idProducto) throws SQLException {
        Statement st;
        ResultSet rs;
        ResultSetMetaData md;
        ObservableList<ObservableList<String>> resultados = FXCollections.observableArrayList();

        StringBuilder query = new StringBuilder();
        query.append("select nomProveedor, apelProveedor, telefonoProveedor, pag_web, nomProd, almacen");
        query.append(" from proveedor, producto where proveedor.id_producto = producto.id_producto");
        query.append(" AND producto.id_producto = ").append(idProducto);

        // Obtener información de los proveedores que preveen los productos especificados
        st = conexion.createStatement();
        rs = st.executeQuery(new String(query));
        md = rs.getMetaData();
        int columnas = md.getColumnCount();
        resultados.add(FXCollections.observableArrayList()); // Nombres de columnas
        for (int i = 1; i <= columnas; i++) {
            resultados.get(0).add(md.getColumnLabel(i));
        }

        int indiceFila = 0;
        while (rs.next()) {
            resultados.add(FXCollections.observableArrayList());
            indiceFila++;
            for (int v = 1; v <= 6; ++v) {
                resultados.get(indiceFila).add(rs.getString(v));
            }
            // resultado.deleteCharAt(resultado.lastIndexOf(","));
        }

        rs.close();
        st.close();
        return resultados;
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
     */ // TODO Actualizar querys con la tabla proveedor_producto
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

    public int editProveedor(int idProv, String nom, String ape, long tel, String pw, ArrayList<Integer> prods) {
        // TODO completar método

        return 0;
    }

    public int deleteProveedor(int idProveedor) {
        // TODO completar método

        return 0;
    }

    public int addPersonal(String nom, String apel, String ocup, long tel, double sal) {
        // TODO completar método

        return 0;
    }

    public int editPersonal(int idPersnl, String nom, String apel, String ocup, long tel, double sal) {
        // TODO completar método

        return 0;
    }

    public int deletePersonal(int idPersnl) {
        // TODO completar método

        return 0;
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
