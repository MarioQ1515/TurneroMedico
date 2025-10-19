package TurneroM;

import java.util.List;

public class MainPacienteCRUD {
    public static void main(String[] args) {
        PacienteService service = new PacienteService(new H2PacienteDao());

        // 1) Crear
        Paciente a = service.crear("Ana", "García", "111", "091111111", "ASSE");
        Paciente b = service.crear("Luis", "Pérez", "222", "092222222", "SMP");
        System.out.println("Creado A id=" + a.getId());
        System.out.println("Creado B id=" + b.getId());

        // 2) Listar
        List<Paciente> lista = service.listar();
        System.out.println("Pacientes:");
        for (Paciente p : lista) {
            System.out.println(" - " + p.getId() + " | " + p.getNombre() + " " + p.getApellido() + " | DNI " + p.getDni());
        }

        // 3) Actualizar
        a = service.actualizar(a.getId(), "Ana", "García", "111", "099999999", "ASSE");
        System.out.println("Actualizado A tel=" + a.getTelefono());

        // 4) Eliminar
        service.eliminar(b.getId());
        System.out.println("Eliminado B id=" + b.getId());

        // 5) Listar final
        System.out.println("Pacientes (final):");
        for (Paciente p : service.listar()) {
            System.out.println(" - " + p.getId() + " | " + p.getNombre() + " " + p.getApellido());
        }

        System.out.println("✅ CRUD Paciente OK contra H2 (jdbc:h2:~/mi_base)");
    }
}
