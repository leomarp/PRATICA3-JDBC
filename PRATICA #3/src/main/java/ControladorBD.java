import org.h2.tools.Server;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ControladorBD {
    private static ControladorBD instancia;
    private String URL = "jdbc:h2:tcp://localhost/~/PRACTICA#3";
    private static Server tc;

    private  ControladorBD(){
        registrarDriver();
    }

    public static ControladorBD getInstancia(){
        if(instancia==null){
            instancia = new ControladorBD();
        }
        return instancia;
    }
    public static void startDb() throws SQLException {
        tc = Server.createTcpServer("-tcpPort", "9092", "-tcpAllowOthers","-ifNotExists").start();
    }

    public static void stopDb() throws SQLException {
        tc.stop();
    }

    private void registrarDriver() {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ControladorBD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public Connection getConexion() {
        Connection con = null;
        try {
            con = DriverManager.getConnection(URL, "sa", "");
        } catch (SQLException ex) {
            Logger.getLogger(ControladorBD.class.getName()).log(Level.SEVERE, null, ex);
        }
        return con;
    }
    public void testConexion() {
        try {
            getConexion().close();
            System.out.println("Conexión realizado con exito...");
        } catch (SQLException ex) {
            Logger.getLogger(ControladorBD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void crearTablas() throws  SQLException{
        String sql = "CREATE TABLE IF NOT EXISTS USUARIO\n" +
                "(\n" +
                "  NAME VARCHAR(100) NOT NULL,\n" +
                "  USER VARCHAR(100) NOT NULL,\n" +
                "  PASSWORD VARCHAR(100) NOT NULL\n" +
                ");\n" +
                "CREATE TABLE IF NOT EXISTS PRODUCTO\n"+
                "(\n" +
                "  ID INTEGER PRIMARY KEY NOT NULL,\n" +
                "  NAME VARCHAR(100) NOT NULL,\n" +
                "  QUANTITY VARCHAR(100) NOT NULL,\n" +
                "  PRICE VARCHAR(100) NOT NULL\n" +
                ");\n"+
                "CREATE TABLE IF NOT EXISTS VENTA \n"+
                "(" +
                "ID VARCHAR(100) NOT NULL,"+
                "CLIENT_NAME VARCHAR(100) NOT NULL,"+
                "FECHA VARCHAR(200)," +
                "TOTAL VARCHAR(100));\n" +
                "CREATE TABLE IF NOT EXISTS PRODUCTOVENDIDOS\n" +
                "( " +
                "IDSELL VARCHAR(100) NOT NULL," +
                "IDPRODUCT INTEGER NOT NULL" +
                ");";

        Connection con = ControladorBD.getInstancia().getConexion();
        Statement statement = con.createStatement();
        statement.execute(sql);
        statement.close();
        con.close();
    }

}
