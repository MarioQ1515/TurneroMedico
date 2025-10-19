package TurneroM;

import java.time.LocalDate;
import java.time.LocalTime;

public class Turno {
    private long id;
    private LocalDate fecha;
    private LocalTime hora;
    private Paciente paciente;
    private Medico medico;
    private Consultorio consultorio;
    private EstadoTurno estado;
    private double costoFinal;


    public Turno() { }

    public Turno(long id, LocalDate fecha, LocalTime hora,
                 Paciente paciente, Medico medico, Consultorio consultorio,
                 EstadoTurno estado, double costoFinal) {
        this.id = id;
        this.fecha = fecha;
        this.hora = hora;
        this.paciente = paciente;
        this.medico = medico;
        this.consultorio = consultorio;
        this.estado = estado;
        this.costoFinal = costoFinal;
    }

    public Turno(LocalDate fecha, LocalTime hora,
                 Paciente paciente, Medico medico, Consultorio consultorio,
                 EstadoTurno estado, double costoFinal) {
        this(0L, fecha, hora, paciente, medico, consultorio, estado, costoFinal);
    }


    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }

    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }

    public Medico getMedico() { return medico; }
    public void setMedico(Medico medico) { this.medico = medico; }

    public Consultorio getConsultorio() { return consultorio; }
    public void setConsultorio(Consultorio consultorio) { this.consultorio = consultorio; }

    public EstadoTurno getEstado() { return estado; }
    public void setEstado(EstadoTurno estado) { this.estado = estado; }

    public double getCostoFinal() { return costoFinal; }
    public void setCostoFinal(double costoFinal) { this.costoFinal = costoFinal; }


    @Override
    public String toString() {
        return "Turno{" +
                "fecha=" + fecha +
                ", hora=" + hora +
                ", paciente=" + (paciente != null ? paciente.getNombre() + " " + paciente.getApellido() : "N/A") +
                ", medico=" + (medico != null ? medico.getNombre() + " " + medico.getApellido() : "N/A") +
                ", consultorio=" + (consultorio != null ? consultorio.getNombre() : "N/A") +
                ", estado=" + estado +
                ", costoFinal=" + costoFinal +
                '}';
    }
}
