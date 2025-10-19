package TurneroM;

import java.util.List;
import java.util.Optional;

public interface ConsultorioDao {
    Consultorio insert(Consultorio c);
    Consultorio update(Consultorio c);
    void deleteById(long id);
    Optional<Consultorio> findById(long id);
    List<Consultorio> findAll();
}
