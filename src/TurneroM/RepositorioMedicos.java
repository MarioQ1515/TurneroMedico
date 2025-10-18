package TurneroM;

import java.util.ArrayList;
import java.util.List;

public class RepositorioMedicos {
    private final List<Medico> medicos = new ArrayList<>();

    public void agregar(Medico m) {
        medicos.add(m);
    }

    public List<Medico> listar() {
        return new ArrayList<>(medicos);
    }
}
