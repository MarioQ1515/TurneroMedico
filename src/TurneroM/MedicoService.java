package TurneroM;

import java.util.List;

public class MedicoService {
    private final MedicoDao dao;

    public MedicoService(MedicoDao dao) { this.dao = dao; }

    public Medico crear(String nombre, String apellido, Especialidad esp, double costo, String obraSocialAtendida) {
        validar(nombre, apellido, esp, costo);
        Medico m = new Medico(0L, nombre, apellido, esp, costo, obraSocialAtendida);
        return dao.insert(m);
    }

    public Medico actualizar(long id, String nombre, String apellido, Especialidad esp, double costo, String obraSocialAtendida) {
        validar(nombre, apellido, esp, costo);
        Medico m = dao.findById(id).orElseThrow(() -> new RuntimeException("Médico no encontrado"));
        m.setNombre(nombre);
        m.setApellido(apellido);
        m.setEspecialidad(esp);
        m.setCostoConsulta(costo);
        m.setObraSocialAtendida(obraSocialAtendida);
        return dao.update(m);
    }

    public void eliminar(long id) {
        dao.findById(id).orElseThrow(() -> new RuntimeException("Médico no encontrado"));
        dao.deleteById(id);
    }

    public List<Medico> listar() { return dao.findAll(); }

    private void validar(String nombre, String apellido, Especialidad esp, double costo) {
        if (nombre == null || nombre.isBlank()) throw new RuntimeException("Nombre obligatorio");
        if (apellido == null || apellido.isBlank()) throw new RuntimeException("Apellido obligatorio");
        if (esp == null) throw new RuntimeException("Especialidad obligatoria");
        if (costo <= 0) throw new RuntimeException("Costo de consulta inválido");
    }
}