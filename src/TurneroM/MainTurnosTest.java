package TurneroM;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class MainTurnosTest {
    public static void main(String[] args) {
        // Servicios/DAOs
        PacienteService pacienteService = new PacienteService(new H2PacienteDao());
        MedicoService   medicoService   = new MedicoService(new H2MedicoDao());
        ConsultorioDao  consDao         = new H2ConsultorioDao();
        TurnoService    turnoService    = new TurnoService(new H2TurnoDao());

        // 1) Asegurar datos base (si no existen)
        Paciente paciente = asegurarPaciente(pacienteService);
        Medico   medico   = asegurarMedico(medicoService);
        Consultorio cons  = asegurarConsultorio(consDao);

        System.out.println("✅ Datos base: Paciente=" + paciente.getId() + ", Medico=" + medico.getId() + ", Consultorio=" + cons.getId());

        // 2) Crear un turno para mañana a las 10:00
        LocalDate fecha = LocalDate.now().plusDays(1);
        LocalTime hora  = LocalTime.of(10, 0);

        Turno t1 = turnoService.crear(fecha, hora, paciente, medico, cons);
        System.out.println("✅ Turno creado ID=" + t1.getId() + " | costoFinal=" + t1.getCostoFinal() + " | estado=" + t1.getEstado());

        // 3) Intentar duplicado (mismo médico+fecha+hora) -> debe fallar
        try {
            turnoService.crear(fecha, hora, paciente, medico, cons);
            System.out.println("❌ ERROR: no debería permitir turno duplicado");
        } catch (RuntimeException ex) {
            System.out.println("🟡 Duplicado detectado OK: " + ex.getMessage());
        }

        // 4) Cambiar estado a CONFIRMADO y luego ATENDIDO
        turnoService.cambiarEstado(t1.getId(), EstadoTurno.CONFIRMADO);
        turnoService.cambiarEstado(t1.getId(), EstadoTurno.ATENDIDO);
        System.out.println("✅ Turno " + t1.getId() + " marcado como ATENDIDO.");

        // 5) Recaudación del médico entre fechas (hoy y +3 días)
        double total = turnoService.recaudacionMedico(medico.getId(), LocalDate.now(), LocalDate.now().plusDays(3));
        System.out.println("💰 Recaudación del médico " + medico.getApellido() + " entre hoy y +3 días: " + total);

        // 6) Listar turnos
        List<Turno> lista = turnoService.listar();
        System.out.println("📋 Turnos en sistema:");
        for (Turno t : lista) {
            System.out.println(" - ID=" + t.getId()
                    + " | " + t.getFecha() + " " + t.getHora()
                    + " | Med=" + t.getMedico().getId()
                    + " | Pac=" + t.getPaciente().getId()
                    + " | Cons=" + t.getConsultorio().getId()
                    + " | Estado=" + t.getEstado()
                    + " | $" + t.getCostoFinal());
        }

        System.out.println("✅ Prueba de Turnos finalizada.");
    }

    private static Paciente asegurarPaciente(PacienteService service) {
        List<Paciente> todos = service.listar();
        if (!todos.isEmpty()) return todos.get(0);
        // crea uno de ejemplo
        return service.crear("Ana", "García", "TST-111", "091111111", "ASSE");
    }

    private static Medico asegurarMedico(MedicoService service) {
        List<Medico> todos = service.listar();
        if (!todos.isEmpty()) return todos.get(0);
        // crea uno de ejemplo (obra social atendida = ASSE para probar descuento)
        return service.crear("Luis", "Pérez", Especialidad.CLINICA, 2000.0, "ASSE");
    }

    private static Consultorio asegurarConsultorio(ConsultorioDao dao) {
        List<Consultorio> todos = dao.findAll();
        if (!todos.isEmpty()) return todos.get(0);
        // crea uno de ejemplo
        Consultorio c = new Consultorio(0L, "Consultorio 101");
        return dao.insert(c);
    }
}
