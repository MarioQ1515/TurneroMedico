package TurneroM;

import java.util.List;
import java.util.Optional;

public interface MedicoDao {
    Medico insert(Medico m);
    Medico update(Medico m);
    void deleteById(long id);
    Optional<Medico> findById(long id);
    List<Medico> findAll();
}
