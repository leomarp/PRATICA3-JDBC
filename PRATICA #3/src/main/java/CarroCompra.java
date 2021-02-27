import java.util.ArrayList;


public class CarroCompra {
    private String id;
    private ArrayList<Producto> listaProductos;

    public CarroCompra(String id) {
        this.id = id;
        this.listaProductos = new ArrayList<Producto>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<Producto> getListaProductos() {
        return listaProductos;
    }

    public void setListaProductos(Producto producto) {
        this.listaProductos.add(producto);
    }
}
