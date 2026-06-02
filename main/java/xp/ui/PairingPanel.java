package main.java.xp.ui;

import main.java.xp.persistence.DesarrolladorDAO;
import main.java.xp.persistence.SesionPairingDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class PairingPanel extends JPanel {

    private final DesarrolladorDAO desarrolladorDAO;
    private final SesionPairingDAO sesionPairingDAO;

    private final JTextField txtFecha;
    private final JComboBox<String> cboPiloto;
    private final JComboBox<String> cboCopiloto;
    private final JLabel lblFormError;

    private final JPanel sessionPanel;
    private final JLabel lblRoles;
    private final JTextField txtComponente;
    private final JTextArea txtCodigo;
    private final JLabel lblSessionError;

    private final DefaultTableModel tableModel;

    private int sesionActualId = -1;
    private String pilotoActual = "";
    private String copilotoActual = "";

    public PairingPanel() {
        this.desarrolladorDAO = new DesarrolladorDAO();
        this.sesionPairingDAO = new SesionPairingDAO();

        setLayout(new BorderLayout(10, 10));
        setBackground(MainFrame.BG_PANEL);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(MainFrame.BG_PANEL);
        formPanel.setBorder(createStyledBorder("Nueva Sesión de Pair Programming"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(createLabel("Fecha (YYYY-MM-DD):"), gbc);
        txtFecha = createStyledTextField(12);
        gbc.gridx = 1; gbc.weightx = 0.3;
        formPanel.add(txtFecha, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(createLabel("Piloto:"), gbc);
        cboPiloto = new JComboBox<>();
        styleComboBox(cboPiloto);
        gbc.gridx = 3; gbc.weightx = 0.3;
        formPanel.add(cboPiloto, gbc);

        gbc.gridx = 4; gbc.weightx = 0;
        formPanel.add(createLabel("Copiloto:"), gbc);
        cboCopiloto = new JComboBox<>();
        styleComboBox(cboCopiloto);
        gbc.gridx = 5; gbc.weightx = 0.3;
        formPanel.add(cboCopiloto, gbc);

        JButton btnIniciar = createStyledButton("Iniciar Sesión", MainFrame.GREEN);
        gbc.gridx = 6; gbc.weightx = 0;
        formPanel.add(btnIniciar, gbc);

        lblFormError = new JLabel(" ");
        lblFormError.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblFormError.setForeground(MainFrame.RED);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 7;
        formPanel.add(lblFormError, gbc);

        sessionPanel = new JPanel(new GridBagLayout());
        sessionPanel.setBackground(MainFrame.BG_PANEL);
        sessionPanel.setBorder(createStyledBorder("Sesión Activa"));
        sessionPanel.setVisible(false);

        GridBagConstraints sgbc = new GridBagConstraints();
        sgbc.insets = new Insets(5, 8, 5, 8);
        sgbc.fill = GridBagConstraints.HORIZONTAL;

        lblRoles = new JLabel("Piloto: --- | Copiloto: ---");
        lblRoles.setFont(MainFrame.FONT_HEADER);
        lblRoles.setForeground(MainFrame.ACCENT_LIGHT);
        sgbc.gridx = 0; sgbc.gridy = 0; sgbc.gridwidth = 4; sgbc.weightx = 1.0;
        sessionPanel.add(lblRoles, sgbc);

        sgbc.gridwidth = 1; sgbc.weightx = 0;
        sgbc.gridx = 0; sgbc.gridy = 1;
        sessionPanel.add(createLabel("Componente:"), sgbc);
        txtComponente = createStyledTextField(20);
        sgbc.gridx = 1; sgbc.weightx = 0.5;
        sessionPanel.add(txtComponente, sgbc);

        sgbc.gridx = 0; sgbc.gridy = 2; sgbc.weightx = 0;
        sessionPanel.add(createLabel("Código / Aporte:"), sgbc);

        txtCodigo = new JTextArea(5, 30);
        txtCodigo.setBackground(MainFrame.BG_SURFACE);
        txtCodigo.setForeground(MainFrame.TEXT_PRIMARY);
        txtCodigo.setCaretColor(MainFrame.TEXT_PRIMARY);
        txtCodigo.setFont(new Font("Consolas", Font.PLAIN, 13));
        txtCodigo.setLineWrap(true);
        txtCodigo.setWrapStyleWord(true);
        txtCodigo.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

        JScrollPane codeScroll = new JScrollPane(txtCodigo);
        codeScroll.setBorder(BorderFactory.createLineBorder(MainFrame.BORDER, 1));
        codeScroll.getViewport().setBackground(MainFrame.BG_SURFACE);
        sgbc.gridx = 1; sgbc.gridy = 2; sgbc.gridwidth = 3; sgbc.weightx = 1.0;
        sgbc.fill = GridBagConstraints.BOTH; sgbc.weighty = 1.0;
        sessionPanel.add(codeScroll, sgbc);

        sgbc.fill = GridBagConstraints.HORIZONTAL; sgbc.weighty = 0;
        sgbc.gridy = 3; sgbc.gridwidth = 1;

        JButton btnRegistrarAporte = createStyledButton("Registrar Aporte", MainFrame.ACCENT);
        sgbc.gridx = 0;
        sessionPanel.add(btnRegistrarAporte, sgbc);

        JButton btnIntercambiar = createStyledButton("Intercambiar Roles", MainFrame.YELLOW);
        sgbc.gridx = 1;
        sessionPanel.add(btnIntercambiar, sgbc);

        JButton btnFinalizar = createStyledButton("Finalizar Sesión", MainFrame.RED);
        sgbc.gridx = 2;
        sessionPanel.add(btnFinalizar, sgbc);

        lblSessionError = new JLabel(" ");
        lblSessionError.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblSessionError.setForeground(MainFrame.RED);
        sgbc.gridx = 0; sgbc.gridy = 4; sgbc.gridwidth = 4;
        sessionPanel.add(lblSessionError, sgbc);

        String[] columnas = {"ID", "Fecha", "Piloto", "Copiloto", "Estado"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tabla = new JTable(tableModel);
        styleTable(tabla);

        JScrollPane tableScroll = new JScrollPane(tabla);
        tableScroll.setBackground(MainFrame.BG_PANEL);
        tableScroll.getViewport().setBackground(MainFrame.BG_PANEL);
        tableScroll.setBorder(BorderFactory.createLineBorder(MainFrame.BG_SURFACE));
        tableScroll.setPreferredSize(new Dimension(0, 180));

        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setBackground(MainFrame.BG_PANEL);
        tableWrapper.setBorder(createStyledBorder("Historial de Sesiones"));
        tableWrapper.add(tableScroll, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout(0, 5));
        topPanel.setBackground(MainFrame.BG_PANEL);
        topPanel.add(formPanel, BorderLayout.NORTH);
        topPanel.add(sessionPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(tableWrapper, BorderLayout.CENTER);

        btnIniciar.addActionListener(e -> iniciarSesion());
        btnRegistrarAporte.addActionListener(e -> registrarAporte());
        btnIntercambiar.addActionListener(e -> intercambiarRoles());
        btnFinalizar.addActionListener(e -> finalizarSesion());

        refrescarDesarrolladores();
        cargarHistorial();
    }

    public void refrescarDesarrolladores() {
        cboPiloto.removeAllItems();
        cboCopiloto.removeAllItems();
        cboPiloto.addItem("-- Seleccionar --");
        cboCopiloto.addItem("-- Seleccionar --");
        try {
            List<String[]> devs = desarrolladorDAO.listarTodos();
            if (devs != null) {
                for (String[] dev : devs) {
                    String item = dev[0] + " - " + dev[1]; // "id - nombre"
                    cboPiloto.addItem(item);
                    cboCopiloto.addItem(item);
                }
            }
        } catch (Exception ex) {
            lblFormError.setText("Error al cargar desarrolladores: " + ex.getMessage());
        }
    }

    private void iniciarSesion() {
        lblFormError.setText(" ");

        String fecha = txtFecha.getText().trim();
        int pilotoIdx = cboPiloto.getSelectedIndex();
        int copilotoIdx = cboCopiloto.getSelectedIndex();

        if (fecha.isEmpty() || !fecha.matches("\\d{4}-\\d{2}-\\d{2}")) {
            lblFormError.setText("La fecha debe tener el formato YYYY-MM-DD.");
            return;
        }

        if (pilotoIdx <= 0) {
            lblFormError.setText("Debe seleccionar un Piloto.");
            return;
        }
        if (copilotoIdx <= 0) {
            lblFormError.setText("Debe seleccionar un Copiloto.");
            return;
        }

        String pilotoItem = (String) cboPiloto.getSelectedItem();
        String copilotoItem = (String) cboCopiloto.getSelectedItem();

        if (pilotoItem != null && pilotoItem.equals(copilotoItem)) {
            lblFormError.setText("El Piloto y el Copiloto deben ser diferentes.");
            return;
        }

        if (sesionActualId > 0) {
            lblFormError.setText("Ya hay una sesión activa. Finalícela primero.");
            return;
        }

        try {
            int pilotoId = Integer.parseInt(pilotoItem.split(" - ")[0].trim());
            int copilotoId = Integer.parseInt(copilotoItem.split(" - ")[0].trim());
            pilotoActual = pilotoItem.split(" - ")[1].trim();
            copilotoActual = copilotoItem.split(" - ")[1].trim();

            sesionActualId = sesionPairingDAO.crearSesion(pilotoId, copilotoId, fecha);

            lblRoles.setText("Piloto: " + pilotoActual + "  |  Copiloto: " + copilotoActual);
            sessionPanel.setVisible(true);
            lblFormError.setText(" ");

            cargarHistorial();

            JOptionPane.showMessageDialog(this,
                    "Sesión #" + sesionActualId + " iniciada.\nPiloto: " + pilotoActual + "\nCopiloto: " + copilotoActual,
                    "Sesión Iniciada",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            lblFormError.setText("⚠ Error al crear sesión: " + ex.getMessage());
        }
    }

    private void registrarAporte() {
        lblSessionError.setText(" ");

        if (sesionActualId <= 0) {
            lblSessionError.setText("No hay sesión activa.");
            return;
        }

        String componente = txtComponente.getText().trim();
        String codigo = txtCodigo.getText().trim();

        if (componente.isEmpty()) {
            lblSessionError.setText("El nombre del componente no puede estar vacío.");
            return;
        }
        if (codigo.isEmpty()) {
            lblSessionError.setText("El código / aporte no puede estar vacío.");
            return;
        }

        try {
            sesionPairingDAO.registrarAporte(sesionActualId, componente, codigo, pilotoActual);
            txtComponente.setText("");
            txtCodigo.setText("");
            lblSessionError.setText(" ");

            JOptionPane.showMessageDialog(this,
                    "Aporte registrado exitosamente por " + pilotoActual + ".",
                    "Aporte Registrado",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            lblSessionError.setText("Error al registrar aporte: " + ex.getMessage());
        }
    }

    private void intercambiarRoles() {
        lblSessionError.setText(" ");

        if (sesionActualId <= 0) {
            lblSessionError.setText("No hay sesión activa.");
            return;
        }

        try {
            String pilotoAnterior = pilotoActual;
            String copilotoAnterior = copilotoActual;

            pilotoActual = copilotoAnterior;
            copilotoActual = pilotoAnterior;

            sesionPairingDAO.registrarIntercambioRoles(sesionActualId,
                    pilotoAnterior, copilotoAnterior,
                    pilotoActual, copilotoActual);

            lblRoles.setText("Piloto: " + pilotoActual + "  |  Copiloto: " + copilotoActual);

            JOptionPane.showMessageDialog(this,
                    "Roles intercambiados.\nNuevo Piloto: " + pilotoActual + "\nNuevo Copiloto: " + copilotoActual,
                    "Roles Intercambiados",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            lblSessionError.setText("Error al intercambiar roles: " + ex.getMessage());
        }
    }

    private void finalizarSesion() {
        lblSessionError.setText(" ");

        if (sesionActualId <= 0) {
            lblSessionError.setText("No hay sesión activa.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Desea finalizar la sesión #" + sesionActualId + "?",
                "Confirmar Finalización",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            sesionPairingDAO.finalizarSesion(sesionActualId);

            sesionActualId = -1;
            pilotoActual = "";
            copilotoActual = "";
            sessionPanel.setVisible(false);
            txtComponente.setText("");
            txtCodigo.setText("");

            cargarHistorial();

            JOptionPane.showMessageDialog(this,
                    "Sesión finalizada exitosamente.",
                    "Sesión Finalizada",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            lblSessionError.setText("⚠ Error al finalizar sesión: " + ex.getMessage());
        }
    }

    private void cargarHistorial() {
        tableModel.setRowCount(0);
        try {
            List<String[]> sesiones = sesionPairingDAO.listarSesiones();
            if (sesiones != null) {
                for (String[] s : sesiones) {
                    tableModel.addRow(s);
                }
            }
        } catch (Exception ex) {
            lblFormError.setText("Error al cargar historial: " + ex.getMessage());
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
        table.setRowHeight(28);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
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
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return c;
            }
        });
    }
}
