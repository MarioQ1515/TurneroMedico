package TurneroM;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class H2PacienteDao implements PacienteDao {

    @Override
    public Paciente insert(Paciente p) {
        final String sql = "INSERT INTO pacientes (nombre, apellido, dni, telefono, obra_social) VALUES (?,?,?,?,?)";
        try (Connection cn = ConexionH2.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getNombre());
            ps.setString(2, p.getApellido());
            ps.setString(3, p.getDni());
            ps.setString(4, p.getTelefono());
            ps.setString(5, p.getObraSocial());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) p.setId(rs.getLong(1));
            }
            return p;

        } catch (SQLException e) {
            throw new RuntimeException("Error insertando paciente: " + e.getMessage(), e);
        }
    }

    @Override
    public Paciente update(Paciente p) {
        final String sql = "UPDATE pacientes SET nombre=?, apellido=?, dni=?, telefono=?, obra_social=? WHERE id=?";
        try (Connection cn = ConexionH2.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, p.getNombre());
            ps.setString(2, p.getApellido());
            ps.setString(3, p.getDni());
            ps.setString(4, p.getTelefono());
            ps.setString(5, p.getObraSocial());
            ps.setLong(6, p.getId());

            int rows = ps.executeUpdate();
            if (rows == 0) throw new RuntimeException("Paciente no encontrado para actualizar (id=" + p.getId() + ")");
            return p;

        } catch (SQLException e) {
            throw new RuntimeException("Error actualizando paciente: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(long id) {
        final String sql = "DELETE FROM pacientes WHERE id=?";
        try (Connection cn = ConexionH2.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error eliminando paciente: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Paciente> findById(long id) {
        final String sql = "SELECT id, nombre, apellido, dni, telefono, obra_social FROM pacientes WHERE id=?";
        try (Connection cn = ConexionH2.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error buscando paciente por id: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Paciente> findAll() {
        final String sql = "SELECT id, nombre, apellido, dni, telefono, obra_social FROM pacientes ORDER BY id";
        List<Paciente> out = new ArrayList<>();
        try (Connection cn = ConexionH2.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) out.add(map(rs));
            return out;

        } catch (SQLException e) {
            throw new RuntimeException("Error listando pacientes: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsByDni(String dni) {
        final String sql = "SELECT 1 FROM pacientes WHERE dni=? LIMIT 1";
        try (Connection cn = ConexionH2.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, dni);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error verificando DNI: " + e.getMessage(), e);
        }
    }

    private Paciente map(ResultSet rs) throws SQLException {
        Paciente p = new Paciente();
        p.setId(rs.getLong("id"));
        p.setNombre(rs.getString("nombre"));
        p.setApellido(rs.getString("apellido"));
        p.setDni(rs.getString("dni"));
        p.setTelefono(rs.getString("telefono"));
        p.setObraSocial(rs.getString("obra_social"));
        return p;
    }
}