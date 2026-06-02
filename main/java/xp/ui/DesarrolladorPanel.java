package main.java.xp.ui;

import main.java.xp.persistence.DesarrolladorDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class DesarrolladorPanel extends JPanel {

    private final DesarrolladorDAO desarrolladorDAO;
    private final JTextField txtNombre;
    private final JLabel lblError;
    private final JTable tabla;
    private final DefaultTableModel tableModel;

    public DesarrolladorPanel() {
        this.desarrolladorDAO = new DesarrolladorDAO();

        setLayout(new BorderLayout(10, 10));
        setBackground(MainFrame.BG_PANEL);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(MainFrame.BG_PANEL);
        formPanel.setBorder(createStyledBorder("Registrar Nuevo Desarrollador"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblNombre = new JLabel("Nombre del Desarrollador:");
        lblNombre.setFont(MainFrame.FONT_REGULAR);
        lblNombre.setForeground(MainFrame.TEXT_PRIMARY);
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(lblNombre, gbc);

        txtNombre = new JTextField(25);
        styleTextField(txtNombre);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        formPanel.add(txtNombre, gbc);

        // Botón Registrar
        JButton btnRegistrar = createStyledButton("Registrar", MainFrame.ACCENT);
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(btnRegistrar, gbc);

        // Label de error
        lblError = new JLabel(" ");
        lblError.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblError.setForeground(MainFrame.RED);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3;
        formPanel.add(lblError, gbc);

        add(formPanel, BorderLayout.NORTH);

        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));
        tablePanel.setBackground(MainFrame.BG_PANEL);
        tablePanel.setBorder(createStyledBorder("Desarrolladores Registrados"));

        // Modelo de tabla no editable
        String[] columnas = {"ID", "Nombre", "Fecha de Registro"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(tableModel);
        styleTable(tabla);

        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBackground(MainFrame.BG_PANEL);
        scrollPane.getViewport().setBackground(MainFrame.BG_PANEL);
        scrollPane.setBorder(BorderFactory.createLineBorder(MainFrame.BG_SURFACE));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Botón Refrescar
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(MainFrame.BG_PANEL);
        JButton btnRefrescar = createStyledButton("Refrescar", MainFrame.ACCENT);
        btnPanel.add(btnRefrescar);
        tablePanel.add(btnPanel, BorderLayout.SOUTH);

        add(tablePanel, BorderLayout.CENTER);

        btnRegistrar.addActionListener(e -> registrarDesarrollador());
        btnRefrescar.addActionListener(e -> cargarDesarrolladores());

        // Enter en el campo de texto también registra
        txtNombre.addActionListener(e -> registrarDesarrollador());

        // Carga inicial
        cargarDesarrolladores();
    }

    private void registrarDesarrollador() {
        String nombre = txtNombre.getText().trim();
        lblError.setText(" ");

        if (nombre.isEmpty()) {
            lblError.setText("El nombre no puede estar vacío.");
            return;
        }

        if (nombre.length() < 2) {
            lblError.setText("El nombre debe tener al menos 2 caracteres.");
            return;
        }

        if (!nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            lblError.setText("El nombre solo puede contener letras y espacios.");
            return;
        }

        try {
            if (desarrolladorDAO.existeNombre(nombre)) {
                lblError.setText("Ya existe un desarrollador con ese nombre.");
                return;
            }

            desarrolladorDAO.registrar(nombre);
            txtNombre.setText("");
            lblError.setText(" ");
            cargarDesarrolladores();

            JOptionPane.showMessageDialog(this,
                    "Desarrollador '" + nombre + "' registrado exitosamente.",
                    "Registro Exitoso",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            lblError.setText("Error al registrar: " + ex.getMessage());
        }
    }

    private void cargarDesarrolladores() {
        tableModel.setRowCount(0);
        try {
            List<String[]> desarrolladores = desarrolladorDAO.listarTodos();
            if (desarrolladores != null) {
                for (String[] dev : desarrolladores) {
                    tableModel.addRow(dev);
                }
            }
        } catch (Exception ex) {
            lblError.setText("Error al cargar desarrolladores: " + ex.getMessage());
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

    private void styleTextField(JTextField field) {
        field.setBackground(MainFrame.BG_SURFACE);
        field.setForeground(MainFrame.TEXT_PRIMARY);
        field.setCaretColor(MainFrame.TEXT_PRIMARY);
        field.setFont(MainFrame.FONT_REGULAR);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MainFrame.BORDER, 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
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
        table.setRowHeight(28);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));

        // Header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(MainFrame.BG_SURFACE);
        header.setForeground(MainFrame.ACCENT);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, MainFrame.ACCENT));
        header.setReorderingAllowed(false);

        // Renderer de filas alternadas
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? MainFrame.BG_PANEL : MainFrame.BG_SURFACE);
                    c.setForeground(MainFrame.TEXT_PRIMARY);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return c;
            }
        });
    }
}
