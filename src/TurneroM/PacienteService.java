package TurneroM;

import java.util.List;

public class PacienteService {
    private final PacienteDao dao;

    public PacienteService(PacienteDao dao) {
        this.dao = dao;
    }

    public Paciente crear(String nombre, String apellido, String dni, String telefono, String obraSocial) {
        validar(nombre, apellido, dni);
        if (dao.existsByDni(dni)) throw new RuntimeException("DNI duplicado");
        Paciente p = new Paciente(0L, nombre, apellido, dni, telefono, obraSocial);
        return dao.insert(p);
    }

    public Paciente actualizar(long id, String nombre, String apellido, String dni, String telefono, String obraSocial) {
        validar(nombre, apellido, dni);
        Paciente existente = dao.findById(id).orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        if (!existente.getDni().equals(dni) && dao.existsByDni(dni))
            throw new RuntimeException("DNI duplicado");
        existente.setNombre(nombre);
        existente.setApellido(apellido);
        existente.setDni(dni);
        existente.setTelefono(telefono);
        existente.setObraSocial(obraSocial);
        return dao.update(existente);
    }

    public void eliminar(long id) {
        dao.findById(id).orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        dao.deleteById(id);
    }

    public List<Paciente> listar() {
        return dao.findAll();
    }

    // ------------------------

    private void validar(String nombre, String apellido, String dni) {
        if (nombre == null || nombre.isBlank()) throw new RuntimeException("Nombre obligatorio");
        if (apellido == null || apellido.isBlank()) throw new RuntimeException("Apellido obligatorio");
        if (dni == null || dni.isBlank()) throw new RuntimeException("DNI obligatorio");
    }
}
