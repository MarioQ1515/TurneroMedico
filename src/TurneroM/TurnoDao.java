package TurneroM;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface TurnoDao {
    Turno insert(Turno t);
    Turno update(Turno t);
    void deleteById(long id);
    Optional<Turno> findById(long id);
    List<Turno> findAll();

    boolean existsByMedicoAndFechaHora(long medicoId, LocalDate fecha, LocalTime hora);
    List<Turno> findByMedicoBetween(long medicoId, LocalDate desde, LocalDate hasta);
}
