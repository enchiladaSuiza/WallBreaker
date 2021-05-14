package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * La clase Datos contiene variedad de métodos que tienen utilidades en el manejo y administración de datos almacenados
 * en el servidor de MySQL en la base de datos wallbreaker
 * <p>Estos métodos realizan acciones CRUD ya que se pueden crear, leer, modificar o eliminar datos almacenados en la base</p>
 * <p><b>Importante:</b> Antes de cerrar la aplicación se recomienda que si se creó o utilizó un objeto de esta clase
 * se llame al método {@link Datos#end()} para cerrar la conexión utilizada en su servidor local de MySQL</p>
 *
 * @see ConexionBase
 * @since 16
 */
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
     * Método que consulta las categorías almacenadas en la base de datos junto con la cantidad de productos que
     * pertenecen a esa categoría
     * @return Devuelve un ObservableList de ObservableList de Strings, cada ObservableList es una fila, cada String es
     * un registro. La priemra fila contiene los nombres de las columnas.
     * @throws SQLException posible excepción SQL<p>Excepción al tratar de obtener la información</p>
     */
    public ObservableList<ObservableList<String>> verCategos() throws SQLException {
        Statement st;
        ResultSet rs;
        ArrayList<Integer> numCategos = new ArrayList<>();
        ArrayList<Integer> cant_prods = new ArrayList<>();

        ObservableList<ObservableList<String>> resultados = FXCollections.observableArrayList();

        StringBuilder numCategs = new StringBuilder("select id_categoria from categoria ");
        StringBuilder cuantosPdeCateg;
        StringBuilder verCategs = new StringBuilder("select id_categoria as ID, nomCategoria as Categoria, descripcion as Descripcion from categoria ");

        // OBTIENEN EL LOS ID DE CATEGORÍAS EN LA BASE DE DATOS
        st = conexion.createStatement();
        rs = st.executeQuery(new String(numCategs));
        while (rs.next()) numCategos.add(Integer.parseInt(rs.getString(1)));
        rs.close();
        st.close();

        // OBTIENE CUANTOS PRODUCTOS TIENE ESA CATAGORIA
        st = conexion.createStatement();
        for (int v : numCategos) {
            cuantosPdeCateg = new StringBuilder("select count(id_categoria) from producto where id_categoria = ");
            rs = st.executeQuery(new String(cuantosPdeCateg.append(v)));
            while (rs.next()) cant_prods.add(Integer.parseInt(rs.getString(1)));
            rs.close();
        }
        st.close();


        // CONSULTA FINAL
        st = conexion.createStatement();
        rs = st.executeQuery(new String(verCategs));
        ResultSetMetaData md = rs.getMetaData();
        int cols = md.getColumnCount();

        resultados.add(FXCollections.observableArrayList()); // La primera fila serán los nombres de las columnas
        // No entendí muy bien qué quisiste hacer aquí Gacchan, lo cambié, me daba bugs :(
        for (int i = 1; i <= cols; i++) {
            resultados.get(0).add(md.getColumnLabel(i));
        }
        resultados.get(0).add("# Productos");

        int indiceFila = 0;
        while (rs.next()) {
            resultados.add(FXCollections.observableArrayList()); // Añade una tupla
            indiceFila++;
            for (int v = 1; v <= cols; ++v) {
                resultados.get(indiceFila).add(rs.getString(v)); // Añade un registro
            }
            resultados.get(indiceFila).add(String.valueOf(cant_prods.get(indiceFila - 1))); // Añade un registro
        }
        rs.close();
        st.close();

        return resultados;
    }

    /**
     * Método que agrega una categoría de herramientas a la base de datos
     * @param categoria String con el nombre de la categoría nueva
     * @param descrip String con la descripción de la nueva categoría
     * @return Devuelve 0 si la operación salió con éxito
     * @throws SQLException posible excepción SQL<p>Excepción al tratar de agreagr una categoría</p>
     */
    public int addCategoria(String categoria, String descrip) throws SQLException {
        Statement st;

        // INSERT INTO categoria
        StringBuilder ins = new StringBuilder("insert into categoria (nomCategoria, descripcion) values ('");
        ins.append(categoria).append("', '").append(descrip).append("') ");

        // MAKE INSERT
        st = conexion.createStatement();
        st.executeUpdate(new String(ins));
        st.close();
        return 0;
    }

    /**
     * Método que actualiza los campos de un registro en la tabla categoria<p><b>Nota:</b> Si solo se desea editar el
     * nombre de categoría y no la descripción, en el atributo descrip se envia un null. Si solo se desea editar la
     * descripíon y no el nombre de categoría, en el atributo categoria se envia un null</p><p><b>Importante:</b> Ambos
     * atributos no pueden ser null, ya que no se editaría nada 😂</p>
     * @param idCategoria Entero que representa el ID de la gategoría a editar
     * @param categoria String con el nuevo nombre de la categoría
     * @param descrip String con la nueva descripción de la categoría
     * @return Devuelve un objeto Pair< String, Integer > con un string del mensaje de salida y el status (0 ó -1)
     * @throws SQLException posible excepción SQL<p>Excepción al tratar de editar una categoría</p>
     */
    public Pair<String, Integer> editCategoria(int idCategoria, String categoria, String descrip) throws SQLException {
        Statement st;

        // UPDATE REGISTRO EN TABLA categoria
        StringBuilder updCat = new StringBuilder("update categoria set nomCategoria = '").append(categoria);
        updCat.append("' where id_categoria = ").append(idCategoria);

        StringBuilder updDes = new StringBuilder("update categoria set descripcion = '").append(descrip);
        updDes.append("' where id_categoria = ").append(idCategoria);

        if (idCategoria == 12) return new Pair<>("Categoría no editable", -1);

        // VERIFICA CUAL EJECUTARÁ
        st = conexion.createStatement();
        if (Objects.isNull(categoria)) { // Actualiza solo descripción
            st.executeUpdate(new String(updDes));
        } else if (Objects.isNull(descrip)) { // Actualiza solo nomCategoria
            st.executeUpdate(new String(updCat));
        } else { // Actualiza nomCategoria y descripcion
            st.executeUpdate(new String(updCat));
            st.executeUpdate(new String(updDes));
        }
        st.close();
        return new Pair<>("Todo bien :)", 0);
    }

    /**
     * Método que elimina una categoría de la base de datos<p>Al eliminarse una categoría, los productos que pertenecían
     * a esta se les asignará el valor por default</p>
     * @param idCategoria Entero que representa el ID de la categoría a eliminar
     * @return Devuelve un objeto Pair< String, Integer > con un string del mensaje de salida y el status (0 ó -1)
     * @throws SQLException posible excepción SQL<p>Excepción al tratar de eliminar la categoría</p>
     */
    public Pair<String, Integer> deleteCategoria(int idCategoria) throws SQLException {
        // DELETE ONE
        StringBuilder del = new StringBuilder("delete from categoria where id_categoria = ").append(idCategoria).append(" limit 1 ");

        if (idCategoria == 12) return new Pair<>("Categoría no editable", -1);

        // BAI BAI
        Statement s = conexion.createStatement();
        s.executeUpdate(new String(del));
        s.close();
        return new Pair<>("Todo bien :)", 0);
    }

    /**
     * Método que obtiene la información de los clientes almacenados en la base de datos
     * @return Devuelve un ObservableList de ObservableList de Strings, cada ObservableList es una fila, cada String es
     * un registro. La priemra fila contiene los nombres de las columnas.
     * @throws SQLException posible excepción SQL<p>Excepción al tratar de obtener la información</p>
     */
    public ObservableList<ObservableList<String>> verClientes() throws SQLException {
        Statement st;
        ResultSet rs;

        ObservableList<ObservableList<String>> resultados = FXCollections.observableArrayList();

        StringBuilder query = new StringBuilder("select id_cliente as 'ID Cliente', nomCliente as Nombre, apelCliente as Apellido,");
        query.append(" telefonoCliente as Telefono from cliente");

        st = conexion.createStatement();
        rs = st.executeQuery(new String(query));

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
     * Método que agrega los datos de un cliente a la base de datos
     * @param nom String que almacena el nombre del cliente
     * @param apel String que almacena el apellido del cliente
     * @param tel Long que almacena el teléfono del cleinte
     * @return Devuelve 0 si la operación salió con éxito
     * @throws SQLException posible excepción SQL<p>Excepción al tratar de agregar un cliente a la base de datos</p>
     */
    public int addCliente(String nom, String apel, long tel) throws SQLException {
        PreparedStatement ps;

        // INSERT EN TABLA cliente
        StringBuilder ins = new StringBuilder("insert into cliente (nomCliente, apelCliente, telefonoCliente)");
        ins.append(" values (?, ?, ?) ");

        // SE PREPARA EL INSERT
        ps = conexion.prepareStatement(new String(ins));
        ps.setString(1, nom); // nomCliente
        ps.setString(2, apel); // apelCliente
        ps.setString(3, String.valueOf(tel)); // telefonoCliente

        ps.execute();
        ps.close();
        return 0;
    }

    /**
     * Método que actualiza los campos de un registro en la tabla cliente
     * @param idCliente Entero que representa el id_cliente a ser modificado
     * @param nom String con el nuevo nombre del cliente
     * @param ape String con el nuevo apellido del cliente
     * @param tel Long con el nuevo teléfono del cliente
     * @param toModify Arreglo de enteros con el número de la(s) columnas a modificar (máximo 3)
     *                  <li>1 = nomCliente</li>
     *                  <li>2 = apelCliente</li>
     *                  <li>3 = telefonoCliente</li>
     * @return Devuelve un objeto Pair< String, Integer > con un string del mensaje de salida y el status (0 ó -1)
     * @throws SQLException posible excepción SQL<p>Excepción al tratar de editar el cliente</p>
     */
    public Pair<String, Integer> editCliente(int idCliente, String nom, String ape, long tel, int[] toModify) throws SQLException {
        Statement st;

        // UPDATE REGISTRO EN TABLA cliente
        StringBuilder updNom = new StringBuilder("update cliente set nomCliente = '");
        StringBuilder updApe = new StringBuilder("update cliente set apelCliente = '");
        StringBuilder updTel = new StringBuilder("update cliente set telefonoCliente = ");

        updNom.append(nom).append("' where id_cliente = ").append(idCliente);
        updApe.append(ape).append("' where id_cliente = ").append(idCliente);
        updTel.append(tel).append(" where id_cliente = ").append(idCliente);

        StringBuilder[] updates = new StringBuilder[]{updNom,updApe,updTel};

        if (idCliente == 100000) return new Pair<>("Cliente no editable", -1);

        for (int p : toModify) {
            st = conexion.createStatement();
            st.executeUpdate(new String(updates[p - 1]));
            st.close();
        }
        return new Pair<>("Todo bien :)", 0);
    }

    /**
     * Método que elimina un cliente de la base de datos<p>Al eliminarse un cliente, los pedidos y ventas que pertenecían
     * a este se les asignará el valor por default</p>
     * @param idCliente Entero que representa el cliente que será borrado de la base de datos
     * @return Devuelve un objeto Pair< String, Integer > con un string del mensaje de salida y el status (0 ó -1)
     * @throws SQLException posible exceppción SQL<p>Excepción al tratar de eliminar un cliente</p>
     */
    public Pair<String, Integer> deleteCliente(int idCliente) throws SQLException {
        Statement st;

        // DELETE CLIENTE
        StringBuilder del = new StringBuilder("delete from cliente where id_cliente =").append(idCliente);

        if (idCliente == 100000) return new Pair<>("Cliente no editable", -1);

        // DELETE
        st = conexion.createStatement();
        st.executeUpdate(new String(del));
        st.close();
        return new Pair<>("Todo bien :)", 0);
    }

    /**
     * Método que obtiene la info de <b>todos</b> los productos para imprimirlos en la aplicación
     * @return Devuelve un ObservableList de ObservableList de Strings, cada ObservableList es una fila, cada String es
     * un registro. La priemra fila contiene los nombres de las columnas.
     * @throws SQLException posible excepción SQL<p>Excepción al tratar de obtener los registros</p>
     */
    public ObservableList<ObservableList<String>> verProductos() throws SQLException {
        Statement st;
        ResultSet rs;

        ObservableList<ObservableList<String>> resultados = FXCollections.observableArrayList();

        StringBuilder join = new StringBuilder("select id_producto as ID, nomProd as Producto, precio as Precio,");
        join.append(" almacen as 'En Almacen', categoria.id_categoria as Categoria from producto, categoria");
        join.append(" where producto.id_categoria = categoria.id_categoria ");

        st = conexion.createStatement();
        rs = st.executeQuery(new String(join));

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
     * Método que obtiene la info <b>del producto especificado</b> para imprimirlos en la aplicación
     * @param idCategoria Entero que representa el ID del producto del que se consultará la información
     * @return Devuelve un ObservableList de ObservableList de Strings, cada ObservableList es una fila, cada String es
     * un registro. La priemra fila contiene los nombres de las columnas.
     * @throws SQLException posible excepción SQL<p>Excepción al tratar de obtener los registros</p>
     */
    public ObservableList<ObservableList<String>> verProductos(int idCategoria) throws SQLException {
        Statement st;
        ResultSet rs;

        ObservableList<ObservableList<String>> resultados = FXCollections.observableArrayList();

        StringBuilder join = new StringBuilder("select id_producto as ID, nomProd as Producto, precio as Precio,");
        join.append(" almacen as 'En Almacen', categoria.id_categoria as Categoria from producto, categoria");
        join.append(" where producto.id_categoria = categoria.id_categoria  AND producto.id_categoria = ").append(idCategoria);

        st = conexion.createStatement();
        rs = st.executeQuery(new String(join));

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
     * Método que devuelve la información de <b>TODAS</b> las ventas relacionando todos los clientes que las realizarón
     * añadiendo la fecha de venta, el ID, el monto total, efectivo, cambio y el ID de pedido
     * @return Devuelve un ObservableList de ObservableList de Strings, cada ObservableList es una fila, cada String es
     * un registro. La priemra fila contiene los nombres de las columnas.
     * @throws SQLException posible excepción SQL<p>Excepción al consultar registros</p>
     */
    public ObservableList<ObservableList<String>> verVentas() throws SQLException {
        Statement st;
        ResultSet rs;

        ObservableList<ObservableList<String>> resultados = FXCollections.observableArrayList();

        StringBuilder join = new StringBuilder("select cliente.id_cliente as 'ID Cliente', nomCliente as Cliente,");
        join.append(" apelCliente as Apellido, id_venta as 'ID Venta', fecha_venta as 'Fecha Venta', montoF as Total,");
        join.append(" efectivo as Efectivo, cambio as Cambio, id_pedido as 'ID Pedido' from cliente, venta");
        join.append(" where cliente.id_cliente = venta.id_cliente ").append("order by fecha_venta desc ");

        st = conexion.createStatement();
        rs = st.executeQuery(new String(join));

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
     * Método que devuelve la información de las ventas relacionando <b>el</b> cliente que las reqlizó añadiendo la
     * fecha de venta, el ID, el monto total, efectivo, cambio y el ID de pedido
     * @param idCliente Entero que representa el ID del cliente del que se desea la información
     * @return Devuelve un ObservableList de ObservableList de Strings, cada ObservableList es una fila, cada String es
     * un registro. La priemra fila contiene los nombres de las columnas.
     * @throws SQLException posible excepción SQL<p>Excepción al consultar registros</p>
     */
    public ObservableList<ObservableList<String>> verVentasXClien(int idCliente) throws SQLException {
        Statement st;
        ResultSet rs;

        ObservableList<ObservableList<String>> resultados = FXCollections.observableArrayList();

        StringBuilder join = new StringBuilder("select cliente.id_cliente as 'ID Cliente', nomCliente as Cliente,");
        join.append(" apelCliente as Apellido, id_venta as 'ID Venta', fecha_venta as 'Fecha Venta', montoF as Total,");
        join.append(" efectivo as Efectivo, cambio as Cambio, id_pedido as 'ID Pedido' from cliente, venta");
        join.append(" where cliente.id_cliente = venta.id_cliente AND cliente.id_cliente = ").append(idCliente);
        join.append(" order by fecha_venta desc ");

        st = conexion.createStatement();
        rs = st.executeQuery(new String(join));

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
        /*StringBuilder idPedido = new StringBuilder("select auto_increment from information_schema.tables");
        idPedido.append(" where table_schema = 'wallbreaker' AND table_name = 'pedido' ");*/

        StringBuilder idPedido = new StringBuilder("SELECT MAX(id_pedido) FROM pedido");

        // SELECT montoF DEL ÚLTIMO PEDIDO
        StringBuilder montoF = new StringBuilder("select montoF from pedido");
        montoF.append(" where id_pedido = ");

        // SE OBTIENE EL id_pedido GENERADO
        st = conexion.createStatement();
        rs = st.executeQuery(new String(idPedido));
        while (rs.next()) {
            id_ped = Integer.parseInt(rs.getString("MAX(id_pedido)"));
        }
        montoF.append(id_ped);
        rs.close();
        st.close();

        // OBTIENE montoF DEL PEDIDO REALIZADO
        st = conexion.createStatement();
        rs = st.executeQuery(new String(montoF));
        while (rs.next()) {
            tot = Double.parseDouble(rs.getString("montoF"));
        }
        rs.close();
        st.close();

        return new Pair<>(id_ped, tot);
    }

    /**
     * Método que modifica los valores de ciertos registros en la tabla venta<p>Nota: No todos los vaores tienen que ser
     * modificados, pueden solo modificarse el 1 o el 2 o los 3</p>
     * @param idVenta Entero que representa el ID del registro de venta que se modificará
     * @param idCliente Entero que representa el nuevo ID de cliente que realizó la venta
     * @param efectivo Nuevo double con el efectivo que <b>cubre totalmente</b> el monto total de la venta
     * @param idPedido Nuevo ID de pedido que se reflejará en la venta
     * @param toModify Arreglo de enteros con el número de la(s) columnas a modificar (máximo 3)
     *                 <li>1 = id_cliente</li>
     *                 <li>2 = efectivo</li>
     *                 <li>3 = id_pedido</li>
     * @return Devuelve 0 si la operación salió con éxito
     * @throws SQLException posible excepción SQL<p>Excepción al tratar de editar la venta</p>
     */
    public int editVenta(int idVenta, int idCliente, double efectivo,  int idPedido, int[] toModify) throws SQLException {
        Statement st;
        ResultSet rs;
        double montoF = 0.0D;
        double efect = efectivo;
        double cambio = 0.0D;
        boolean flag = false;

        // UPDATES
        StringBuilder updMoF = new StringBuilder("update venta");
        StringBuilder updEfe = new StringBuilder("update venta");
        StringBuilder updCam = new StringBuilder("update venta");
        StringBuilder updCli = new StringBuilder("update venta");
        StringBuilder updIDP = new StringBuilder("update venta");

        ArrayList<Integer> mod = (ArrayList<Integer>) Arrays.stream(toModify).boxed().collect(Collectors.toList());

        // SE ALTERAN LOS VALORES DESDE EL PEDIDO
        if (mod.contains(3)) {
            flag = true;

            // SELECT montoF FROM TABLE pedido
            StringBuilder total = new StringBuilder("select montoF from pedido where id_pedido = ").append(idPedido);

            // SE OBTIENE EL NUEVO venta.montoF Y cambio PARA LA TABLA venta
            st = conexion.createStatement();
            rs = st.executeQuery(new String(total));
            while (rs.next()) montoF = Double.parseDouble(rs.getString("montoF")); // montoF NUEVO
            rs.close();
            st.close();

            // NO CAMBIA EL EFECTIVO
            if (!mod.contains(2)) {
                StringBuilder money = new StringBuilder("select efectivo from venta where id_venta = ").append(idVenta);
                st = conexion.createStatement();
                rs = st.executeQuery(new String(money));
                while (rs.next()) efect = Double.parseDouble(rs.getString("efectivo")); // efectivo
                rs.close();
                st.close();
            }

            // OPERACIÓN DEL CAMBIO
            cambio = efect - montoF; // cambio NUEVO

        }

        // SE CAMBIA EL VALOR DEL cambio
        if (mod.contains(2)) {
            flag = true;

            // SELECT montoF FROM TABLE venta
            StringBuilder total = new StringBuilder("select montoF from venta where id_venta = ").append(idVenta);

            // SE OBTIENE EL NUEVO venta.montoF Y cambio PARA LA TABLA venta
            st = conexion.createStatement();
            rs = st.executeQuery(new String(total));
            while (rs.next()) montoF = Double.parseDouble(rs.getString("montoF")); // montoF
            cambio = efect - montoF;
            rs.close();
            st.close();

        }
        // SI NO -> SOLO SE MODIFICA EL CLIENTE

        // SE PREPARAN LAS ACTUAIZACIONES
        updCli.append(" set id_cliente = ").append(idCliente).append(" where id_venta = ").append(idVenta);
        updMoF.append(" set montoF = ").append(montoF).append(" where id_venta = ").append(idVenta);
        updEfe.append(" set efectivo = ").append(efect).append(" where id_venta = ").append(idVenta);
        updCam.append(" set cambio = ").append(cambio).append(" where id_venta = ").append(idVenta);
        updIDP.append(" set id_pedido = ").append(idPedido).append(" where id_venta = ").append(idVenta);

        StringBuilder[] toM = new StringBuilder[] {updCli, updEfe, updIDP};

        st = conexion.createStatement();
        for (int i : toModify) {
            st.executeUpdate(new String(toM[(i - 1)]));
        }

        if (flag) {
            st.executeUpdate(new String(updMoF));
            st.executeUpdate(new String(updCam));
        }

        st.close();
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
        String queryProd = "select * from producto where id_producto = ";
        StringBuilder registro;

        st = conexion.createStatement();
        for (Pair<Integer, Integer> p : prods_cant) {
            rs = st.executeQuery(queryProd + p.getKey().toString());
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
     * Método que obtiene información de los pedidos, es decir, que clientes los realizarón, en que fecha, que productos
     * agregarón, el precio unitario por producto, el número de productos pedidos y el monto total a pagar por sus pedidos
     * <p>La consulta esta configurada para devolver la información ordenada en forma descendente por la fecha de pedido</p>
     * @return Devuelve un ObservableList de ObservableList de Strings, cada ObservableList es una fila, cada String es
     * un registro. La priemra fila contiene los nombres de las columnas.
     * @throws SQLException posible excepción SQL<p>Excepción al consultar registros</p>
     */
    public ObservableList<ObservableList<String>> verPedidos() throws SQLException {
        Statement st;
        ResultSet rs;

        ObservableList<ObservableList<String>> resultados = FXCollections.observableArrayList();

        StringBuilder join = new StringBuilder("select cliente.id_cliente as 'ID Cliente', nomCliente as Nombre, apelCliente as Apellido,");
        join.append(" pedido.id_pedido as 'ID Pedido', fecha_pedido as 'Fecha Pedido', pedido.status as Estado, producto.id_producto as 'ID Producto',");
        join.append(" nomProd as Producto, precio as 'Precio U', cantidad as '# Productos', descuento as Descuento, montoF as Total");
        join.append(" from cliente, pedido, producto, pedido_producto where cliente.id_cliente = pedido.id_cliente");
        join.append(" AND pedido.id_pedido = pedido_producto.id_pedido AND producto.id_producto = pedido_producto.id_producto");
        join.append("  order by fecha_pedido desc ");

        st = conexion.createStatement();
        rs = st.executeQuery(new String(join));

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
                if (v == 6) { // COLUMNA status EN EL JOIN pedido
                    if (Objects.isNull(rs.getString(v))) resultados.get(indiceFila).add("A Pagar"); // Añade un registro
                    else resultados.get(indiceFila).add("Vendido"); // Añade un registro
                } else resultados.get(indiceFila).add(rs.getString(v)); // Añade un registro
            }
        }
        rs.close();
        st.close();

        return resultados;
    }

    /**
     * Método que obtiene información de los <b>pedidos especifícos de un cliente</b> en que fecha se hicierón,
     * que productos agregarón, el precio unitario por producto, el número de productos pedidos y el monto total a pagar
     * por sus pedidos
     * <p>La consulta esta configurada para devolver la información ordenada en forma descendente por la fecha de pedido</p>
     * @param idCliente Entero que representa el ID del cliente del que se obtendrá la información
     * @return Devuelve un ObservableList de ObservableList de Strings, cada ObservableList es una fila, cada String es
     * un registro. La priemra fila contiene los nombres de las columnas.
     * @throws SQLException posible excepción SQL<p>Excepción al consultar registros</p>
     */
    public ObservableList<ObservableList<String>> verPedidosXClien(int idCliente) throws SQLException {
        Statement st;
        ResultSet rs;

        ObservableList<ObservableList<String>> resultados = FXCollections.observableArrayList();

        StringBuilder join = new StringBuilder("select cliente.id_cliente as 'ID Cliente', nomCliente as Nombre, apelCliente as Apellido,");
        join.append(" pedido.id_pedido as 'ID Pedido', fecha_pedido as 'Fecha Pedido', pedido.status as Estado, producto.id_producto as 'ID Producto',");
        join.append(" nomProd as Producto, precio as 'Precio U', cantidad as '# Productos', descuento as Descuento, montoF as Total");
        join.append(" from cliente, pedido, producto, pedido_producto where cliente.id_cliente = pedido.id_cliente");
        join.append(" AND pedido.id_pedido = pedido_producto.id_pedido AND producto.id_producto = pedido_producto.id_producto");
        join.append(" AND cliente.id_cliente = ").append(idCliente);
        join.append("  order by fecha_pedido desc ");

        st = conexion.createStatement();
        rs = st.executeQuery(new String(join));

        ResultSetMetaData md = rs.getMetaData();
        int cols = md.getColumnCount();

        resultados.add(FXCollections.observableArrayList()); // La primera fila serán los nombres de las columnas
        for (int i = 1; i <= cols; i++) {
            resultados.get(0).add(md.getColumnLabel(i)); // Nombre columnas
        }

        int indiceFila = 0;
        while (rs.next()) {
            resultados.add(FXCollections.observableArrayList()); // Añade una tupla
            indiceFila++;
            for (int v = 1; v <= cols; ++v) {
                if (v == 6) { // COLUMNA status EN EL JOIN pedido
                    if (Objects.isNull(rs.getString(v))) resultados.get(indiceFila).add("A Pagar"); // Añade un registro
                    else resultados.get(indiceFila).add("Vendido"); // Añade un registro
                } else resultados.get(indiceFila).add(rs.getString(v)); // Añade un registro
            }
        }
        rs.close();
        st.close();

        return resultados;
    }

    /**
     * Método que agrega un pedido a la base de datos
     * @param prods_cant ArrayList de objetos Pair que contienen la relación de los productos y la cantidad que se piden:
     *                   <p align="center"> Pair< id_producto , cantidad > </p>
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
        /*StringBuilder idPedido = new StringBuilder("select auto_increment from information_schema.tables");
        idPedido.append(" where table_schema = 'wallbreaker' AND table_name = 'pedido' ");*/

        StringBuilder idPedido = new StringBuilder("SELECT MAX(id_pedido) FROM pedido");

        // UPDATE almacen EN PRODUCTO
        String updateProd = "update producto set almacen = ";

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
            id_ped = Integer.parseInt(rs.getString("MAX(id_pedido)"));
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

            // updateProd.append(newAlmacen).append(" where id_producto = ").append(infoProds.get(w)[0]);
            st.executeUpdate(updateProd + newAlmacen + " WHERE id_producto = "
                    + infoProds.get(w)[0]); // Nuevo número de productos almacenados tras el pedido
            st.close();
        }

        return 0; // Everything OK
    }

    /**
     * Método que edita los valores de un pedido antes realizado<p>Se requiere del id_pedido que se modificará,
     * el id_cliente del cliente que edita el pedido y por último un ArrayList de objetos Pair que contienen la
     * relación de los <b>nuevos</b> productos y la cantidad de estos que se guardarán en el pedido:</p>
     * <p align="center"> Pair< id_producto , cantidad > </p>
     * @param idPed Entero que representa el id_pedido que se modificará
     * @param idClien Entero que representa el id_cliente que resliza el cambio
     * @param prods_cant ArrayList de objetos Pair que contienen la relación de los productos y la cantidad que se piden:
     *                   <p align="center"> Pair< id_producto , cantidad > </p>
     * @return Un objeto Pair< String , Integer > que representa el mensaje de retorno y el id_pedido nuevo generado<p>
     *     <b>Nota:</b> Si el entero es -1 el pedido ya estaba vendido y no se puede modificar
     * </p>
     * @throws SQLException posible excepción SQL<p>Excepción al tratar de editar el pedido</p>
     */
    public Pair<String, Integer> editPedido(int idPed, int idClien, ArrayList<Pair<Integer, Integer>> prods_cant) throws SQLException {
        Statement st;
        ResultSet rs;
        boolean flag = false; // DECIDE SI SE PUEDE EDITAR EL PEDIDO | false -> NO EDIT | true -> EDIT
        int newIdPedido = 0;

        // SELECT pedido.staus QUE VERIFICA SI NO SE HA VENDIDO; SI ESTA VENDIDO YA NO SE PUEDE EDITAR
        StringBuilder verSiVendido = new StringBuilder("select status from pedido where id_pedido = ").append(idPed);

        // VERIFICACIÓN DEL status DEL PEDIDO
        st = conexion.createStatement();
        rs = st.executeQuery(new String(verSiVendido));
        while (rs.next()) {
            // status = 1 -> VENDIDO -> NO SE PUEDE EDITAR
            // status = null -> NO ESTA VENDIDO -> SE PUEDE EDITAR
            flag = Objects.isNull(rs.getString("status"));
        }
        rs.close();
        st.close();

        if (flag) { // SOLO SI SE PUEDE EDITAR EL PEDIDO
            deletePedido(idPed); // ELIMINA EL PEDIDO QUE AHORA YA ESTA MODIFICADO Y ALMACENADO EN OTRO REGISTRO
            addPedido(prods_cant, idClien); // AGREGA LOS "(NUEVOS VALORES)" EN UN NUEVO REGISTRO
        }
        else return new Pair<>("Imposible editar pedido! Ya esta vendido!", -1); // Pues ya se vendio, nimodo

        // SELECT ÚLTIMO id_pedido CREADO
        StringBuilder idPedido = new StringBuilder("SELECT MAX(id_pedido) FROM pedido");

        st = conexion.createStatement();
        rs = st.executeQuery(new String(idPedido));
        while (rs.next()) newIdPedido = Integer.parseInt(rs.getString("MAX(id_pedido)"));
        rs.close();
        st.close();

        return new Pair<>("FINE!", newIdPedido);
    }

    /**
     * Método que realiza los procesos necesarios para ejecutar la cancelación de un pedido<p>Es decir, cuando un
     * cliente decide que no desea ya ese pedido, se utiliza este método para devolver los productos pedidos al almacen</p>
     * <p><b>Nota:</b> Es importante saber que si un pedido ya fue vendido, ya no se puede cancelar</p>
     * @param idPedido Entero que representa la llave primaria del pedido que será cancelado
     * @return Devuelve 0 si la operación salió correctamente<p>Devuelve -1 si el pedido que se intenta cancelar ya
     * esta vendido</p>
     * @throws SQLException posible excepción SQL<p>Excepción al tratar de cancelar un pedido</p>
     */
    public int cancelPedido(int idPedido) throws SQLException {
        Statement st;
        ResultSet rs;
        boolean flag = false; // DECIDE SI SE PUEDE CANCELAR EL PEDIDO | false -> NO CANCELABLE | true -> CANCELABLE

        // SELECT pedido.staus QUE VERIFICA SI NO SE HA VENDIDO; SI ESTA VENDIDO YA NO SE PUEDE EDITAR
        StringBuilder verSiVendido = new StringBuilder("select status from pedido where id_pedido = ").append(idPedido);

        // VERIFICACIÓN DEL status DEL PEDIDO
        st = conexion.createStatement();
        rs = st.executeQuery(new String(verSiVendido));
        while (rs.next()) {
            // status = 1 -> VENDIDO -> NO SE PUEDE CANCELAR
            // status = null -> NO ESTA VENDIDO -> SE PUEDE CANCELAR
            flag = Objects.isNull(rs.getString("status"));
        }
        rs.close();
        st.close();

        if (flag) deletePedido(idPedido); // SE ELIMINA == CANCELA EL PEDIDO
        else return -1; // NO ES POSIBLE REALIZAR LA OPERACIÓN | PEDIDO YA VENDIDO

        return 0;
    }

    /**
     * Método que elimina un(os) registro(s) de la base de datos en las tablas pedido y sus relaciones en la tabla
     * pedido_producto
     * <p>
     *     A su vez, como este método se utiliza en el proceso de editar o cancelar un pedido, este regresa la cantidad de
     *     productos pedidos a la tabla productos, para que despues se genere otro pedido (caso: edición del
     *     pedido inicial) con los nuevos productos solicitados o bien eliminarlo definitivamente (caso: cancelación del
     *     pedido)
     * </p>
     * @param idPedido Entero que representa el id_pedido que será eliminado de la base de datos
     * @return Devuelve 0 si no hubo errores
     * @throws SQLException posible excepción SQL<p>Excepción al tratar de eliminar un(os) registro(s)</p>
     */
    private int deletePedido(int idPedido) throws SQLException {
        Statement st;
        ResultSet rs;
        ArrayList<Pair<Integer, Integer>> prods_cant = new ArrayList<>();
        ArrayList<Pair<Integer, Integer>> actualProds = new ArrayList<>();

        // SELECT DE id_pedido Y cantidad DE TABLA pedido_producto
        StringBuilder select = new StringBuilder("select id_producto, cantidad from pedido_producto");
        select.append(" where id_pedido = ").append(idPedido);

        // OBTENCION DE CANTIDADES DE PRODUCTOS EN PEDIDO
        st = conexion.createStatement();
        rs = st.executeQuery(new String(select));
        while (rs.next()) {
            prods_cant.add(new Pair<>(Integer.parseInt(rs.getString(1)), Integer.parseInt(rs.getString(2))));
        }
        rs.close();
        st.close();



        // SELECT DE LOS VALORES ACTUALES DE LA TABLA producto
        StringBuilder prods = new StringBuilder("select id_producto, almacen from producto ");

        // OBTENCION DE INFORMACIÓN
        st = conexion.createStatement();
        rs = st.executeQuery(new String(prods));
        while (rs.next()) {
            actualProds.add(new Pair<>(Integer.parseInt(rs.getString(1)), Integer.parseInt(rs.getString(2))));
        }
        rs.close();
        st.close();



        // UPDATE QUE DEVUELVE LOS PRODUCTOS DEL PEDIDO QUE SE EDITA A LA TABLA producto
        StringBuilder devolu;
        int almacen;
        /* int[] almacen = new int[]{0};
        actualProds.forEach(v -> {
            if (v.getKey().equals(p.getKey())) {
                almacen[0] = v.getValue();
            }
        }); */

        // MAKE THE CHANGE
        for (Pair<Integer, Integer> p : prods_cant) {
            // RECORRE EL ARRAYLIST actualProds PARA OBTENER EL 'almacen' DE LOS PRODUCTOS QUE ESTABAN EN EL PEDIDO
            Optional<Pair<Integer, Integer>> x = actualProds.stream().filter(v -> v.getKey().equals(p.getKey())).findFirst();

            almacen = x.isPresent() ? x.get().getValue() : 0; // SI ENCONTRÓ UNA COINCIDENCIA OBTIENE EL 'almacen'

            devolu = new StringBuilder("update producto set almacen = ").append(almacen + p.getValue());
            devolu.append(" where id_producto = ").append(p.getKey());

            st = conexion.createStatement();
            st.executeUpdate(new String(devolu));
            st.close();
        }



        // DELETE ROW idPedido FROM pedido
        StringBuilder delPed = new StringBuilder("delete from pedido where id_pedido = ").append(idPedido);
        delPed.append(" limit 1 ");

        // DELETE ROWS FROM pedido_producto WHERE idPedido
        StringBuilder delPP = new StringBuilder("delete from pedido_producto where id_pedido = ").append(idPedido);

        StringBuilder[] querys = new StringBuilder[]{delPed,delPP};

        for (StringBuilder x : querys) {
            st = conexion.createStatement();
            st.executeUpdate(new String(x));
            st.close();
        }
        return 0;
    }

    /**
     * Método que obtiene información de todos los proveedores que proveen productos
     * @return Devuelve un ObservableList de ObservableList de Strings, cada ObservableList es una fila, cada String es
     * un registro. La priemra fila contiene los nombres de las columnas.
     * @throws SQLException posible excepción SQL<p>Excepción al solicitar la información</p>
     */
    public ObservableList<ObservableList<String>> proveedor() throws SQLException {
        Statement st;
        ResultSet rs;
        ResultSetMetaData md;
        ObservableList<ObservableList<String>> resultados = FXCollections.observableArrayList();

        StringBuilder queryProveedores = new StringBuilder("select proveedor.id_proveedor as 'ID Proveedor',");
        queryProveedores.append(" nomProveedor as Nombre, apelProveedor as Apellido, telefonoProveedor as Telefono, pag_web as 'Web Page',");
        queryProveedores.append(" producto.id_producto as 'ID Producto', nomProd as Producto, almacen as 'En Almacen'");
        queryProveedores.append(" from proveedor, producto, proveedor_producto where proveedor.id_proveedor = proveedor_producto.id_proveedor");
        queryProveedores.append(" AND producto.id_producto = proveedor_producto.id_producto");

        // Obtener información de todos los proveedores que proveen productos
        st = conexion.createStatement();
        rs = st.executeQuery(new String(queryProveedores));
        md = rs.getMetaData();
        int columnas = md.getColumnCount();
        resultados.add(FXCollections.observableArrayList()); // Nombres de columnas

        for (int i = 1; i <= columnas; i++) {
            resultados.get(0).add(md.getColumnLabel(i)); // Nombre columnas
        }

        int indiceFila = 0;
        while (rs.next()) {
            resultados.add(FXCollections.observableArrayList());
            indiceFila++;
            for (int p = 1; p <= 8; ++p) {
                resultados.get(indiceFila).add(rs.getString(p));
            }
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
    public ObservableList<ObservableList<String>> proveedor(int idProducto) throws SQLException {
        Statement st;
        ResultSet rs;
        ResultSetMetaData md;
        ObservableList<ObservableList<String>> resultados = FXCollections.observableArrayList();

        StringBuilder query = new StringBuilder("select proveedor.id_proveedor as 'ID Proveedor',");
        query.append(" nomProveedor as Nombre, apelProveedor as Apellido, telefonoProveedor as Telefono, pag_web as 'Web Page',");
        query.append(" producto.id_producto as 'ID Producto', nomProd as Producto, almacen as 'En Almacen'");
        query.append(" from proveedor, producto, proveedor_producto where proveedor.id_proveedor = proveedor_producto.id_proveedor");
        query.append(" AND producto.id_producto = proveedor_producto.id_producto AND producto.id_producto = ");
        query.append(idProducto);

        // Obtener información de los proveedores que preveen los productos especificados
        st = conexion.createStatement();
        rs = st.executeQuery(new String(query));
        md = rs.getMetaData();
        int columnas = md.getColumnCount();
        resultados.add(FXCollections.observableArrayList()); // Nombres de columnas

        for (int i = 1; i <= columnas; i++) {
            resultados.get(0).add(md.getColumnLabel(i)); // Nombre columnas
        }

        int indiceFila = 0;
        while (rs.next()) {
            resultados.add(FXCollections.observableArrayList());
            indiceFila++;
            for (int v = 1; v <= 8; ++v) {
                resultados.get(indiceFila).add(rs.getString(v));
            }
        }

        rs.close();
        st.close();
        return resultados;
    }

    /**
     * Método que agrega un proveedor a la base de datos en la tabla (proveedor) y relaciona los productos que este
     * provee en la tabla (proveedor_producto)<p>Parámetros: Nombre, Apellido, Teléfono, Pag. Web del proveedor y un
     * ArrayList de enteros con los ID de productos que el proveedor provee</p><p><b>Nota:</b> Se espera que los ID en
     * el ArrayList pertenezcan a <b>productos ya exitentes</b></p>
     * @param nom String con el nombre del proveedor (nulleable en la BD)
     * @param apel String con el apellido del proveedor (NOT NULL en la BD)
     * @param tel Long con el teléfono del proveedor (NOT NULL en la BD)
     * @param pWeb String con la página web del proveedor (nulleable en la BD)
     * @param idProds ArrayList de enteros con las llaves primarias de los productos que provee este proveedor
     * @return Devuelve 0 si la operación se realizó con éxito
     * @throws SQLException posible excepción SQL<p>Excepción al tratar de agreagr un proveeedor</p>
     */
    public int addProveedor(String nom, String apel, long tel, String pWeb, ArrayList<Integer> idProds) throws SQLException {
        Statement st;
        ResultSet rs;
        PreparedStatement ps;
        int id_proveed = 0;

        // INSERT INTO proveedor
        StringBuilder intoProve = new StringBuilder("insert into proveedor (nomProveedor, apelProveedor,");
        intoProve.append(" telefonoProveedor, pag_web)").append(" values (?, ?, ?, ?)");

        // SELECT ÚLTIMO id_proveedor
        /*StringBuilder idProveedor = new StringBuilder("select auto_increment from information_schema.tables");
        idProveedor.append(" where table_schema = 'wallbreaker' AND table_name = 'proveedor' ");*/

        StringBuilder idProveedor = new StringBuilder("SELECT MAX(id_proveedor) FROM proveedor");

        // INSERT INTO proveedor_producto
        StringBuilder propro = new StringBuilder("insert into proveedor_producto (id_proveedor, id_producto)");
        propro.append(" values (?, ?)");


        // SE PREPARA EL proveedor
        ps = conexion.prepareStatement(new String(intoProve));
        ps.setString(1, nom); // nomProveedor
        ps.setString(2, apel); // apelProveedor
        ps.setString(3, String.valueOf(tel)); // telefonoProveedor
        ps.setString(4, pWeb); // pag_web

        // SE AGREGA EL PROVEEDOR A LA BASE DE DATOS
        ps.execute();
        ps.close();

        // SE OBTIENE EL ÚLTIMO id_proveedor AGREGADO
        st = conexion.createStatement();
        rs = st.executeQuery(new String(idProveedor));
        while (rs.next()) {
            id_proveed = Integer.parseInt(rs.getString("MAX(id_proveedor)"));
        }
        rs.close();
        st.close();


        // SE PREPARA LA RELACIÓN proveedor_producto
        for (int p : idProds) {
            ps = conexion.prepareStatement(new String(propro));
            ps.setInt(1, id_proveed); // id_proveedor
            ps.setInt(2, p); // id_producto

            // GUARDA EL REGISTRO EN LA BASE DE DATOS
            ps.execute();
            ps.close();
        }

        return 0; // Everithing OK
    }

    /**
     * Método que edita la información propia del proveedor, es decir, solo Nombre, Apellido, Teléfono y Pág. Web de él
     * <p><b>Nota:</b> Para editar que productos provee que proveedor utilizar el método editProdsProv()</p>
     * @param id Entero que representa la llave primaria que identifica al proveedor que será editado
     * @param nom String con el nuevo nombre del proveedor
     * @param ape String con el apellido nombre del proveedor
     * @param tel Long con el nuevo teléfono del proveedor
     * @param pW String con la nueva página web del proveedor
     * @param toModify Arreglo de enteros con el número de la(s) columnas a modificar (máximo 4)
     *                  <li>1 = nomProveedor</li>
     *                  <li>2 = apelProveedor</li>
     *                  <li>3 = telefonoProveedor</li>
     *                  <li>4 = pag_web</li>
     * @return Devuelve 0 si la operación salió con éxito
     * @throws SQLException posible excepción SQL<p>Excepción al tratar de editar un proveeedor</p>
     */
    public int editProveedor(int id, String nom, String ape, long tel, String pW, int[] toModify) throws SQLException {
        Statement st;

        // UPDATE proveedor
        StringBuilder updNom = new StringBuilder("update proveedor");
        StringBuilder updApe = new StringBuilder("update proveedor");
        StringBuilder updTel = new StringBuilder("update proveedor");
        StringBuilder updPW = new StringBuilder("update proveedor");

        updNom.append(" set nomProveedor = '").append(nom).append("' where id_proveedor = ").append(id);
        updApe.append(" set apelProveedor = '").append(ape).append("' where id_proveedor = ").append(id);
        updTel.append(" set telefonoProveedor = ").append(tel).append(" where id_proveedor = ").append(id);
        updPW.append(" set pag_web = '").append(pW).append("' where id_proveedor = ").append(id);

        // CAMPOS A MODIFICAR
        StringBuilder[] toM = new StringBuilder[] {updNom, updApe, updTel, updPW};

        for (int i : toModify) {
            st = conexion.createStatement();
            st.executeUpdate(new String(toM[(i - 1)]));
            st.close();
        }
        return 0;
    }

    /**
     * Método que edita los productos que provee determinado proveedor<p>Cambia los productos que provee dicha persona
     * por otros diferentes</p><p><b>Nota:</b> No le agrega nuevos productos para proveer a un determinado proveedor</p>
     * @param idProveedor Entero que representa la llave primaria que identifica el proveedor del que se editarán
     *                    sus productos
     * @param oldIDProds ArrayList de enteros que almacena los (id_producto) del proveedor (idProveedor) que
     *                   serán modificados
     * @param newIDProds ArrayList de enteros que almacena los nuevos (id_producto) que reemplazarán los del ArrayList
     *                   de (oldIDProds)
     * @return devuelve 0 si la operación salió exitosamente
     * @throws SQLException posible excepción SQL<p>Excepción al tratar de editar los productos de un proveeedor</p>
     */
    public int editProdsProv(int idProveedor, ArrayList<Integer> oldIDProds, ArrayList<Integer> newIDProds) throws SQLException {
        Statement st;
        ResultSet rs;
        ArrayList<String[]> prov_prod = new ArrayList<>(); // Registros de tabla proveedor_producto del proveedor dado
        ArrayList<Integer> IDsProvProd = new ArrayList<>(); // IDs de tabla proveedor_producto a modificar

        // SELECT ROWS WITH id_proveedor LIKE @param idProveedor FROM proveedor_producto
        StringBuilder queryProPro = new StringBuilder("select * from proveedor_producto");
        queryProPro.append(" where id_proveedor = ").append(idProveedor);

        // OBTENER INFO DE QUE PRODS PROVEE EL PROVEEDOR ESPECIFICADO
        st = conexion.createStatement();
        rs = st.executeQuery(new String(queryProPro));
        while (rs.next()) {
            StringBuilder s = new StringBuilder();
            for (int i = 1; i <= numcols.get("proveedor_producto"); ++i) s.append(rs.getString(i)).append(",");
            s.deleteCharAt(s.lastIndexOf(","));
            prov_prod.add(new String(s).split(","));
        }
        rs.close();
        st.close();


        // DEFINIR QUE id_propro(s) SERÁN MODIFICADOS
        for (String[] x : prov_prod) { // Registros de tabla proveedor_producto del proveedor dado
            for (int y : oldIDProds) { // IDs de los productos a ser cambiados
                if (Integer.parseInt(x[2]) == y) IDsProvProd.add(Integer.parseInt(x[0]));
            }
        }

        // MODIFICAR REGISTRO
        int i = 0;
        for (int p : newIDProds) {
            // UPDATE id_producto EN proveedor_producto
            StringBuilder updProds = new StringBuilder("update proveedor_producto");
            updProds.append(" set id_producto = ").append(p).append(" where id_propro = ").append(IDsProvProd.get(i));
            ++i;

            st = conexion.createStatement();
            st.executeUpdate(new String(updProds));
            st.close();
        }
        return 0;
    }

    /**
     * Método que agrega IDs de productos proveidos por un proveedor específico a la tabla proveedor_producto en la
     * base de datos<p>Este método puede agreagr uno o varios ID de productos a la vez a la relación de proveedor con
     * producto por medio del ID del proveedor y un ArrayList con los IDs de los productos que provee que <b>no tenía
     * ya antes asignados</b></p>
     * @param idProveedor Entero que representa el ID del proveedor al que se le asignarán productos
     * @param prods ArrayList de enteros que contiene <b>el o los</b> ID de producto(s) que provee ese proveedor
     * @return Devuelve 0 si la operación salió con éxito
     * @throws SQLException posible excepción SQL<p>Excepción al agregar información en la tabla proveedor_producto</p>
     */
    public int addProdToProveedor(int idProveedor, ArrayList<Integer> prods) throws SQLException {
        PreparedStatement ps;

        // INSERT INTO proveedor_producto
        StringBuilder ins = new StringBuilder("insert into proveedor_producto (id_proveedor, id_producto)");
        ins.append(" values (?, ?) ");

        // SE PREPARA LA RELACIÓN
        for (int v : prods) {
            ps = conexion.prepareStatement(new String(ins));
            ps.setInt(1, idProveedor);
            ps.setInt(2, v);

            ps.execute();
            ps.close();
        }
        return 0;
    }

    /**
     * Método que elimina la relación de un producto específico con el proveedor específico<p>Es decir, este método
     * quita un producto de ser proveido por un determinado proveedor</p>
     * @param idProveedor Entero que representa el ID del proveedor al que se le desasignará un producto
     * @param idProducto Entero que representa el ID del producto que ya no será proveido por el proveedor
     * @return Devuelve 0 si la operación salió con éxito
     * @throws SQLException posible excepción SQL<p>Excepción al tratar de desasignar un producto de un proveedor</p>
     */
    public int deleteProdFromProvee(int idProveedor, int idProducto) throws SQLException {
        Statement st;

        // DELETE FROM proveedor_producto
        StringBuilder del = new StringBuilder("delete from proveedor_producto where id_proveedor = ").append(idProveedor);
        del.append(" AND id_producto = ").append(idProducto).append(" limit 1 ");

        // MAKE THE DELETION
        st = conexion.createStatement();
        st.executeUpdate(new String(del));
        st.close();

        return 0;
    }

    /**
     * Método para eliminar un proveedor de la base de datos
     * @param idProveedor Llave primaria que representa el (id_proveedor) a ser eliminado
     * @return Devuelve 0 si no hubo errores
     * @throws SQLException posible excepción SQL<p>Excepción al tratar de eliminar un proveedor</p>
     */
    public int deleteProveedor(int idProveedor) throws SQLException {
        Statement st = conexion.createStatement();
        StringBuilder x = new StringBuilder("delete from proveedor");
        x.append(" where id_proveedor = ").append(idProveedor);

        st.executeUpdate(new String(x));
        st.close();
        return 0;
    }

    /**
     * Método que obtienen información de todos el personal almacenado en la base de datos
     * @return Devuelve un ObservableList de ObservableList de Strings, cada ObservableList es una fila, cada String es
     * un registro. La priemra fila contiene los nombres de las columnas.
     * @throws SQLException posible excepción SQL<p>Excepción al tratar de obtener los registros</p>
     */
    public ObservableList<ObservableList<String>> verPersonal() throws SQLException {
        Statement st;
        ResultSet rs;

        ObservableList<ObservableList<String>> resultados = FXCollections.observableArrayList();

        StringBuilder join = new StringBuilder("select id_personal as ID, nomPersonal as Nombre,");
        join.append(" apelPersonal as Apellido, ocupacion as Ocupacion, telefonoPersonal as Telefono,");
        join.append(" salario as Salario from personal ");

        st = conexion.createStatement();
        rs = st.executeQuery(new String(join));

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
     * Método que agrega personal a la base de datos
     * @param nom String con el nombre del personal
     * @param apel String con el apellido del personal
     * @param ocup String con la ocupación del personal
     * @param tel Long con el teléfono del personal
     * @param sal Double con el salario del personal
     * @return Devuelve 0 si la operación se realizó exitosamente
     * @throws SQLException posible excepción SQL<p>Excepción al tratar de agregar algún personal</p>
     */
    public int addPersonal(String nom, String apel, String ocup, long tel, double sal) throws SQLException {
        PreparedStatement ps;

        // INSERT INTO PERSONAL
        StringBuilder personal = new StringBuilder("insert into personal");
        personal.append(" (nomPersonal, apelPersonal, ocupacion, telefonoPersonal, salario)");
        personal.append(" values (?, ?, ?, ?, ?)");

        // SE PREPARA EL PERSONAL
        ps = conexion.prepareStatement(new String(personal));
        ps.setString(1, nom); // nomPersonal
        ps.setString(2, apel); // apelPersonal
        ps.setString(3, ocup); // ocupacion
        ps.setLong(4, tel); // telefonoPersonal
        ps.setDouble(5, sal); // salario

        // GUARDA LA INFO EN LA BASE DE DATOS
        ps.execute();
        ps.close();

        return 0;
    }

    /**
     * Método que edita el nombre, apellido, ocupación, teléfono y salario de algún personal específico
     * @param idPersnl Llave primaria que identifica el personal a ser modificado
     * @param nom String con el nuevo nombre
     * @param apel String con el nuevo apellido
     * @param ocup Strimg con la nueva ocupación
     * @param tel Long con el nuevo teléfono
     * @param sal Double con el nuevo salario
     * @param toModify Arreglo de enteros con el número de la(s) columnas a modificar (máximo 5)
     *                  <li>1 = nomPersonal</li>
     *                  <li>2 = apelPersonal</li>
     *                  <li>3 = ocupacion</li>
     *                  <li>4 = telefonoPersonal</li>
     *                  <li>5 = salario</li>
     * @return Develve 0 si la operación salió con éxito
     * @throws SQLException posible excepción SQL<p>Excepción al tratar de editar algún personal</p>
     */
    public int editPersonal(int idPersnl, String nom, String apel, String ocup, long tel, double sal, int[] toModify) throws SQLException {
        Statement st;
        StringBuilder updNom = new StringBuilder("update personal");
        StringBuilder updApe = new StringBuilder("update personal");
        StringBuilder updOcu = new StringBuilder("update personal");
        StringBuilder updTel = new StringBuilder("update personal");
        StringBuilder updSal = new StringBuilder("update personal");

        updNom.append(" set nomPersonal = '").append(nom).append("' where id_personal = ").append(idPersnl);
        updApe.append(" set apelPersonal = '").append(apel).append("' where id_personal = ").append(idPersnl);
        updOcu.append(" set ocupacion = '").append(ocup).append("' where id_personal = ").append(idPersnl);
        updTel.append(" set telefonoPersonal = ").append(tel).append(" where id_personal = ").append(idPersnl);
        updSal.append(" set salario = ").append(String.format("%.1f", sal)).append(" where id_personal = ").append(idPersnl);

        StringBuilder[] toM = new StringBuilder[] {updNom, updApe, updOcu, updTel, updSal};

        st = conexion.createStatement();
        for (int i : toModify) {
            st.executeUpdate(new String(toM[(i - 1)]));
        }
        st.close();
        return 0;
    }

    /**
     * Método para eliminar algún personal de la base de datos
     * @param idPersonal Llave primaria que representa el (id_personal) a ser eliminado
     * @return Devuelve 0 si no hubo errores
     * @throws SQLException posible excepción SQL<p>Excepción al tratar de eliminar algún personal</p>
     */
    public int deletePersonal(int idPersonal) throws SQLException {
        Statement st = conexion.createStatement();
        StringBuilder x = new StringBuilder("delete from personal");
        x.append(" where id_personal = ").append(idPersonal);

        st.executeUpdate(new String(x));
        st.close();
        return 0;
    }

    /** Método de ayuda para obtener el nombre e ID de alguna tabla
     * @return Una lista observables de listas observables con el nombre e ID
     * @throws SQLException Si algo sale mal en la BD
     * */
    public ArrayList<Pair<String, Integer>> consultarNombreConId(String columnaNombre, String columnaId, String tabla)
            throws SQLException {
        ObservableList<ObservableList<String>> resultados = FXCollections.observableArrayList();
        String instruccion = "SELECT " + columnaNombre + ", " + columnaId + " FROM " + tabla;
        Statement st = conexion.createStatement();
        ResultSet rs = st.executeQuery(instruccion);
        int cols = rs.getMetaData().getColumnCount();
        int indiceFila = -1;
        while (rs.next()) {
            resultados.add(FXCollections.observableArrayList()); // Añade una tupla
            indiceFila++;
            for (int v = 1; v <= cols; ++v) {
                resultados.get(indiceFila).add(rs.getString(v)); // Añade un registro
            }
        }
        rs.close();
        st.close();
        ArrayList<Pair<String, Integer>> relacion = new ArrayList<>();
        for (ObservableList<String> tupla : resultados) {
            relacion.add(new Pair<>(tupla.get(0), Integer.parseInt(tupla.get(1))));
        }
        return relacion;
    }


    /** Método para obtener el nombre e ID de las categorías
     * @return Una lista observables de listas observables con el nombre e ID
     * @throws SQLException Si algo sale mal en la BD
    * */
    public ArrayList<Pair<String, Integer>> consultarCategorias() throws SQLException {
        return consultarNombreConId("nomCategoria", "id_categoria", "categoria");
    }

    /** Método para obtener el nombre e ID de los productos
     * @return Una lista observables de listas observables con el nombre e ID
     * @throws SQLException Si algo sale mal en la BD
     * */
    public ArrayList<Pair<String, Integer>> consultarProductos() throws SQLException {
        return consultarNombreConId("nomProd", "id_producto", "producto");
    }

    /**
     * Obtiene los productos que provee el proveedor dado.
     * @param idProveedor el ID del proveedor a consultar.
     * @return Un ArrayList con los IDs de los productos que provee.
     * @throws SQLException Si algo sale mal en la BD.
     */
    public ArrayList<Integer> conseguirProductosDeUnProveedor(int idProveedor) throws SQLException {
        ArrayList<Integer> productos = new ArrayList<>();
        String instruccion = "SELECT id_producto FROM proveedor_producto WHERE id_proveedor = " + idProveedor;
        Statement statement = conexion.createStatement();
        ResultSet resultSet = statement.executeQuery(instruccion);
        while (resultSet.next()) {
            productos.add(resultSet.getInt("id_producto"));
        }
        resultSet.close();
        statement.close();
        return productos;
    }

    /**
     * Cierra la conexión con el servidor de MySQL
     * Siempre llamar este método antes de terminar la aplicación
     * @throws SQLException posible excepción SQL
     */
    public void end() throws SQLException {
        obj.closeCNX();
    }

}
