package TurneroM;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class ReporteRecaudacionFrame extends JFrame {

    private final MedicoService medicoService = new MedicoService(new H2MedicoDao());
    private final TurnoService turnoService   = new TurnoService(new H2TurnoDao());

    private final JComboBox<Medico> cbMedico = new JComboBox<>();
    private final JTextField tfDesde = new JTextField(LocalDate.now().minusDays(7).toString());
    private final JTextField tfHasta = new JTextField(LocalDate.now().toString());
    private final JLabel lblCantidad = new JLabel("-");
    private final JLabel lblTotal = new JLabel("-");

    public ReporteRecaudacionFrame() {
        setTitle("Reporte: Recaudación por Médico");
        setSize(480, 240);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // top: filtros
        JPanel filtros = new JPanel(new GridLayout(0,2,8,8));
        filtros.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        filtros.add(new JLabel("Médico:")); filtros.add(cbMedico);
        filtros.add(new JLabel("Desde (YYYY-MM-DD):")); filtros.add(tfDesde);
        filtros.add(new JLabel("Hasta (YYYY-MM-DD):")); filtros.add(tfHasta);

        // middle: resultados
        JPanel resultados = new JPanel(new GridLayout(1,4,10,10));
        resultados.setBorder(BorderFactory.createEmptyBorder(0,12,12,12));
        resultados.add(new JLabel("Cantidad:"));
        resultados.add(lblCantidad);
        resultados.add(new JLabel("Total $:"));
        resultados.add(lblTotal);

        // bottom: acciones
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCalcular = new JButton("Calcular");
        JButton btnListar   = new JButton("Ver turnos…");
        acciones.add(btnCalcular);
        acciones.add(btnListar);

        add(filtros, BorderLayout.NORTH);
        add(resultados, BorderLayout.CENTER);
        add(acciones, BorderLayout.SOUTH);

        // cargar médicos
        for (Medico m : medicoService.listar()) cbMedico.addItem(m);

        // acciones
        btnCalcular.addActionListener(e -> calcular());
        btnListar.addActionListener(e -> listarTurnosPagables());
    }

    private void calcular() {
        try {
            Medico med = (Medico) cbMedico.getSelectedItem();
            if (med == null) { warn("Seleccione un médico."); return; }
            LocalDate desde = LocalDate.parse(tfDesde.getText().trim());
            LocalDate hasta = LocalDate.parse(tfHasta.getText().trim());

            ReporteRecaudacionMedico r = turnoService.reporteRecaudacionMedico(med.getId(), desde, hasta);
            lblCantidad.setText(String.valueOf(r.getCantidadTurnos()));
            lblTotal.setText(String.format("%.2f", r.getTotalCobrado()));
        } catch (Exception ex) {
            error("Revisá médico y fechas. " + ex.getMessage());
        }
    }

    // opcional: mostrar los turnos pagables (CONFIRMADO/ATENDIDO)
    private void listarTurnosPagables() {
        try {
            Medico med = (Medico) cbMedico.getSelectedItem();
            if (med == null) { warn("Seleccione un médico."); return; }
            LocalDate desde = LocalDate.parse(tfDesde.getText().trim());
            LocalDate hasta = LocalDate.parse(tfHasta.getText().trim());
            List<Turno> todos = new H2TurnoDao().findByMedicoBetween(med.getId(), desde, hasta);

            StringBuilder sb = new StringBuilder("Turnos pagables (CONFIRMADO/ATENDIDO):\n\n");
            for (Turno t : todos) {
                if (t.getEstado() == EstadoTurno.CONFIRMADO || t.getEstado() == EstadoTurno.ATENDIDO) {
                    sb.append(String.format("#%d  %s %s  $%.2f  (%s)\n",
                            t.getId(), t.getFecha(), t.getHora(), t.getCostoFinal(), t.getEstado()));
                }
            }
            JTextArea area = new JTextArea(sb.toString(), 18, 50);
            area.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(area), "Detalle de turnos", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            error("Revisá médico y fechas. " + ex.getMessage());
        }
    }

    private void warn(String m){ JOptionPane.showMessageDialog(this, m, "Atención", JOptionPane.WARNING_MESSAGE); }
    private void error(String m){ JOptionPane.showMessageDialog(this, m, "Error", JOptionPane.ERROR_MESSAGE); }
}
