package TurneroM;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PacienteFrame extends JFrame {

    private final PacienteService service;
    private final DefaultTableModel model;
    private final JTable tabla;

    public PacienteFrame() {
        setTitle("Turnero Médico – Pacientes");
        setSize(800, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Service con H2
        service = new PacienteService(new H2PacienteDao());

        // Modelo de tabla (no editable)
        model = new DefaultTableModel(new Object[]{
                "ID", "Nombre", "Apellido", "DNI", "Teléfono", "Obra Social"
        }, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tabla = new JTable(model);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Panel de botones
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnNuevo = new JButton("Nuevo");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnRefrescar = new JButton("Refrescar");

        barra.add(btnNuevo);
        barra.add(btnEditar);
        barra.add(btnEliminar);
        barra.add(btnRefrescar);

        // Layout
        add(barra, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Acciones
        btnRefrescar.addActionListener(e -> cargarTabla());
        btnNuevo.addActionListener(e -> crearPaciente());
        btnEditar.addActionListener(e -> editarSeleccionado());
        btnEliminar.addActionListener(e -> eliminarSeleccionado());

        // Cargar datos iniciales
        cargarTabla();
    }

    private void cargarTabla() {
        model.setRowCount(0);
        try {
            List<Paciente> lista = service.listar();
            for (Paciente p : lista) {
                model.addRow(new Object[]{
                        p.getId(), p.getNombre(), p.getApellido(), p.getDni(),
                        p.getTelefono(), p.getObraSocial()
                });
            }
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void crearPaciente() {
        try {
            String nombre = input("Nombre:");
            if (nombre == null) return;

            String apellido = input("Apellido:");
            if (apellido == null) return;

            String dni = input("DNI:");
            if (dni == null) return;

            String tel = input("Teléfono:");
            if (tel == null) return;

            String obra = input("Obra Social:");
            if (obra == null) return;

            service.crear(nombre, apellido, dni, tel, obra);
            cargarTabla();
            mostrarInfo("Paciente creado correctamente.");
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void editarSeleccionado() {
        int row = tabla.getSelectedRow();
        if (row == -1) {
            mostrarAtencion("Seleccione un paciente de la tabla.");
            return;
        }

        long id = parseLong(model.getValueAt(row, 0));

        String nombreActual = valueStr(row, 1);
        String apellidoActual = valueStr(row, 2);
        String dniActual = valueStr(row, 3);
        String telActual = valueStr(row, 4);
        String obraActual = valueStr(row, 5);

        try {
            String nombre = inputDefault("Nombre:", nombreActual);
            if (nombre == null) return;

            String apellido = inputDefault("Apellido:", apellidoActual);
            if (apellido == null) return;

            String dni = inputDefault("DNI:", dniActual);
            if (dni == null) return;

            String tel = inputDefault("Teléfono:", telActual);
            if (tel == null) return;

            String obra = inputDefault("Obra Social:", obraActual);
            if (obra == null) return;

            service.actualizar(id, nombre, apellido, dni, tel, obra);
            cargarTabla();
            mostrarInfo("Paciente actualizado.");
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void eliminarSeleccionado() {
        int row = tabla.getSelectedRow();
        if (row == -1) {
            mostrarAtencion("Seleccione un paciente de la tabla.");
            return;
        }
        long id = parseLong(model.getValueAt(row, 0));
        int opt = JOptionPane.showConfirmDialog(
                this, "¿Eliminar paciente ID " + id + "?", "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );
        if (opt == JOptionPane.YES_OPTION) {
            try {
                service.eliminar(id);
                cargarTabla();
                mostrarInfo("Paciente eliminado.");
            } catch (RuntimeException ex) {
                mostrarError(ex.getMessage());
            }
        }
    }

    // Helpers UI
    private String input(String titulo) {
        return JOptionPane.showInputDialog(this, titulo);
    }

    private String inputDefault(String titulo, String valorActual) {
        return (String) JOptionPane.showInputDialog(
                this, titulo, "Editar", JOptionPane.PLAIN_MESSAGE, null, null, valorActual
        );
    }

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarAtencion(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Atención", JOptionPane.WARNING_MESSAGE);
    }

    private void mostrarInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    private long parseLong(Object value) {
        return Long.parseLong(String.valueOf(value));
    }

    private String valueStr(int row, int col) {
        Object v = model.getValueAt(row, col);
        return v == null ? "" : v.toString();
    }
}
