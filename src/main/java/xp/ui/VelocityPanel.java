package main.java.xp.ui;

import main.java.xp.persistence.DesarrolladorDAO;
import main.java.xp.persistence.SprintDAO;
import main.java.xp.persistence.TareaDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class VelocityPanel extends JPanel {

    private final SprintDAO sprintDAO;
    private final TareaDAO tareaDAO;
    private final DesarrolladorDAO desarrolladorDAO;

    private final JTextField txtSprintNombre;
    private final JTextField txtSprintFecha;
    private final JComboBox<String> cboSprints;
    private final JLabel lblSprintError;

    private final JPanel taskPanel;
    private final JTextField txtTareaNombre;
    private final JTextField txtHorasEstimadas;
    private final JComboBox<String> cboDev;
    private final JLabel lblTareaError;
    private final JTable tablaTareas;
    private final DefaultTableModel tareaTableModel;
    private final JTextField txtRegistrarTiempo;

    private final JLabel lblTareasCompletadas;
    private final JLabel lblHorasReales;
    private final JLabel lblHorasEstimadas;
    private final JLabel lblVelocity;

    private int sprintSeleccionadoId = -1;

    public VelocityPanel() {
        this.sprintDAO = new SprintDAO();
        this.tareaDAO = new TareaDAO();
        this.desarrolladorDAO = new DesarrolladorDAO();

        setLayout(new BorderLayout(10, 10));
        setBackground(MainFrame.BG_PANEL);
        setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(MainFrame.BG_PANEL);
        leftPanel.setBorder(createStyledBorder("Sprints"));
        leftPanel.setPreferredSize(new Dimension(280, 0));

        JPanel sprintForm = new JPanel(new GridBagLayout());
        sprintForm.setBackground(MainFrame.BG_PANEL);
        GridBagConstraints sgbc = new GridBagConstraints();
        sgbc.insets = new Insets(5, 6, 5, 6);
        sgbc.fill = GridBagConstraints.HORIZONTAL;
        sgbc.weightx = 1.0;

        sgbc.gridx = 0; sgbc.gridy = 0;
        sprintForm.add(createLabel("Nombre del Sprint:"), sgbc);
        txtSprintNombre = createStyledTextField(15);
        sgbc.gridy = 1;
        sprintForm.add(txtSprintNombre, sgbc);

        sgbc.gridy = 2;
        sprintForm.add(createLabel("Fecha Inicio (YYYY-MM-DD):"), sgbc);
        txtSprintFecha = createStyledTextField(15);
        sgbc.gridy = 3;
        sprintForm.add(txtSprintFecha, sgbc);

        JButton btnCrearSprint = createStyledButton("Crear Sprint", MainFrame.GREEN);
        sgbc.gridy = 4;
        sgbc.insets = new Insets(10, 6, 5, 6);
        sprintForm.add(btnCrearSprint, sgbc);

        lblSprintError = new JLabel(" ");
        lblSprintError.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblSprintError.setForeground(MainFrame.RED);
        sgbc.gridy = 5;
        sgbc.insets = new Insets(2, 6, 5, 6);
        sprintForm.add(lblSprintError, sgbc);

        sgbc.gridy = 6;
        sgbc.insets = new Insets(10, 6, 5, 6);
        sprintForm.add(createLabel("Sprint Activo:"), sgbc);

        cboSprints = new JComboBox<>();
        styleComboBox(cboSprints);
        sgbc.gridy = 7;
        sgbc.insets = new Insets(2, 6, 5, 6);
        sprintForm.add(cboSprints, sgbc);

        leftPanel.add(sprintForm);

        leftPanel.add(Box.createVerticalGlue());

        // ── Velocity Summary (en la parte inferior del panel izquierdo) ──
        JPanel summaryPanel = new JPanel(new GridBagLayout());
        summaryPanel.setBackground(MainFrame.BG_PANEL);
        summaryPanel.setBorder(createStyledBorder("Velocity Summary"));

        GridBagConstraints vgbc = new GridBagConstraints();
        vgbc.insets = new Insets(6, 10, 6, 10);
        vgbc.fill = GridBagConstraints.HORIZONTAL;
        vgbc.weightx = 1.0;
        vgbc.gridx = 0;

        lblTareasCompletadas = createSummaryLabel("Tareas Completadas: 0");
        vgbc.gridy = 0;
        summaryPanel.add(lblTareasCompletadas, vgbc);

        lblHorasEstimadas = createSummaryLabel("Horas Estimadas: 0.0");
        vgbc.gridy = 1;
        summaryPanel.add(lblHorasEstimadas, vgbc);

        lblHorasReales = createSummaryLabel("Horas Reales: 0.0");
        vgbc.gridy = 2;
        summaryPanel.add(lblHorasReales, vgbc);

        lblVelocity = new JLabel("Velocity: ---");
        lblVelocity.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblVelocity.setForeground(MainFrame.GREEN);
        vgbc.gridy = 3;
        vgbc.insets = new Insets(12, 10, 6, 10);
        summaryPanel.add(lblVelocity, vgbc);

        JButton btnCalcular = createStyledButton("Calcular Velocity", MainFrame.ACCENT);
        vgbc.gridy = 4;
        vgbc.insets = new Insets(8, 10, 10, 10);
        summaryPanel.add(btnCalcular, vgbc);

        leftPanel.add(summaryPanel);

        taskPanel = new JPanel(new BorderLayout(8, 8));
        taskPanel.setBackground(MainFrame.BG_PANEL);
        taskPanel.setBorder(createStyledBorder("Tareas del Sprint"));

        JPanel taskForm = new JPanel(new GridBagLayout());
        taskForm.setBackground(MainFrame.BG_PANEL);
        GridBagConstraints tgbc = new GridBagConstraints();
        tgbc.insets = new Insets(5, 6, 5, 6);
        tgbc.fill = GridBagConstraints.HORIZONTAL;

        tgbc.gridx = 0; tgbc.gridy = 0; tgbc.weightx = 0;
        taskForm.add(createLabel("Tarea:"), tgbc);
        txtTareaNombre = createStyledTextField(15);
        tgbc.gridx = 1; tgbc.weightx = 0.3;
        taskForm.add(txtTareaNombre, tgbc);

        tgbc.gridx = 2; tgbc.weightx = 0;
        taskForm.add(createLabel("Hrs Est:"), tgbc);
        txtHorasEstimadas = createStyledTextField(6);
        tgbc.gridx = 3; tgbc.weightx = 0.1;
        taskForm.add(txtHorasEstimadas, tgbc);

        tgbc.gridx = 4; tgbc.weightx = 0;
        taskForm.add(createLabel("Dev:"), tgbc);
        cboDev = new JComboBox<>();
        styleComboBox(cboDev);
        tgbc.gridx = 5; tgbc.weightx = 0.2;
        taskForm.add(cboDev, tgbc);

        JButton btnAgregarTarea = createStyledButton("Agregar Tarea", MainFrame.GREEN);
        tgbc.gridx = 6; tgbc.weightx = 0;
        taskForm.add(btnAgregarTarea, tgbc);

        lblTareaError = new JLabel(" ");
        lblTareaError.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblTareaError.setForeground(MainFrame.RED);
        tgbc.gridx = 0; tgbc.gridy = 1; tgbc.gridwidth = 7;
        taskForm.add(lblTareaError, tgbc);

        taskPanel.add(taskForm, BorderLayout.NORTH);

        // Tabla de tareas
        String[] colTareas = {"ID", "Nombre", "Estimadas", "Invertido", "Diferencia", "Estado", "Dev", "Completada"};
        tareaTableModel = new DefaultTableModel(colTareas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaTareas = new JTable(tareaTableModel);
        styleTable(tablaTareas);

        JScrollPane tareaScroll = new JScrollPane(tablaTareas);
        tareaScroll.setBackground(MainFrame.BG_PANEL);
        tareaScroll.getViewport().setBackground(MainFrame.BG_PANEL);
        tareaScroll.setBorder(BorderFactory.createLineBorder(MainFrame.BG_SURFACE));
        taskPanel.add(tareaScroll, BorderLayout.CENTER);

        // Panel de control de tarea seleccionada
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        controlPanel.setBackground(MainFrame.BG_SURFACE);
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, MainFrame.ACCENT),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));

        JButton btnIniciar = createStyledButton("Iniciar", MainFrame.GREEN);
        JButton btnPausar = createStyledButton("Pausar", MainFrame.YELLOW);
        JButton btnDetener = createStyledButton("Detener", MainFrame.RED);

        controlPanel.add(btnIniciar);
        controlPanel.add(btnPausar);
        controlPanel.add(btnDetener);

        controlPanel.add(Box.createHorizontalStrut(20));

        txtRegistrarTiempo = createStyledTextField(6);
        controlPanel.add(txtRegistrarTiempo);
        JButton btnRegistrarTiempo = createStyledButton("Registrar Tiempo (hrs)", MainFrame.ACCENT);
        controlPanel.add(btnRegistrarTiempo);

        controlPanel.add(Box.createHorizontalStrut(20));

        JButton btnCerrarTarea = createStyledButton("Cerrar Tarea", MainFrame.TEAL);
        controlPanel.add(btnCerrarTarea);

        taskPanel.add(controlPanel, BorderLayout.SOUTH);

        add(leftPanel, BorderLayout.WEST);
        add(taskPanel, BorderLayout.CENTER);

        taskPanel.setVisible(false);

        btnCrearSprint.addActionListener(e -> crearSprint());
        cboSprints.addActionListener(e -> seleccionarSprint());
        btnAgregarTarea.addActionListener(e -> agregarTarea());
        btnIniciar.addActionListener(e -> cambiarEstadoCronometro("INICIADO"));
        btnPausar.addActionListener(e -> cambiarEstadoCronometro("PAUSADO"));
        btnDetener.addActionListener(e -> cambiarEstadoCronometro("DETENIDO"));
        btnRegistrarTiempo.addActionListener(e -> registrarTiempo());
        btnCerrarTarea.addActionListener(e -> cerrarTarea());
        btnCalcular.addActionListener(e -> calcularVelocity());

        cargarSprints();
        cargarDesarrolladores();
    }

    public void refrescarDatos() {
        cargarSprints();
        cargarDesarrolladores();
        if (sprintSeleccionadoId > 0) {
            cargarTareas();
        }
    }

    private void crearSprint() {
        lblSprintError.setText(" ");

        String nombre = txtSprintNombre.getText().trim();
        String fecha = txtSprintFecha.getText().trim();

        if (nombre.length() < 3) {
            lblSprintError.setText("El nombre del sprint debe tener al menos 3 caracteres.");
            return;
        }

        if (fecha.isEmpty() || !fecha.matches("\\d{4}-\\d{2}-\\d{2}")) {
            lblSprintError.setText("La fecha debe tener el formato YYYY-MM-DD.");
            return;
        }

        try {
            int id = sprintDAO.crearSprint(nombre, fecha);
            txtSprintNombre.setText("");
            txtSprintFecha.setText("");
            lblSprintError.setText(" ");
            cargarSprints();

            JOptionPane.showMessageDialog(this,
                    "Sprint '" + nombre + "' creado con ID #" + id + ".",
                    "Sprint Creado",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            lblSprintError.setText("Error: " + ex.getMessage());
        }
    }

    private void cargarSprints() {
        cboSprints.removeAllItems();
        cboSprints.addItem("-- Seleccionar Sprint --");
        try {
            List<String[]> sprints = sprintDAO.listarSprints();
            if (sprints != null) {
                for (String[] s : sprints) {
                    // {id, nombre, fechaInicio, fechaFin}
                    String label = s[0] + " - " + s[1] + " (" + s[2] + ")";
                    cboSprints.addItem(label);
                }
            }
        } catch (Exception ex) {
            lblSprintError.setText("Error al cargar sprints: " + ex.getMessage());
        }
    }

    private void seleccionarSprint() {
        int idx = cboSprints.getSelectedIndex();
        if (idx <= 0) {
            sprintSeleccionadoId = -1;
            taskPanel.setVisible(false);
            return;
        }

        try {
            String item = (String) cboSprints.getSelectedItem();
            if (item != null) {
                sprintSeleccionadoId = Integer.parseInt(item.split(" - ")[0].trim());
                taskPanel.setVisible(true);
                cargarTareas();
            }
        } catch (Exception ex) {
            lblTareaError.setText("Error al seleccionar sprint: " + ex.getMessage());
        }
    }

    private void cargarDesarrolladores() {
        cboDev.removeAllItems();
        cboDev.addItem("-- Dev --");
        try {
            List<String[]> devs = desarrolladorDAO.listarTodos();
            if (devs != null) {
                for (String[] dev : devs) {
                    cboDev.addItem(dev[1]); // nombre
                }
            }
        } catch (Exception ex) {
            lblTareaError.setText("Error al cargar devs: " + ex.getMessage());
        }
    }

    private void agregarTarea() {
        lblTareaError.setText(" ");

        if (sprintSeleccionadoId <= 0) {
            lblTareaError.setText("Seleccione un sprint primero.");
            return;
        }

        String nombre = txtTareaNombre.getText().trim();
        String horasStr = txtHorasEstimadas.getText().trim();
        int devIdx = cboDev.getSelectedIndex();

        if (nombre.isEmpty()) {
            lblTareaError.setText("El nombre de la tarea no puede estar vacío.");
            return;
        }

        double horas;
        try {
            horas = Double.parseDouble(horasStr);
            if (horas <= 0) {
                lblTareaError.setText("Las horas estimadas deben ser un número positivo.");
                return;
            }
        } catch (NumberFormatException ex) {
            lblTareaError.setText("Las horas estimadas deben ser un número válido.");
            return;
        }

        if (devIdx <= 0) {
            lblTareaError.setText("Seleccione un desarrollador.");
            return;
        }

        String dev = (String) cboDev.getSelectedItem();

        try {
            int id = tareaDAO.crearTarea(sprintSeleccionadoId, nombre, horas, dev);
            txtTareaNombre.setText("");
            txtHorasEstimadas.setText("");
            lblTareaError.setText(" ");
            cargarTareas();

            JOptionPane.showMessageDialog(this,
                    "Tarea '" + nombre + "' creada con ID #" + id + ".",
                    "Tarea Creada",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            lblTareaError.setText("Error: " + ex.getMessage());
        }
    }

    private void cargarTareas() {
        tareaTableModel.setRowCount(0);
        if (sprintSeleccionadoId <= 0) return;

        try {
            List<String[]> tareas = tareaDAO.listarTareasPorSprint(sprintSeleccionadoId);
            if (tareas != null) {
                for (String[] t : tareas) {
                    double estimadas = parseDoubleSafe(t[2]);
                    double invertido = parseDoubleSafe(t[3]);
                    double diferencia = estimadas - invertido;

                    String completadaLabel = "1".equals(t[5]) ? "Sí" : "No";

                    String[] row = {
                        t[0],                                          // ID
                        t[1],                                          // Nombre
                        String.format("%.1f", estimadas),              // Estimadas
                        String.format("%.1f", invertido),              // Invertido
                        String.format("%.1f", diferencia),             // Diferencia
                        t[4],                                          // Estado cronómetro
                        t[6],                                          // Dev
                        completadaLabel                                // Completada
                    };
                    tareaTableModel.addRow(row);
                }
            }
        } catch (Exception ex) {
            lblTareaError.setText("Error al cargar tareas: " + ex.getMessage());
        }
    }

    private int getSelectedTaskId() {
        int row = tablaTareas.getSelectedRow();
        if (row < 0) {
            lblTareaError.setText("Seleccione una tarea de la tabla.");
            return -1;
        }
        try {
            return Integer.parseInt(tareaTableModel.getValueAt(row, 0).toString());
        } catch (Exception ex) {
            lblTareaError.setText("Error al obtener ID de tarea.");
            return -1;
        }
    }

    private void cambiarEstadoCronometro(String estado) {
        lblTareaError.setText(" ");
        int tareaId = getSelectedTaskId();
        if (tareaId < 0) return;

        try {
            tareaDAO.actualizarEstadoCronometro(tareaId, estado);
            cargarTareas();
        } catch (Exception ex) {
            lblTareaError.setText("Error: " + ex.getMessage());
        }
    }

    private void registrarTiempo() {
        lblTareaError.setText(" ");
        int tareaId = getSelectedTaskId();
        if (tareaId < 0) return;

        String horasStr = txtRegistrarTiempo.getText().trim();
        double horas;
        try {
            horas = Double.parseDouble(horasStr);
            if (horas <= 0) {
                lblTareaError.setText("Las horas deben ser un número positivo.");
                return;
            }
        } catch (NumberFormatException ex) {
            lblTareaError.setText("Ingrese un número válido de horas.");
            return;
        }

        try {
            tareaDAO.registrarTiempo(tareaId, horas);
            txtRegistrarTiempo.setText("");
            cargarTareas();
        } catch (Exception ex) {
            lblTareaError.setText("Error: " + ex.getMessage());
        }
    }

    private void cerrarTarea() {
        lblTareaError.setText(" ");
        int tareaId = getSelectedTaskId();
        if (tareaId < 0) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Cerrar la tarea #" + tareaId + "?",
                "Confirmar Cierre",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            tareaDAO.cerrarTarea(tareaId);
            cargarTareas();

            JOptionPane.showMessageDialog(this,
                    "Tarea #" + tareaId + " cerrada exitosamente.",
                    "Tarea Cerrada",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            lblTareaError.setText("Error: " + ex.getMessage());
        }
    }

    private void calcularVelocity() {
        int totalCompletadas = 0;
        double totalEstimadas = 0.0;
        double totalReales = 0.0;

        for (int i = 0; i < tareaTableModel.getRowCount(); i++) {
            String completada = tareaTableModel.getValueAt(i, 7).toString();
            if ("Sí".equals(completada)) {
                totalCompletadas++;
                totalEstimadas += parseDoubleSafe(tareaTableModel.getValueAt(i, 2).toString());
                totalReales += parseDoubleSafe(tareaTableModel.getValueAt(i, 3).toString());
            }
        }

        lblTareasCompletadas.setText("Tareas Completadas: " + totalCompletadas);
        lblHorasEstimadas.setText("Horas Estimadas: " + String.format("%.1f", totalEstimadas));
        lblHorasReales.setText("Horas Reales: " + String.format("%.1f", totalReales));

        if (totalReales > 0) {
            double velocity = totalEstimadas / totalReales;
            lblVelocity.setText("Velocity: " + String.format("%.2f", velocity));
            lblVelocity.setForeground(velocity >= 1.0 ? MainFrame.GREEN : MainFrame.RED);
        } else {
            lblVelocity.setText("Velocity: ---");
            lblVelocity.setForeground(MainFrame.YELLOW);
        }
    }

    private double parseDoubleSafe(String val) {
        try {
            return Double.parseDouble(val);
        } catch (Exception e) {
            return 0.0;
        }
    }

    private TitledBorder createStyledBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(MainFrame.BG_SURFACE, 1),
                "  " + title + "  ");
        border.setTitleFont(MainFrame.FONT_HEADER);
        border.setTitleColor(MainFrame.ACCENT);
        border.setTitleJustification(TitledBorder.LEFT);
        return border;
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(MainFrame.FONT_REGULAR);
        lbl.setForeground(MainFrame.TEXT_PRIMARY);
        return lbl;
    }

    private JLabel createSummaryLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setForeground(MainFrame.TEXT_PRIMARY);
        return lbl;
    }

    private JTextField createStyledTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setBackground(MainFrame.BG_SURFACE);
        field.setForeground(MainFrame.TEXT_PRIMARY);
        field.setCaretColor(MainFrame.TEXT_PRIMARY);
        field.setFont(MainFrame.FONT_REGULAR);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MainFrame.BORDER, 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        return field;
    }

    private void styleComboBox(JComboBox<String> combo) {
        combo.setUI(new javax.swing.plaf.basic.BasicComboBoxUI());
        combo.setBackground(MainFrame.BG_SURFACE);
        combo.setForeground(MainFrame.TEXT_PRIMARY);
        combo.setFont(MainFrame.FONT_REGULAR);
        combo.setBorder(BorderFactory.createLineBorder(MainFrame.BORDER, 1));
    }

    private JButton createStyledButton(String text, Color bgColor) {
        return MainFrame.createModernButton(text, bgColor);
    }

    private void styleTable(JTable table) {
        table.setFont(MainFrame.FONT_TABLE);
        table.setBackground(MainFrame.BG_PANEL);
        table.setForeground(MainFrame.TEXT_PRIMARY);
        table.setSelectionBackground(MainFrame.ACCENT);
        table.setSelectionForeground(MainFrame.BG_DARK);
        table.setGridColor(MainFrame.BG_SURFACE);
        table.setRowHeight(26);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        header.setBackground(MainFrame.BG_SURFACE);
        header.setForeground(MainFrame.ACCENT);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, MainFrame.ACCENT));
        header.setReorderingAllowed(false);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? MainFrame.BG_PANEL : MainFrame.BG_SURFACE);
                    c.setForeground(MainFrame.TEXT_PRIMARY);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
                return c;
            }
        });
    }
}
