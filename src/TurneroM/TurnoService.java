package TurneroM;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class TurnoService {
    private final TurnoDao dao;

    public TurnoService(TurnoDao dao) { this.dao = dao; }

    public Turno crear(LocalDate fecha, LocalTime hora, Paciente pac, Medico med, Consultorio cons) {
        validarCampos(fecha, hora, pac, med, cons);

        if (dao.existsByMedicoAndFechaHora(med.getId(), fecha, hora)) {
            throw new RuntimeException("El médico ya tiene un turno en esa fecha y hora");
        }

        double costo = calcularCostoFinal(med, pac);
        Turno t = new Turno(0L, fecha, hora, pac, med, cons, EstadoTurno.RESERVADO, costo);
        return dao.insert(t);
    }

    public Turno actualizar(long id, LocalDate fecha, LocalTime hora, Paciente pac, Medico med, Consultorio cons, EstadoTurno estado) {
        validarCampos(fecha, hora, pac, med, cons);
        Turno actual = dao.findById(id).orElseThrow(() -> new RuntimeException("Turno no encontrado"));

        // Si cambian fecha/hora/médico, validar solapamiento
        boolean medicoCambia = actual.getMedico().getId() != med.getId();
        boolean fechaCambia  = !actual.getFecha().equals(fecha);
        boolean horaCambia   = !actual.getHora().equals(hora);
        if (medicoCambia || fechaCambia || horaCambia) {
            if (dao.existsByMedicoAndFechaHora(med.getId(), fecha, hora)) {
                throw new RuntimeException("El médico ya tiene un turno en esa fecha y hora");
            }
        }

        actual.setFecha(fecha);
        actual.setHora(hora);
        actual.setPaciente(pac);
        actual.setMedico(med);
        actual.setConsultorio(cons);
        actual.setEstado(estado != null ? estado : actual.getEstado());
        actual.setCostoFinal(calcularCostoFinal(med, pac));
        return dao.update(actual);
    }

    public void cambiarEstado(long turnoId, EstadoTurno nuevoEstado) {
        Turno t = dao.findById(turnoId).orElseThrow(() -> new RuntimeException("Turno no encontrado"));
        t.setEstado(nuevoEstado);
        dao.update(t);
    }

    public void eliminar(long id) {
        dao.findById(id).orElseThrow(() -> new RuntimeException("Turno no encontrado"));
        dao.deleteById(id);
    }

    public List<Turno> listar() { return dao.findAll(); }

    public double recaudacionMedico(long medicoId, LocalDate desde, LocalDate hasta) {
        double total = 0;
        for (Turno t : dao.findByMedicoBetween(medicoId, desde, hasta)) {
            if (t.getEstado() == EstadoTurno.ATENDIDO || t.getEstado() == EstadoTurno.CONFIRMADO) {
                total += t.getCostoFinal();
            }
        }
        return total;
    }

    // ---------- helpers ----------
    private void validarCampos(LocalDate fecha, LocalTime hora, Paciente pac, Medico med, Consultorio cons) {
        if (fecha == null) throw new RuntimeException("Fecha obligatoria");
        if (hora == null) throw new RuntimeException("Hora obligatoria");
        if (pac == null || pac.getId() == 0) throw new RuntimeException("Paciente obligatorio");
        if (med == null || med.getId() == 0) throw new RuntimeException("Médico obligatorio");
        if (cons == null || cons.getId() == 0) throw new RuntimeException("Consultorio obligatorio");
    }

    private double calcularCostoFinal(Medico med, Paciente pac) {
        String obraPac = pac.getObraSocial();
        String obraMed = med.getObraSocialAtendida();
        if (obraPac != null && obraMed != null && obraPac.trim().equalsIgnoreCase(obraMed.trim())) {
            return med.getCostoConsulta() * 0.5; // descuento 50%
        }
        return med.getCostoConsulta();
    }
}
