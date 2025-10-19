package TurneroM;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Turnero Médico - Menú Principal");
        setSize(420, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(0, 10));

        JLabel titulo = new JLabel("Seleccione una opción", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(titulo, BorderLayout.NORTH);

        JPanel botones = new JPanel(new GridLayout(0, 1, 10, 10)); // 0 filas => se calcula según los componentes
        botones.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JButton btnPacientes   = new JButton("🧍‍♂️  Gestionar Pacientes");
        JButton btnMedicos     = new JButton("👨‍⚕️  Gestionar Médicos");
        JButton btnTurnos      = new JButton("📅  Gestionar Turnos");
        JButton btnAgenda      = new JButton("🗓️  Agenda de Médico");
        JButton btnRecaudacion = new JButton("📈  Recaudación por Médico");

        btnPacientes.addActionListener(e -> new PacienteFrame().setVisible(true));
        btnMedicos.addActionListener(e -> new MedicoFrame().setVisible(true));
        btnTurnos.addActionListener(e -> new TurnoFrame().setVisible(true));
        btnAgenda.addActionListener(e -> new AgendaMedicoFrame().setVisible(true));
        btnRecaudacion.addActionListener(e -> new ReporteRecaudacionFrame().setVisible(true));

        botones.add(btnPacientes);
        botones.add(btnMedicos);
        botones.add(btnTurnos);
        botones.add(btnAgenda);
        botones.add(btnRecaudacion);

        add(botones, BorderLayout.CENTER);
    }
}
