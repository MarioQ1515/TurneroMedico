package TurneroM;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class H2TurnoDao implements TurnoDao {

    @Override
    public Turno insert(Turno t) {
        final String sql = "INSERT INTO turnos " +
                "(fecha, hora, paciente_id, medico_id, consultorio_id, estado, costo_final) " +
                "VALUES (?,?,?,?,?,?,?)";
        try (Connection cn = ConexionH2.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDate(1, Date.valueOf(t.getFecha()));
            ps.setTime(2, Time.valueOf(t.getHora()));
            ps.setLong(3, t.getPaciente().getId());
            ps.setLong(4, t.getMedico().getId());
            ps.setLong(5, t.getConsultorio().getId());
            ps.setString(6, t.getEstado().name());
            ps.setBigDecimal(7, java.math.BigDecimal.valueOf(t.getCostoFinal()));
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) t.setId(rs.getLong(1));
            }
            return t;

        } catch (SQLException e) {
            // H2: UNIQUE constraint -> SQLState 23505
            if ("23505".equals(e.getSQLState())) {
                throw new RuntimeException("El médico ya tiene un turno en esa fecha y hora", e);
            }
            throw new RuntimeException("Error insertando turno: " + e.getMessage(), e);
        }
    }

    @Override
    public Turno update(Turno t) {
        final String sql = "UPDATE turnos SET fecha=?, hora=?, paciente_id=?, medico_id=?, " +
                "consultorio_id=?, estado=?, costo_final=? WHERE id=?";
        try (Connection cn = ConexionH2.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(t.getFecha()));
            ps.setTime(2, Time.valueOf(t.getHora()));
            ps.setLong(3, t.getPaciente().getId());
            ps.setLong(4, t.getMedico().getId());
            ps.setLong(5, t.getConsultorio().getId());
            ps.setString(6, t.getEstado().name());
            ps.setBigDecimal(7, java.math.BigDecimal.valueOf(t.getCostoFinal()));
            ps.setLong(8, t.getId());

            if (ps.executeUpdate() == 0) throw new RuntimeException("Turno no encontrado");
            return t;

        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                throw new RuntimeException("El médico ya tiene un turno en esa fecha y hora", e);
            }
            throw new RuntimeException("Error actualizando turno: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(long id) {
        final String sql = "DELETE FROM turnos WHERE id=?";
        try (Connection cn = ConexionH2.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error eliminando turno: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Turno> findById(long id) {
        final String sql = "SELECT id, fecha, hora, paciente_id, medico_id, consultorio_id, estado, costo_final " +
                "FROM turnos WHERE id=?";
        try (Connection cn = ConexionH2.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error buscando turno: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Turno> findAll() {
        final String sql = "SELECT id, fecha, hora, paciente_id, medico_id, consultorio_id, estado, costo_final " +
                "FROM turnos ORDER BY fecha, hora";
        List<Turno> out = new ArrayList<>();
        try (Connection cn = ConexionH2.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(map(rs));
            return out;
        } catch (SQLException e) {
            throw new RuntimeException("Error listando turnos: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsByMedicoAndFechaHora(long medicoId, LocalDate fecha, LocalTime hora) {
        final String sql = "SELECT 1 FROM turnos WHERE medico_id=? AND fecha=? AND hora=? LIMIT 1";
        try (Connection cn = ConexionH2.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, medicoId);
            ps.setDate(2, Date.valueOf(fecha));
            ps.setTime(3, Time.valueOf(hora));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error verificando solapamiento: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Turno> findByMedicoBetween(long medicoId, LocalDate desde, LocalDate hasta) {
        final String sql = "SELECT id, fecha, hora, paciente_id, medico_id, consultorio_id, estado, costo_final " +
                "FROM turnos WHERE medico_id=? AND fecha BETWEEN ? AND ? ORDER BY fecha, hora";
        List<Turno> out = new ArrayList<>();
        try (Connection cn = ConexionH2.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, medicoId);
            ps.setDate(2, Date.valueOf(desde));
            ps.setDate(3, Date.valueOf(hasta));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
                return out;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error listando turnos por médico/fechas: " + e.getMessage(), e);
        }
    }

    // ---------- mapper ----------
    private Turno map(ResultSet rs) throws SQLException {
        Turno t = new Turno();
        t.setId(rs.getLong("id"));
        t.setFecha(rs.getDate("fecha").toLocalDate());
        t.setHora(rs.getTime("hora").toLocalTime());

        Paciente p = new Paciente(); p.setId(rs.getLong("paciente_id"));
        Medico   m = new Medico();   m.setId(rs.getLong("medico_id"));
        Consultorio c = new Consultorio(); c.setId(rs.getLong("consultorio_id"));

        t.setPaciente(p);
        t.setMedico(m);
        t.setConsultorio(c);
        t.setEstado(EstadoTurno.valueOf(rs.getString("estado")));
        t.setCostoFinal(rs.getBigDecimal("costo_final").doubleValue());
        return t;
    }
}
