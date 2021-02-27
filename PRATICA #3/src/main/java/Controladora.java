import java.util.ArrayList;

public class Controladora{
    private static Controladora controladora = null;
    private ArrayList<Usuario> usuarios;
    private ArrayList<Producto> productos;
    private ArrayList<VentasProductos> ventas;


    public Controladora() {
        usuarios = new ArrayList<Usuario>();
        /*Usuario ad = new Usuario("admin","Administrator","admin");
        usuarios.add(ad);*/
        productos = new ArrayList<Producto>();
        ventas = new ArrayList<VentasProductos>();


    }
    public static Controladora getInstance() {
        if (controladora == null) {
            controladora = new Controladora();
        }
        return controladora;
    }


    public static Controladora getControladora() {
        return controladora;
    }

    public static void setControladora(Controladora controladora) {
        Controladora.controladora = controladora;
    }

    public ArrayList<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(ArrayList<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public ArrayList<Producto> getProductos() {
        return productos;
    }

    public void setProductos(ArrayList<Producto> productos) {
        this.productos = productos;
    }

    public ArrayList<VentasProductos> getVentas() {
        return ventas;
    }

    public void setVentas(ArrayList<VentasProductos>  venta) {
        this.ventas = venta;
    }

    public void crearUsuario(Usuario usuario){
        this.usuarios.add(usuario);
    }
    public boolean verificarUsuario(String usuario, String password){
        boolean aux = false;
        for (Usuario u:usuarios) {
            if(u.getUsuario().equals(usuario) && u.getPassword().equals(password)){
                aux = true;
            }
        }
        return aux;
    }
    public void creaProducto(Producto producto){
        productos.add(producto);
    }
    public Producto buscaProductobyid (int id){
        return(productos.stream().filter(e -> e.getId() == id).findFirst().orElse(null));
    }
    public Producto editarProducto(Producto producto){
        Producto tmp = buscaProductobyid(producto.getId());
        if(tmp != null){
            tmp.mezclar(producto);
        }

        return tmp;
    }
    public void borraProducto(Producto producto){
        this.productos.remove(producto);
    }
    public Usuario buscarUsuariobyUser(String user){
        Usuario aux = null;
        for(Usuario u :usuarios){
            if(user.equals(u.getUsuario())){
                aux = u;
            }
        }

        return aux;
    }
}

