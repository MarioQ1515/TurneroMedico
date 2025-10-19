package TurneroM;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TurnoFrame extends JFrame {

    private final PacienteService pacienteService = new PacienteService(new H2PacienteDao());
    private final MedicoService medicoService = new MedicoService(new H2MedicoDao());
    private final ConsultorioDao consultorioDao = new H2ConsultorioDao();
    private final TurnoService turnoService = new TurnoService(new H2TurnoDao());

    private Map<Long, Paciente> cachePacientes = new HashMap<>();
    private Map<Long, Medico> cacheMedicos = new HashMap<>();
    private Map<Long, Consultorio> cacheConsultorios = new HashMap<>();

    private final DefaultTableModel model;
    private final JTable tabla;

    public TurnoFrame() {
        setTitle("Turnero Médico – Turnos");
        setSize(1000, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        model = new DefaultTableModel(new Object[]{
                "ID", "Fecha", "Hora", "Paciente", "Médico", "Consultorio", "Estado", "Costo"
        }, 0) { public boolean isCellEditable(int r, int c) { return false; } };

        tabla = new JTable(model);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setAutoCreateRowSorter(true);

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnNuevo = new JButton("Nuevo");
        JButton btnEstado = new JButton("Cambiar Estado");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnRefrescar = new JButton("Refrescar");

        barra.add(btnNuevo);
        barra.add(btnEstado);
        barra.add(btnEliminar);
        barra.add(btnRefrescar);

        add(barra, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        btnRefrescar.addActionListener(e -> cargarTabla());
        btnNuevo.addActionListener(e -> crearTurno());
        btnEstado.addActionListener(e -> cambiarEstadoSeleccionado());
        btnEliminar.addActionListener(e -> eliminarSeleccionado());

        cargarTabla();
    }

    private void cargarTabla() {
        try {
            cachePacientes = mapById(pacienteService.listar());
            cacheMedicos = mapById(medicoService.listar());
            cacheConsultorios = mapById(consultorioDao.findAll());

            model.setRowCount(0);
            for (Turno t : turnoService.listar()) {
                model.addRow(new Object[]{
                        t.getId(),
                        t.getFecha(),
                        t.getHora(),
                        nombrePaciente(t.getPaciente() != null ? t.getPaciente().getId() : 0L),
                        nombreMedico(t.getMedico() != null ? t.getMedico().getId() : 0L),
                        nombreConsultorio(t.getConsultorio() != null ? t.getConsultorio().getId() : 0L),
                        t.getEstado(),
                        t.getCostoFinal()
                });
            }
        } catch (RuntimeException ex) {
            showError(ex.getMessage());
        }
    }

    private void crearTurno() {
        try {
            JComboBox<Paciente> cbPaciente = new JComboBox<>(pacienteService.listar().toArray(new Paciente[0]));
            JComboBox<Medico> cbMedico = new JComboBox<>(medicoService.listar().toArray(new Medico[0]));
            JComboBox<Consultorio> cbConsultorio = new JComboBox<>();
            recargarConsultorios(cbConsultorio);

            JButton btnAddCons = new JButton("+");
            btnAddCons.addActionListener(e -> {
                String nombre = JOptionPane.showInputDialog(this, "Nombre del consultorio:");
                if (nombre != null && !nombre.isBlank()) {
                    Consultorio nuevo = new Consultorio(0L, nombre.trim());
                    consultorioDao.insert(nuevo);
                    long idNuevo = nuevo.getId();
                    recargarConsultorios(cbConsultorio);
                    seleccionarConsultorioPorId(cbConsultorio, idNuevo);
                }
            });

            JPanel filaCons = new JPanel(new BorderLayout(6, 0));
            filaCons.add(cbConsultorio, BorderLayout.CENTER);
            filaCons.add(btnAddCons, BorderLayout.EAST);

            JTextField tfFecha = new JTextField(LocalDate.now().plusDays(1).toString());
            JTextField tfHora  = new JTextField("10:00");

            JPanel form = new JPanel(new GridLayout(0,2,8,8));
            form.add(new JLabel("Paciente:"));    form.add(cbPaciente);
            form.add(new JLabel("Médico:"));      form.add(cbMedico);
            form.add(new JLabel("Consultorio:")); form.add(filaCons);
            form.add(new JLabel("Fecha (YYYY-MM-DD):")); form.add(tfFecha);
            form.add(new JLabel("Hora (HH:mm):"));       form.add(tfHora);

            int opt = JOptionPane.showConfirmDialog(this, form, "Nuevo Turno", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (opt != JOptionPane.OK_OPTION) return;

            Paciente pac = (Paciente) cbPaciente.getSelectedItem();
            Medico med = (Medico) cbMedico.getSelectedItem();
            Consultorio cons = (Consultorio) cbConsultorio.getSelectedItem();
            if (pac == null || med == null || cons == null) { showWarn("Seleccione paciente, médico y consultorio."); return; }

            LocalDate fecha = parseFecha(tfFecha.getText());
            LocalTime hora = parseHora(tfHora.getText());
            if (fecha == null || hora == null) return;

            Turno creado = turnoService.crear(fecha, hora, pac, med, cons);
            showInfo("Turno creado. ID=" + creado.getId() + " | Estado=" + creado.getEstado() + " | $ " + creado.getCostoFinal());
            cargarTabla();

        } catch (RuntimeException ex) {
            showError(ex.getMessage());
        }
    }

    private void cambiarEstadoSeleccionado() {
        int row = tabla.getSelectedRow();
        if (row == -1) { showWarn("Seleccione un turno de la tabla."); return; }
        long id = asLong(model.getValueAt(tabla.convertRowIndexToModel(row), 0));
        EstadoTurno actual = EstadoTurno.valueOf(String.valueOf(model.getValueAt(tabla.convertRowIndexToModel(row), 6)));

        EstadoTurno nuevo = (EstadoTurno) JOptionPane.showInputDialog(
                this, "Nuevo estado:", "Cambiar Estado",
                JOptionPane.QUESTION_MESSAGE, null, EstadoTurno.values(), actual
        );
        if (nuevo == null) return;

        try {
            turnoService.cambiarEstado(id, nuevo);
            showInfo("Estado actualizado a " + nuevo);
            cargarTabla();
        } catch (RuntimeException ex) {
            showError(ex.getMessage());
        }
    }

    private void eliminarSeleccionado() {
        int row = tabla.getSelectedRow();
        if (row == -1) { showWarn("Seleccione un turno de la tabla."); return; }
        long id = asLong(model.getValueAt(tabla.convertRowIndexToModel(row), 0));

        int opt = JOptionPane.showConfirmDialog(this, "¿Eliminar turno ID " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (opt != JOptionPane.YES_OPTION) return;

        try {
            turnoService.eliminar(id);
            showInfo("Turno eliminado.");
            cargarTabla();
        } catch (RuntimeException ex) {
            showError(ex.getMessage());
        }
    }

    private LocalDate parseFecha(String s) {
        try { return LocalDate.parse(s.trim()); }
        catch (Exception e) { showError("Fecha inválida. Formato: YYYY-MM-DD"); return null; }
    }

    private LocalTime parseHora(String s) {
        try { return LocalTime.parse(s.trim()); }
        catch (Exception e) { showError("Hora inválida. Formato: HH:mm"); return null; }
    }

    private long asLong(Object v) {
        return Long.parseLong(String.valueOf(v));
    }

    private void showError(String m){ JOptionPane.showMessageDialog(this, m, "Error", JOptionPane.ERROR_MESSAGE); }
    private void showWarn(String m){ JOptionPane.showMessageDialog(this, m, "Atención", JOptionPane.WARNING_MESSAGE); }
    private void showInfo(String m){ JOptionPane.showMessageDialog(this, m, "Información", JOptionPane.INFORMATION_MESSAGE); }

    private String nombrePaciente(long id) {
        Paciente p = cachePacientes.get(id);
        return p == null ? ("#" + id) : (p.getNombre() + " " + p.getApellido());
    }

    private String nombreMedico(long id) {
        Medico m = cacheMedicos.get(id);
        return m == null ? ("#" + id) : (m.getApellido() + ", " + m.getNombre() + " - " + m.getEspecialidad());
    }

    private String nombreConsultorio(long id) {
        Consultorio c = cacheConsultorios.get(id);
        return c == null ? ("#" + id) : c.getNombre();
    }

    private static <T> Map<Long, T> mapById(List<T> lista) {
        Map<Long, T> map = new HashMap<>();
        for (T t : lista) {
            if (t instanceof Paciente) map.put(((Paciente) t).getId(), t);
            else if (t instanceof Medico) map.put(((Medico) t).getId(), t);
            else if (t instanceof Consultorio) map.put(((Consultorio) t).getId(), t);
        }
        return map;
    }

    private void recargarConsultorios(JComboBox<Consultorio> combo) {
        DefaultComboBoxModel<Consultorio> model = new DefaultComboBoxModel<>();
        for (Consultorio c : consultorioDao.findAll()) model.addElement(c);
        combo.setModel(model);
    }

    private void seleccionarConsultorioPorId(JComboBox<Consultorio> combo, long id) {
        ComboBoxModel<Consultorio> m = combo.getModel();
        for (int i = 0; i < m.getSize(); i++) {
            Consultorio c = m.getElementAt(i);
            if (c.getId() == id) { combo.setSelectedIndex(i); break; }
        }
    }
}
