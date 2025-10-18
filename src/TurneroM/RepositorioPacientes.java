package TurneroM;

import java.util.ArrayList;
import java.util.List;

public class RepositorioPacientes {
    private final List<Paciente> pacientes = new ArrayList<>();

    public void agregar(Paciente p) {
        pacientes.add(p);
    }

    public List<Paciente> listar() {
        return new ArrayList<>(pacientes);
    }
}
