package TurneroM;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class H2ConsultorioDao implements ConsultorioDao {

    @Override
    public Consultorio insert(Consultorio c) {
        final String sql = "INSERT INTO consultorios (nombre) VALUES (?)";
        try (Connection cn = ConexionH2.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getNombre());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) c.setId(rs.getLong(1));
            }
            return c;
        } catch (SQLException e) {
            throw new RuntimeException("Error insertando consultorio: " + e.getMessage(), e);
        }
    }

    @Override
    public Consultorio update(Consultorio c) {
        final String sql = "UPDATE consultorios SET nombre=? WHERE id=?";
        try (Connection cn = ConexionH2.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setLong(2, c.getId());
            if (ps.executeUpdate() == 0) throw new RuntimeException("Consultorio no encontrado");
            return c;
        } catch (SQLException e) {
            throw new RuntimeException("Error actualizando consultorio: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(long id) {
        final String sql = "DELETE FROM consultorios WHERE id=?";
        try (Connection cn = ConexionH2.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error eliminando consultorio: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Consultorio> findById(long id) {
        final String sql = "SELECT id, nombre FROM consultorios WHERE id=?";
        try (Connection cn = ConexionH2.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error buscando consultorio: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Consultorio> findAll() {
        final String sql = "SELECT id, nombre FROM consultorios ORDER BY id";
        List<Consultorio> out = new ArrayList<>();
        try (Connection cn = ConexionH2.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(map(rs));
            return out;
        } catch (SQLException e) {
            throw new RuntimeException("Error listando consultorios: " + e.getMessage(), e);
        }
    }

    private Consultorio map(ResultSet rs) throws SQLException {
        Consultorio c = new Consultorio();
        c.setId(rs.getLong("id"));
        c.setNombre(rs.getString("nombre"));
        return c;
    }
}
