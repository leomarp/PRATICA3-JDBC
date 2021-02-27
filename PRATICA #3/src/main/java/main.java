import io.javalin.Javalin;
import io.javalin.core.util.RouteOverviewPlugin;
import org.jasypt.util.text.BasicTextEncryptor;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;


public class main {
    public static void main(String[] args) throws SQLException {
        Controladora control = Controladora.getInstance();
        ArrayList<Producto> prueba = control.getProductos();
        CarroCompra carroCompra = new CarroCompra(null);
        AtomicBoolean status = new AtomicBoolean(false);
        BDServices servicios = new BDServices();
        ControladorBD bd = ControladorBD.getInstancia();
        bd.startDb();
        bd.testConexion();
        bd.crearTablas();
        if(!servicios.existeUsuario("admin")){
            servicios.crearUsuario(new Usuario("admin", "Administrador", "admin"));
        }
        control.setUsuarios(servicios.cargarUsuarios());
        control.setProductos(servicios.cargarProductos());
        control.setVentas(servicios.cargarVentas());

        Javalin app = Javalin.create(config ->{
            config.addStaticFiles("/Publico"); //desde la carpeta de resources
            config.registerPlugin(new RouteOverviewPlugin("/rutas")); //aplicando plugins de las rutas
        }).start(8000);

            /* MANEJO DE LA PAGINA PRINCIPAL*/
            app.get("/", ctx ->{ ctx.redirect("/Principal");});

            app.get("/Principal", ctx -> {
                //tomando el parametro utl y validando el tipo.
                List<Producto> lista = Controladora.getInstance().getProductos();
                //
               if(ctx.cookie("usuarioGuardado")!=null){
                   BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
                   textEncryptor.setPassword("clave");
                   print(ctx.cookie("usuarioGuardado"));
                   String myEncryptedText = ctx.cookie("usuarioGuardado");
                   String plainText = textEncryptor.decrypt(myEncryptedText);
                   print(plainText);

                   if(ctx.sessionAttribute("user")== null){
                       ctx.sessionAttribute("user",plainText);
                   }

               }

                Map<String, Object> modelo = new HashMap<>();
                int aux = carroCompra.getListaProductos().size();
                modelo.put("cantidad",aux);
                modelo.put("titulo", "Listado de productos");
                modelo.put("lista", lista);
                //enviando al sistema de plantilla.
                ctx.render("Publico/principal.html", modelo);

            });




            /* MANEJO DEL LOGIN Y REGISTRAR*/

            app.before("/login", ctx -> {

                print("Entro a verificar el login");
                String usuario = ctx.formParam("user");
                String contrasena = ctx.formParam("password");

                if(!control.verificarUsuario(usuario, contrasena)){
                    ctx.redirect("login.html");
                }else{
                    ctx.removeCookie("usuarioGuardado", "/");
                }

            });

            app.get("/login", ctx -> {

                ctx.redirect("login.html");

            });

            app.post("/login", ctx -> {

                print("Entro a verificar el login");
                String usuario = ctx.formParam("user");
                String contrasena = ctx.formParam("password");
                //print("Usuario: "+ usuario);
                if( ctx.formParam("recordar")!=null){
                    BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
                    textEncryptor.setPassword("clave");
                    String myEncryptedUser = textEncryptor.encrypt(usuario);
                    /*BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
                    String encryptedPassword = passwordEncryptor.encryptPassword(contrasena);*/

                    ctx.cookie("usuarioGuardado", myEncryptedUser, 604800);
                    //ctx.cookie("contrasenaGuardado", encryptedPassword, 168);


                }
                ctx.req.getSession().invalidate();
                ctx.sessionAttribute("user",usuario);
                String id = ctx.req.getSession().getId();
                carroCompra.setId(id);
                print(servicios.getUsuariobyUser(usuario).getNombre());
                print(carroCompra.getId());
                ctx.redirect("/");

            });
            /* REGISTRAR*/
            app.get("/registar", ctx -> {

                ctx.redirect("/nuevousuario.html");

            });

            app.post("/registar", ctx -> {

                String nombre = ctx.formParam("nombre");
                String user = ctx.formParam("usuario");
                String password = ctx.formParam("contrasena");
                if(!servicios.existeUsuario(user)){
                    Usuario tmp = new Usuario(user,nombre,password);
                    servicios.crearUsuario(tmp);
                    control.setUsuarios(servicios.cargarUsuarios());
                    print("Se creo usuario");
                    print(""+control.getUsuarios().size());
                    ctx.redirect("/login.html");
                }else{
                    Map<String, Object> modelo = new HashMap<>();
                    modelo.put("error", "ERROR: YA EXISTE UN PRODUCTO CON ESE ID");
                    ctx.render("Publico/nuevousuario.html", modelo);
                }

            });


            /* MANEJO DE LA PARTE DE GESTION DE PRODUCTOS*/
            app.before("/admprod", ctx -> {
                print("Entro a verificar");
                if(ctx.sessionAttribute("user")==null) {
                    print("No usuario");
                    ctx.redirect("/login.html");
                }
            });

            app.get("/admprod", ctx ->{

                List<Producto> lista = control.getProductos();

                if(ctx.sessionAttribute("user").equals("admin")){
                    Map<String, Object> modelo = new HashMap<>();
                    modelo.put("titulo", "Lista de productos");
                    modelo.put("lista", lista);
                    ctx.render("Publico/admproductos.html", modelo);
                }else{
                    ctx.result("No tienes permiso para acceder a esta pagina");
                }

            });
            app.get("/nuevoproducto", ctx -> {
                Map<String, Object> modelo = new HashMap<>();
                modelo.put("titulo", "Crear nuevo producto");
                modelo.put("boton", "Crear");
                modelo.put("accion", "/nuevoproducto");
                modelo.put("error", "");
                ctx.render("Publico/editarcrearprod.html",modelo);
            });

            app.post("/nuevoproducto", ctx -> {
                Map<String, Object> modelo = new HashMap<>();
                boolean aux = false;
                int id = ctx.formParam("id", Integer.class).get();
                String nombre = ctx.formParam("nombre");
                int cantidad = ctx.formParam("cantidad", Integer.class).get();
                String auxprecio = ctx.formParam("precio");
                BigDecimal precio = new BigDecimal(auxprecio);
                for (Producto producto: control.getProductos()) {
                    if(producto.getId() == id){
                        aux = true;
                        break;
                    }else{
                        aux = false;
                    }
                }
                if(aux){
                    modelo.put("titulo", "Crear nuevo producto");
                    modelo.put("boton", "Crear");
                    modelo.put("error", "ERROR: YA EXISTE UN PRODUCTO CON ESE ID");
                    ctx.render("Publico/editarcrearprod.html", modelo);

                }else{
                    Producto prod = new Producto(id,nombre,cantidad,precio);
                    servicios.crearProducto(prod);
                    control.setProductos(servicios.cargarProductos());
                    modelo.put("titulo", "Crear nuevo producto");
                    modelo.put("boton", "Crear");
                    modelo.put("error", "");
                    ctx.render("Publico/editarcrearprod.html",modelo);
                }

            });

            app.get("/editar/:id", ctx-> {
                Map<String, Object> modelo = new HashMap<>();
                Producto producto = control.buscaProductobyid(ctx.pathParam("id", Integer.class).get());
                modelo.put("titulo", "Editar producto");
                modelo.put("boton", "Editar");
                modelo.put("idval", Integer.toString(producto.getId()));
                modelo.put("nombreval", producto.getNombre());
                modelo.put("cantidadval", Integer.toString(producto.getCantidad()));
                modelo.put("precioval", producto.getPrecio().toString());
                modelo.put("accion", "/editar");
                ctx.render("Publico/editarcrearprod.html", modelo);

            });

            app.post("/editar", ctx -> {
                Map<String, Object> modelo = new HashMap<>();

                int id = ctx.formParam("id", Integer.class).get();
                String nombre = ctx.formParam("nombre");
                int cantidad = ctx.formParam("cantidad", Integer.class).get();
                String auxprecio = ctx.formParam("precio");
                BigDecimal precio = new BigDecimal(auxprecio);
                Producto tmp = new Producto(id,nombre,cantidad,precio);

                if(control.buscaProductobyid(id) == null){
                    modelo.put("error","Error: Producto no encontrado");
                    modelo.put("accion", "/editar");
                    modelo.put("titulo", "Editar producto");
                    modelo.put("boton", "Editar");
                    ctx.render("Publico/editarcrearprod.html", modelo);
                }else{
                    servicios.actualizarProducto(tmp);
                    control.setProductos(servicios.cargarProductos());
                    ctx.redirect("/admprod");
                }

            });

            app.get("/eliminar/:id", ctx-> {
                Map<String, Object> modelo = new HashMap<>();
                if(servicios.borrarProducto(ctx.pathParam("id", Integer.class).get())){
                    print("Eliminado");
                    control.setProductos(servicios.cargarProductos());
                }else{
                    print("Hay problemas");
                }
                ctx.redirect("/admprod");

            });

            /*CONTROLANDO LOS ITEMS ANADIDOS AL CARRITO DE UN USUARIO PARTICULAR*/
            app.post("/agregarcar", ctx -> {
                Map<String, Object> modelo = new HashMap<>();

                Producto producto = control.buscaProductobyid(ctx.formParam("id", Integer.class).get());
                //int cant = 2;
                int cantidad = ctx.formParam("cantidad", Integer.class).get();
                print(""+cantidad);
                for(int i =0; i<cantidad; i++){
                    carroCompra.setListaProductos(producto);
                }
                ctx.redirect("/");

            });
            /*CONTROLANDO EL ACCESO AL CARRITO*/
            app.before("/carrito", ctx -> {
                print("Entro a verificar");
                if(ctx.sessionAttribute("user")==null) {
                    print("No usuario");
                    ctx.redirect("/login.html");
                }
            });

            app.get("/carrito", ctx -> {
                //tomando el parametro utl y validando el tipo.
                List<Producto> lista = carroCompra.getListaProductos();
                String u = ctx.sessionAttribute("user").toString();
                Usuario user = control.buscarUsuariobyUser(u);
                BigDecimal total = new BigDecimal(0);
                for(Producto p : lista ){
                    total = total.add(p.getPrecio());
                }
                Map<String, Object> modelo = new HashMap<>();
                int aux = carroCompra.getListaProductos().size();
                modelo.put("cantidad",aux);
                modelo.put("titulo", "Carro de compras");
                modelo.put("nombrecliente", user.getNombre());
                modelo.put("total", total);
                modelo.put("lista", lista);
                //enviando al sistema de plantilla.
                ctx.render("Publico/carro.html", modelo);
            });

            app.get("/eliminaritemcarro/:id", ctx -> {
                Map<String, Object> modelo = new HashMap<>();
                Producto producto = control.buscaProductobyid(ctx.pathParam("id", Integer.class).get());
                carroCompra.getListaProductos().remove(producto);
                ctx.redirect("/carrito");

            });

            app.get("/vender", ctx ->{
                String id = ctx.req.getSession().getId();
                Date fechacompra = new Date();
                String u = ctx.sessionAttribute("user").toString();
                Usuario user = control.buscarUsuariobyUser(u);
                if(user!=null){
                    ArrayList<Producto> tmp = new ArrayList<>();

                    BigDecimal total = new BigDecimal(0);
                    for(Producto p : carroCompra.getListaProductos()){
                        tmp.add(p);
                        total = total.add(p.getPrecio());
                    }
                    VentasProductos venta = new VentasProductos(id,fechacompra,user.getNombre(),tmp,total.floatValue());
                    if(servicios.crearVenta(venta)){
                        print("Se creo");
                    }
                    print("Cargar ventas: "+servicios.cargarVentas().size());
                    control.setVentas(servicios.cargarVentas());
                    carroCompra.getListaProductos().clear();
                }

                ctx.redirect("/carrito");

            });

            /*MANEJA LA VISTA DE LAS VENTAS*/
            app.before("/verventas", ctx -> {

                if(ctx.sessionAttribute("user")==null) {
                    print("No usuario");
                    ctx.redirect("/login.html");
                }
            });

            app.get("/verventas", ctx -> {

                Map<String, Object> modelo = new HashMap<>();

                if(ctx.sessionAttribute("user").equals("admin")){
                    List<VentasProductos> lista = control.getVentas();
                    modelo.put("titulo", "Historial de ventas");
                    modelo.put("lista", lista);
                    ctx.render("Publico/verventas.html", modelo);
            }else{
                    ctx.result("No tienes permiso para acceder a esta pagina");
                }
            });


    }

        public static void print (String string){

            System.out.println(string);
        }



}





