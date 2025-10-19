package TurneroM;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AgendaMedicoFrame extends JFrame {

    private final MedicoService medicoService = new MedicoService(new H2MedicoDao());
    private final PacienteService pacienteService = new PacienteService(new H2PacienteDao());
    private final ConsultorioDao consultorioDao = new H2ConsultorioDao();
    private final H2TurnoDao turnoDao = new H2TurnoDao();

    private final JComboBox<Medico> cbMedico = new JComboBox<>();
    private final JTextField tfFecha = new JTextField(LocalDate.now().toString());
    private final JButton btnBuscar = new JButton("Buscar");

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Hora", "Paciente", "Consultorio", "Estado", "Costo"}, 0
    ) { public boolean isCellEditable(int r, int c) { return false; } };
    private final JTable tabla = new JTable(model);

    private Map<Long, Paciente> cachePac = new HashMap<>();
    private Map<Long, Consultorio> cacheCon = new HashMap<>();

    public AgendaMedicoFrame() {
        setTitle("Agenda del Médico");
        setSize(700, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel filtros = new JPanel(new GridLayout(1, 5, 8, 8));
        filtros.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        filtros.add(new JLabel("Médico:"));
        filtros.add(cbMedico);
        filtros.add(new JLabel("Fecha (YYYY-MM-DD):"));
        filtros.add(tfFecha);
        filtros.add(btnBuscar);

        add(filtros, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        for (Medico m : medicoService.listar()) cbMedico.addItem(m);
        recargarCaches();

        btnBuscar.addActionListener(e -> buscar());
    }

    private void recargarCaches() {
        cachePac.clear();
        for (Paciente p : pacienteService.listar()) cachePac.put(p.getId(), p);
        cacheCon.clear();
        for (Consultorio c : consultorioDao.findAll()) cacheCon.put(c.getId(), c);
    }

    private void buscar() {
        try {
            Medico m = (Medico) cbMedico.getSelectedItem();
            if (m == null) { JOptionPane.showMessageDialog(this, "Seleccione un médico."); return; }
            LocalDate fecha = LocalDate.parse(tfFecha.getText().trim());
            var turnos = turnoDao.findByMedicoBetween(m.getId(), fecha, fecha);
            model.setRowCount(0);
            for (Turno t : turnos) {
                Paciente p = cachePac.get(t.getPaciente().getId());
                Consultorio c = cacheCon.get(t.getConsultorio().getId());
                model.addRow(new Object[]{
                        t.getHora(),
                        p != null ? (p.getApellido()+", "+p.getNombre()) : ("#"+t.getPaciente().getId()),
                        c != null ? c.getNombre() : ("#"+t.getConsultorio().getId()),
                        t.getEstado(),
                        String.format("%.2f", t.getCostoFinal())
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Revisá la fecha. " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
