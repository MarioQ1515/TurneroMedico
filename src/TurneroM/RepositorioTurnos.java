package TurneroM;

import java.util.ArrayList;
import java.util.List;

public class RepositorioTurnos {
    private final List<Turno> turnos = new ArrayList<>();

    public void agregar(Turno t) {
        turnos.add(t);
    }

    public List<Turno> listar() {
        return new ArrayList<>(turnos);
    }
}
