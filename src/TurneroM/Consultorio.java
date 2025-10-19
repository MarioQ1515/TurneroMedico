package TurneroM;

public class Consultorio {
    private long id;
    private String nombre; // o número de consultorio

    public Consultorio() { }

    public Consultorio(long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    @Override
    public String toString() {
        return nombre;
    }
}
