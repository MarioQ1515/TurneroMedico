package TurneroM;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MedicoFrame extends JFrame {

    private final MedicoService service;
    private final DefaultTableModel model;
    private final JTable tabla;

    public MedicoFrame() {
        setTitle("Turnero Médico – Médicos");
        setSize(850, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        service = new MedicoService(new H2MedicoDao());

        model = new DefaultTableModel(new Object[]{
                "ID", "Nombre", "Apellido", "Especialidad", "Costo", "Obra Social Atendida"
        }, 0) { public boolean isCellEditable(int r, int c) { return false; }};

        tabla = new JTable(model);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnNuevo = new JButton("Nuevo");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnRefrescar = new JButton("Refrescar");
        barra.add(btnNuevo); barra.add(btnEditar); barra.add(btnEliminar); barra.add(btnRefrescar);

        add(barra, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        btnRefrescar.addActionListener(e -> cargarTabla());
        btnNuevo.addActionListener(e -> crearMedico());
        btnEditar.addActionListener(e -> editarSeleccionado());
        btnEliminar.addActionListener(e -> eliminarSeleccionado());

        cargarTabla();
    }

    private void cargarTabla() {
        model.setRowCount(0);
        try {
            List<Medico> lista = service.listar();
            for (Medico m : lista) {
                model.addRow(new Object[]{
                        m.getId(), m.getNombre(), m.getApellido(),
                        m.getEspecialidad(), m.getCostoConsulta(), m.getObraSocialAtendida()
                });
            }
        } catch (RuntimeException ex) {
            showError(ex.getMessage());
        }
    }

    private void crearMedico() {
        try {
            String nombre = input("Nombre:"); if (nombre == null) return;
            String apellido = input("Apellido:"); if (apellido == null) return;
            Especialidad esp = inputEspecialidad(); if (esp == null) return;
            Double costo = inputDouble("Costo consulta:"); if (costo == null) return;
            String obra = input("Obra Social atendida (opcional):"); if (obra == null) return;

            service.crear(nombre, apellido, esp, costo, obra);
            cargarTabla(); showInfo("Médico creado.");
        } catch (RuntimeException ex) { showError(ex.getMessage()); }
    }

    private void editarSeleccionado() {
        int row = tabla.getSelectedRow();
        if (row == -1) { showWarn("Seleccione un médico."); return; }

        long id = Long.parseLong(String.valueOf(model.getValueAt(row, 0)));
        String nombreA = String.valueOf(model.getValueAt(row, 1));
        String apellidoA = String.valueOf(model.getValueAt(row, 2));
        Especialidad espA = Especialidad.valueOf(String.valueOf(model.getValueAt(row, 3)));
        double costoA = Double.parseDouble(String.valueOf(model.getValueAt(row, 4)));
        String obraA = String.valueOf(model.getValueAt(row, 5));

        try {
            String nombre = inputDefault("Nombre:", nombreA); if (nombre == null) return;
            String apellido = inputDefault("Apellido:", apellidoA); if (apellido == null) return;
            Especialidad esp = inputEspecialidad(espA); if (esp == null) return;
            Double costo = inputDoubleDefault("Costo consulta:", costoA); if (costo == null) return;
            String obra = inputDefault("Obra Social atendida (opcional):", obraA); if (obra == null) return;

            service.actualizar(id, nombre, apellido, esp, costo, obra);
            cargarTabla(); showInfo("Médico actualizado.");
        } catch (RuntimeException ex) { showError(ex.getMessage()); }
    }

    private void eliminarSeleccionado() {
        int row = tabla.getSelectedRow();
        if (row == -1) { showWarn("Seleccione un médico."); return; }
        long id = Long.parseLong(String.valueOf(model.getValueAt(row, 0)));
        int opt = JOptionPane.showConfirmDialog(this, "¿Eliminar médico ID " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            try { service.eliminar(id); cargarTabla(); showInfo("Médico eliminado."); }
            catch (RuntimeException ex) { showError(ex.getMessage()); }
        }
    }

    // ---------- helpers UI ----------
    private String input(String t) { return JOptionPane.showInputDialog(this, t); }
    private String inputDefault(String t, String v) {
        return (String) JOptionPane.showInputDialog(this, t, "Editar", JOptionPane.PLAIN_MESSAGE, null, null, v);
    }
    private void showError(String m){ JOptionPane.showMessageDialog(this, m, "Error", JOptionPane.ERROR_MESSAGE); }
    private void showWarn(String m){ JOptionPane.showMessageDialog(this, m, "Atención", JOptionPane.WARNING_MESSAGE); }
    private void showInfo(String m){ JOptionPane.showMessageDialog(this, m, "Información", JOptionPane.INFORMATION_MESSAGE); }

    private Especialidad inputEspecialidad() {
        return (Especialidad) JOptionPane.showInputDialog(
                this, "Especialidad:", "Seleccionar",
                JOptionPane.QUESTION_MESSAGE, null, Especialidad.values(), Especialidad.values()[0]
        );
    }
    private Especialidad inputEspecialidad(Especialidad actual) {
        return (Especialidad) JOptionPane.showInputDialog(
                this, "Especialidad:", "Seleccionar",
                JOptionPane.QUESTION_MESSAGE, null, Especialidad.values(), actual
        );
    }
    private Double inputDouble(String titulo) {
        String s = input(titulo); if (s == null) return null;
        try { return Double.parseDouble(s); } catch (NumberFormatException e) { showError("Número inválido"); return null; }
    }
    private Double inputDoubleDefault(String titulo, double actual) {
        String s = inputDefault(titulo, String.valueOf(actual)); if (s == null) return null;
        try { return Double.parseDouble(s); } catch (NumberFormatException e) { showError("Número inválido"); return null; }
    }
}