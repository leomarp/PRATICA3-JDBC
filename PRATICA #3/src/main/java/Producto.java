import java.math.BigDecimal;

public class Producto {
    private int id;
    private String nombre;
    private int cantidad;
    private BigDecimal precio;

    public Producto(int id, String nombre, int cantidad, BigDecimal precio) {
        this.id = id;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precio = precio;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
    public void mezclar(Producto p){
        this.id = p.getId();
        this.nombre = p.getNombre();
        this.cantidad = p.getCantidad();
        this.precio = p.getPrecio();
    }
}