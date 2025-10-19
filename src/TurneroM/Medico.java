package TurneroM;

public class Medico {
    private long id;
    private String nombre;
    private String apellido;
    private Especialidad especialidad;
    private double costoConsulta;
    private String obraSocialAtendida;

    public Medico() { }

    public Medico(long id, String nombre, String apellido, Especialidad especialidad, double costoConsulta, String obraSocialAtendida) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.especialidad = especialidad;
        this.costoConsulta = costoConsulta;
        this.obraSocialAtendida = obraSocialAtendida;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public Especialidad getEspecialidad() { return especialidad; }
    public void setEspecialidad(Especialidad especialidad) { this.especialidad = especialidad; }

    public double getCostoConsulta() { return costoConsulta; }
    public void setCostoConsulta(double costoConsulta) { this.costoConsulta = costoConsulta; }

    public String getObraSocialAtendida() { return obraSocialAtendida; }
    public void setObraSocialAtendida(String obraSocialAtendida) { this.obraSocialAtendida = obraSocialAtendida; }

    @Override
    public String toString() {
        return nombre + " " + apellido + " - " + especialidad + " ($" + costoConsulta + ")";
    }
}