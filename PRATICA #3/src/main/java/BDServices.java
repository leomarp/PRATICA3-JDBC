import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BDServices {


    public boolean crearUsuario(Usuario user){
        boolean ok =false;
        Connection con = null;
        try {
            String query = "insert into USUARIO(NAME, USER, PASSWORD) values(?,?,?)";
            con = ControladorBD.getInstancia().getConexion();
            //
            PreparedStatement prepareStatement = con.prepareStatement(query);
            //Antes de ejecutar seteo los parametros.
            prepareStatement.setString(1, user.getNombre());
            prepareStatement.setString(2, user.getUsuario());
            prepareStatement.setString(3, user.getPassword());

            //
            int fila = prepareStatement.executeUpdate();
            ok = fila > 0 ;

        } catch (SQLException ex) {
            Logger.getLogger(BDServices.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(BDServices.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ok;
    }

    public Usuario getUsuariobyUser(String us) {
        Usuario user = null;
        Connection con = null;
        try {
            //utilizando los comodines (?)...
            String query = "select * from USUARIO where USER = ?";
            con = ControladorBD.getInstancia().getConexion();
            //
            PreparedStatement prepareStatement = con.prepareStatement(query);
            //Antes de ejecutar seteo los parametros.
            prepareStatement.setString(1, us);
            //Ejecuto...
            ResultSet rs = prepareStatement.executeQuery();
            while(rs.next()){
                user = new Usuario(rs.getString("NAME"),rs.getString("USER"),
                        rs.getString("PASSWORD"));
                           }

        } catch (SQLException ex) {
            Logger.getLogger(BDServices.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(BDServices.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return user;
    }

    public boolean existeUsuario(String user){
        boolean aux;

        if (getUsuariobyUser(user) != null){
            aux = true;
        }else{
            aux = false;
        }

        return aux;
    }

    public ArrayList<Usuario> cargarUsuarios(){
        ArrayList<Usuario> lista = new ArrayList<Usuario>();
        Connection con = null; //objeto conexion.
        try {
            //
            String query = "select * from USUARIO ";
            con = ControladorBD.getInstancia().getConexion(); //referencia a la conexion.
            //
            PreparedStatement prepareStatement = con.prepareStatement(query);
            ResultSet rs = prepareStatement.executeQuery();
            while(rs.next()){
                Usuario user = new Usuario(rs.getString("USER"), rs.getString("NAME"),
                        rs.getString("PASSWORD"));

                lista.add(user);
            }

        } catch (SQLException ex) {
            Logger.getLogger(BDServices.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(BDServices.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return  lista;
    }

    public boolean crearProducto(Producto producto){
        boolean ok =false;

        Connection con = null;
        try {

            String query = "insert into PRODUCTO(ID,NAME, QUANTITY, PRICE) values(?,?,?,?)";
            con = ControladorBD.getInstancia().getConexion();
            //
            PreparedStatement prepareStatement = con.prepareStatement(query);
            //Antes de ejecutar seteo los parametros.
            prepareStatement.setInt(1, producto.getId());
            prepareStatement.setString(2, producto.getNombre());
            prepareStatement.setString(3, String.valueOf(producto.getCantidad()));
            prepareStatement.setString(4, String.valueOf(producto.getPrecio()));


            //
            int fila = prepareStatement.executeUpdate();
            ok = fila > 0 ;

        } catch (SQLException ex) {
            Logger.getLogger(BDServices.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(BDServices.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return ok;
    }

    public ArrayList<Producto> cargarProductos(){
        ArrayList<Producto> lista = new ArrayList<Producto>();
        Connection con = null; //objeto conexion.
        try {
            //
            String query = "select * from PRODUCTO ";
            con = ControladorBD.getInstancia().getConexion(); //referencia a la conexion.
            //
            PreparedStatement prepareStatement = con.prepareStatement(query);
            ResultSet rs = prepareStatement.executeQuery();
            while(rs.next()){
                Producto tmp = new Producto(rs.getInt("ID"),rs.getString("NAME"),
                        Integer.valueOf(rs.getString("QUANTITY")),
                        new BigDecimal(rs.getString("PRICE")));

                lista.add(tmp);
            }

        } catch (SQLException ex) {
            Logger.getLogger(BDServices.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(BDServices.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return  lista;
    }
    public boolean borrarProducto(int ID){
        boolean ok =false;

        Connection con = null;
        try {

            String query = "delete from PRODUCTO where ID = ?";
            con = ControladorBD.getInstancia().getConexion();
            //
            PreparedStatement prepareStatement = con.prepareStatement(query);

            //Indica el where...
            prepareStatement.setInt(1, ID);
            //
            int fila = prepareStatement.executeUpdate();
            ok = fila > 0 ;

        } catch (SQLException ex) {
            Logger.getLogger(BDServices.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(BDServices.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return ok;
    }
    public boolean actualizarProducto(Producto prod){
        boolean ok =false;

        Connection con = null;
        try {

            String query = "UPDATE PRODUCTO SET NAME=?, QUANTITY=?, PRICE =? WHERE ID = ?";
            con = ControladorBD.getInstancia().getConexion();

            PreparedStatement prepareStatement = con.prepareStatement(query);
            prepareStatement.setString(1, prod.getNombre());
            prepareStatement.setString(2, String.valueOf(prod.getCantidad()));
            prepareStatement.setString(3, String.valueOf(prod.getPrecio()));
            prepareStatement.setInt(4, prod.getId());//Este es para el Where
            int fila = prepareStatement.executeUpdate();
            ok = fila > 0 ;

        } catch (SQLException ex) {
            Logger.getLogger(BDServices.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(BDServices.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return ok;
    }

    public boolean crearVenta(VentasProductos venta){
        boolean ok =false;

        Connection con = null;
        try {

            String query = "INSERT INTO VENTA(ID,CLIENT_NAME, FECHA, TOTAL) values(?,?,?,?)";
            String queryaux = "INSERT INTO PRODUCTOVENDIDOS(IDSELL, IDPRODUCT ) values(?,?)";
            con = ControladorBD.getInstancia().getConexion();
            //
            PreparedStatement prepareStatement = con.prepareStatement(query);
            //Antes de ejecutar seteo los parametros.
            String id = venta.getId();
            prepareStatement.setString(1, id );
            prepareStatement.setString(2, venta.getNombreCliente());
            prepareStatement.setString(3,venta.getFechaCompra().toString());
            prepareStatement.setFloat(4,venta.getTotal());
            if(!insertarProductosVendidos(venta.getListaProductos(),id)){
                System.out.println("Error al insertar produtos\n");
            }

            int fila = prepareStatement.executeUpdate();
            System.out.println("Creo ventas: "+ fila);
            ok = fila > 0 ;

        } catch (SQLException ex) {
            Logger.getLogger(BDServices.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(BDServices.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return ok;
    }
    public boolean insertarProductosVendidos(ArrayList<Producto> productos, String ID){
        boolean ok = false;
        Connection con = null;
        try {

            String query = "INSERT INTO PRODUCTOVENDIDOS(IDSELL, IDPRODUCT ) values(?,?)";
            con = ControladorBD.getInstancia().getConexion();
            //
            PreparedStatement prepareStatement = con.prepareStatement(query);
            System.out.println("Cantidad de productos en venta: "+ productos.size());
            //Antes de ejecutar seteo los parametros.
            int fila = 0;
            for(Producto p : productos){
                prepareStatement.setString(1,ID);
                prepareStatement.setInt(2,p.getId());
                fila = prepareStatement.executeUpdate();
                ok = fila > 0 ;
            }



        } catch (SQLException ex) {
            Logger.getLogger(BDServices.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(BDServices.class.getName()).log(Level.SEVERE, null, ex);
            }
        }


        return ok;
    }


    public ArrayList<VentasProductos> cargarVentas() throws SQLException {
        ArrayList<VentasProductos> lista = new ArrayList<VentasProductos>();
        Connection con = null; //objeto conexion.
        try {

            String query = "SELECT * FROM VENTA";
            con = ControladorBD.getInstancia().getConexion();
            PreparedStatement prepareStatement = con.prepareStatement(query);
            ResultSet rs = prepareStatement.executeQuery();

            while(rs.next()){

                ArrayList<Producto> prods = new ArrayList<Producto>();
                String id = rs.getString("ID");
                String fecha = rs.getString("FECHA");
                String nombreclient = rs.getString("CLIENT_NAME");
                String total = rs.getString("TOTAL");

                DateFormat sfd = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                Date date = sfd.parse(String.valueOf(fecha));
                prods = cargarProductosdeVendidos(id);
                System.out.println("Cantidad de productos en la venta"+id + "es:"+prods.size());
                VentasProductos venta = new VentasProductos(id, date, nombreclient, prods, Float.valueOf(total));
                lista.add(venta);

            }



        } catch (SQLException | ParseException ex) {
            Logger.getLogger(BDServices.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            try {
               con.close();
            } catch (SQLException ex) {
                Logger.getLogger(BDServices.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return lista;
    }

    private ArrayList<Producto> cargarProductosdeVendidos(String IDVenta){
        ArrayList<Producto> lista = new ArrayList<Producto>();
        Connection con = null; //objeto conexion.
        try {
            //
            String query = "SELECT * FROM PRODUCTOVENDIDOS WHERE IDSELL = ?";
            con = ControladorBD.getInstancia().getConexion(); //referencia a la conexion.
            //
            PreparedStatement prepareStatement = con.prepareStatement(query);
            prepareStatement.setString(1,IDVenta);
            ResultSet rs = prepareStatement.executeQuery();
            while(rs.next()){
                Producto tmp = Controladora.getInstance().buscaProductobyid(rs.getInt("IDPRODUCT"));

                lista.add(tmp);
            }

        } catch (SQLException ex) {
            Logger.getLogger(BDServices.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(BDServices.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return  lista;
    }
}
