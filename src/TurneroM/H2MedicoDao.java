package TurneroM;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class H2MedicoDao implements MedicoDao {

    @Override
    public Medico insert(Medico m) {
        final String sql = "INSERT INTO medicos (nombre, apellido, especialidad, costo_consulta, obra_social_atendida) VALUES (?,?,?,?,?)";
        try (Connection cn = ConexionH2.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, m.getNombre());
            ps.setString(2, m.getApellido());
            ps.setString(3, m.getEspecialidad().name());
            ps.setBigDecimal(4, java.math.BigDecimal.valueOf(m.getCostoConsulta()));
            ps.setString(5, m.getObraSocialAtendida());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) m.setId(rs.getLong(1)); }
            return m;
        } catch (SQLException e) {
            throw new RuntimeException("Error insertando médico: " + e.getMessage(), e);
        }
    }

    @Override
    public Medico update(Medico m) {
        final String sql = "UPDATE medicos SET nombre=?, apellido=?, especialidad=?, costo_consulta=?, obra_social_atendida=? WHERE id=?";
        try (Connection cn = ConexionH2.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, m.getNombre());
            ps.setString(2, m.getApellido());
            ps.setString(3, m.getEspecialidad().name());
            ps.setBigDecimal(4, java.math.BigDecimal.valueOf(m.getCostoConsulta()));
            ps.setString(5, m.getObraSocialAtendida());
            ps.setLong(6, m.getId());
            if (ps.executeUpdate() == 0) throw new RuntimeException("Médico no encontrado (id=" + m.getId() + ")");
            return m;
        } catch (SQLException e) {
            throw new RuntimeException("Error actualizando médico: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(long id) {
        final String sql = "DELETE FROM medicos WHERE id=?";
        try (Connection cn = ConexionH2.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error eliminando médico: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Medico> findById(long id) {
        final String sql = "SELECT id,nombre,apellido,especialidad,costo_consulta,obra_social_atendida FROM medicos WHERE id=?";
        try (Connection cn = ConexionH2.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? Optional.of(map(rs)) : Optional.empty(); }
        } catch (SQLException e) {
            throw new RuntimeException("Error buscando médico: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Medico> findAll() {
        final String sql = "SELECT id,nombre,apellido,especialidad,costo_consulta,obra_social_atendida FROM medicos ORDER BY id";
        List<Medico> out = new ArrayList<>();
        try (Connection cn = ConexionH2.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(map(rs));
            return out;
        } catch (SQLException e) {
            throw new RuntimeException("Error listando médicos: " + e.getMessage(), e);
        }
    }

    // ---- mapper
    private Medico map(ResultSet rs) throws SQLException {
        Medico m = new Medico();
        m.setId(rs.getLong("id"));
        m.setNombre(rs.getString("nombre"));
        m.setApellido(rs.getString("apellido"));
        m.setEspecialidad(Especialidad.valueOf(rs.getString("especialidad")));
        m.setCostoConsulta(rs.getBigDecimal("costo_consulta").doubleValue());
        m.setObraSocialAtendida(rs.getString("obra_social_atendida"));
        return m;
    }
}
