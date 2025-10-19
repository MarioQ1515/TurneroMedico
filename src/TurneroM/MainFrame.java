package TurneroM;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Turnero Médico - Menú Principal");
        setSize(400, 260);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Contenido
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10)); // ← 4 filas
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("Seleccione una opción", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));

        JButton btnPacientes = new JButton("🧍‍♂️  Gestionar Pacientes");
        JButton btnMedicos   = new JButton("👨‍⚕️  Gestionar Médicos");
        JButton btnTurnos    = new JButton("📅  Gestionar Turnos"); // ← nuevo

        btnPacientes.addActionListener(e -> new PacienteFrame().setVisible(true));
        btnMedicos.addActionListener(e -> new MedicoFrame().setVisible(true));
        btnTurnos.addActionListener(e -> new TurnoFrame().setVisible(true)); // ← abre TurnoFrame

        panel.add(titulo);
        panel.add(btnPacientes);
        panel.add(btnMedicos);
        panel.add(btnTurnos);

        setContentPane(panel);
    }
}
