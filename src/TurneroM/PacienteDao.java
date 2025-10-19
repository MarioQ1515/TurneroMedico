package TurneroM;

import java.util.List;
import java.util.Optional;

public interface PacienteDao {
    Paciente insert(Paciente p);
    Paciente update(Paciente p);
    void deleteById(long id);
    Optional<Paciente> findById(long id);
    List<Paciente> findAll();
    boolean existsByDni(String dni);
}
