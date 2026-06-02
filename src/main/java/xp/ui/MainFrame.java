package main.java.xp.ui;

import main.java.xp.persistence.DatabaseConnection;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.RoundRectangle2D;

public class MainFrame extends JFrame {

    public static final Color BG_DARK      = new Color(0x0a0a12);   // near-black con tinte violeta — fondo principal
    public static final Color BG_PANEL     = new Color(0x14121f);   // violeta muy oscuro — contenedores / tarjetas
    public static final Color BG_SURFACE   = new Color(0x1e1a2e);   // violeta oscuro — superficies elevadas
    public static final Color TEXT_PRIMARY  = new Color(0xf0eef6);   // lavanda claro — texto principal
    public static final Color TEXT_SECONDARY = new Color(0x8b85a1);  // gris violáceo — texto secundario
    public static final Color ACCENT       = new Color(0x8b5cf6);   // violet-500  — acento principal (morado medio-oscuro)
    public static final Color ACCENT_LIGHT = new Color(0xa78bfa);   // violet-400  — acento claro (morado más claro)
    public static final Color ACCENT_DARK  = new Color(0x6d28d9);   // violet-700  — acento oscuro
    public static final Color GREEN        = new Color(0x8b5cf6);   // violet-500  — éxito / estado activo
    public static final Color RED          = new Color(0xfb7185);   // rose-400    — error / estado RED
    public static final Color YELLOW       = new Color(0xfbbf24);   // amber-400   — advertencia / pausa
    public static final Color TEAL         = new Color(0xc4b5fd);   // violet-300  — acciones especiales (lila claro)
    public static final Color BORDER       = new Color(0x2e2945);   // violeta grisáceo — bordes sutiles

    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_HEADER  = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_TABLE   = new Font("Segoe UI", Font.PLAIN, 12);

    public MainFrame() {
        super("XP Project Manager - Pair Programming & Velocity");

        applyDarkThemeDefaults();

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));
        getContentPane().setBackground(BG_DARK);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    DatabaseConnection.closeConnection();
                } catch (Exception ex) {
                    System.err.println("Error cerrando conexión: " + ex.getMessage());
                }
                System.exit(0);
            }
        });

        JPanel mainContainer = new JPanel(new BorderLayout(0, 0));
        mainContainer.setBackground(BG_DARK);

        JPanel titleBar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, ACCENT_DARK, getWidth(), 0, new Color(0x4338ca));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        titleBar.setPreferredSize(new Dimension(0, 38));
        titleBar.setLayout(new FlowLayout(FlowLayout.LEFT, 16, 8));

        JLabel titleLabel = new JLabel("XP Project Manager");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLabel.setForeground(Color.WHITE);
        titleBar.add(titleLabel);

        JLabel subtitleLabel = new JLabel("—  Pair Programming & Velocity Tracker");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(0xe0daf8));  // violet-100
        titleBar.add(subtitleLabel);

        mainContainer.add(titleBar, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setUI(new EmeraldTabbedPaneUI());
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.setBackground(BG_DARK);
        tabbedPane.setForeground(TEXT_PRIMARY);
        tabbedPane.setOpaque(true);
        tabbedPane.setTabPlacement(JTabbedPane.TOP);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        DesarrolladorPanel desarrolladorPanel = new DesarrolladorPanel();
        PairingPanel pairingPanel = new PairingPanel();
        VelocityPanel velocityPanel = new VelocityPanel();

        tabbedPane.addTab("  Desarrolladores  ", desarrolladorPanel);
        tabbedPane.addTab("  Sincronización de roles y control compartido  ", pairingPanel);
        tabbedPane.addTab("  Velocity  ", velocityPanel);

        tabbedPane.addChangeListener(e -> {
            int idx = tabbedPane.getSelectedIndex();
            if (idx == 1) {
                pairingPanel.refrescarDesarrolladores();
            } else if (idx == 2) {
                velocityPanel.refrescarDatos();
            }
        });

        mainContainer.add(tabbedPane, BorderLayout.CENTER);
        getContentPane().add(mainContainer, BorderLayout.CENTER);
    }

    private void applyDarkThemeDefaults() {
        ColorUIResource bgDark     = new ColorUIResource(BG_DARK);
        ColorUIResource bgPanel    = new ColorUIResource(BG_PANEL);
        ColorUIResource bgSurface  = new ColorUIResource(BG_SURFACE);
        ColorUIResource fgText     = new ColorUIResource(TEXT_PRIMARY);
        ColorUIResource accent     = new ColorUIResource(ACCENT);

        UIManager.put("Panel.background", bgPanel);
        UIManager.put("Panel.foreground", fgText);

        UIManager.put("Label.foreground", fgText);
        UIManager.put("Label.font", FONT_REGULAR);

        UIManager.put("TextField.background", bgSurface);
        UIManager.put("TextField.foreground", fgText);
        UIManager.put("TextField.caretForeground", fgText);
        UIManager.put("TextField.font", FONT_REGULAR);
        UIManager.put("TextField.border", BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)));

        UIManager.put("TextArea.background", bgSurface);
        UIManager.put("TextArea.foreground", fgText);
        UIManager.put("TextArea.caretForeground", fgText);
        UIManager.put("TextArea.font", FONT_REGULAR);

        UIManager.put("Button.background", bgSurface);
        UIManager.put("Button.foreground", fgText);
        UIManager.put("Button.font", FONT_REGULAR);
        UIManager.put("Button.focus", new ColorUIResource(BG_SURFACE));

        UIManager.put("ComboBox.background", bgSurface);
        UIManager.put("ComboBox.foreground", fgText);
        UIManager.put("ComboBox.selectionBackground", accent);
        UIManager.put("ComboBox.selectionForeground", bgDark);
        UIManager.put("ComboBox.font", FONT_REGULAR);

        UIManager.put("Table.background", bgPanel);
        UIManager.put("Table.foreground", fgText);
        UIManager.put("Table.selectionBackground", accent);
        UIManager.put("Table.selectionForeground", bgDark);
        UIManager.put("Table.gridColor", new ColorUIResource(BORDER));
        UIManager.put("Table.font", FONT_TABLE);

        UIManager.put("TableHeader.background", bgSurface);
        UIManager.put("TableHeader.foreground", accent);
        UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 12));

        UIManager.put("ScrollPane.background", bgPanel);
        UIManager.put("ScrollBar.background", bgDark);
        UIManager.put("ScrollBar.thumb", bgSurface);
        UIManager.put("ScrollBar.track", bgDark);

        UIManager.put("TabbedPane.background", bgDark);
        UIManager.put("TabbedPane.foreground", fgText);
        UIManager.put("TabbedPane.selected", bgPanel);
        UIManager.put("TabbedPane.contentAreaColor", bgPanel);

        UIManager.put("TitledBorder.titleColor", accent);
        UIManager.put("TitledBorder.border", BorderFactory.createLineBorder(BORDER));

        UIManager.put("OptionPane.background", bgPanel);
        UIManager.put("OptionPane.messageForeground", fgText);
        UIManager.put("OptionPane.messageFont", FONT_REGULAR);

        UIManager.put("List.background", bgSurface);
        UIManager.put("List.foreground", fgText);
        UIManager.put("List.selectionBackground", accent);
        UIManager.put("List.selectionForeground", bgDark);
    }

    private static class EmeraldTabbedPaneUI extends BasicTabbedPaneUI {

        @Override
        protected void installDefaults() {
            super.installDefaults();
            tabAreaInsets = new Insets(4, 8, 0, 8);
            tabInsets = new Insets(10, 20, 10, 20);
            selectedTabPadInsets = new Insets(0, 0, 0, 0);
            contentBorderInsets = new Insets(2, 0, 0, 0);
        }

        @Override
        protected void paintTabArea(Graphics g, int tabPlacement, int selectedIndex) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Fondo del área de pestañas
            Rectangle bounds = g2.getClipBounds();
            g2.setColor(BG_DARK);
            g2.fillRect(0, 0, tabPane.getWidth(), rects[0].y + rects[0].height + 4);

            g2.dispose();
            super.paintTabArea(g, tabPlacement, selectedIndex);
        }

        @Override
        protected void paintTabBackground(Graphics g, int tabPlacement,
                int tabIndex, int x, int y, int w, int h, boolean isSelected) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (isSelected) {
                // Pestaña seleccionada: fondo panel con gradiente sutil
                GradientPaint gp = new GradientPaint(x, y, BG_PANEL, x, y + h, BG_PANEL);
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Double(x + 1, y + 1, w - 2, h + 4, 10, 10));

                // Barra de acento esmeralda arriba
                g2.setColor(ACCENT);
                g2.fill(new RoundRectangle2D.Double(x + 4, y + 1, w - 8, 3, 3, 3));
            } else {
                // Pestaña no seleccionada: fondo oscuro
                g2.setColor(BG_DARK);
                g2.fill(new RoundRectangle2D.Double(x + 1, y + 3, w - 2, h - 2, 8, 8));
            }

            g2.dispose();
        }

        @Override
        protected void paintTabBorder(Graphics g, int tabPlacement,
                int tabIndex, int x, int y, int w, int h, boolean isSelected) {
            // No pintar bordes nativos — los bordes redondeados ya están en paintTabBackground
        }

        @Override
        protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Insets insets = tabPane.getInsets();
            int x = insets.left;
            int y = insets.top + calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
            int w = tabPane.getWidth() - insets.left - insets.right;
            int h = tabPane.getHeight() - y - insets.bottom;

            // Borde superior del contenido con línea de acento
            g2.setColor(ACCENT_DARK);
            g2.fillRect(x, y, w, 2);

            // Fondo del área de contenido
            g2.setColor(BG_PANEL);
            g2.fillRect(x, y + 2, w, h - 2);

            g2.dispose();
        }

        @Override
        protected void paintText(Graphics g, int tabPlacement, Font font,
                FontMetrics metrics, int tabIndex, String title,
                Rectangle textRect, boolean isSelected) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            if (isSelected) {
                g2.setColor(ACCENT_LIGHT);
                g2.setFont(font.deriveFont(Font.BOLD));
            } else {
                g2.setColor(TEXT_SECONDARY);
                g2.setFont(font);
            }

            g2.drawString(title, textRect.x, textRect.y + metrics.getAscent());
            g2.dispose();
        }

        @Override
        protected void paintFocusIndicator(Graphics g, int tabPlacement,
                Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect,
                boolean isSelected) {
            // No pintar el rectángulo de foco nativo
        }

        @Override
        protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
            return fontHeight + 20;
        }

        @Override
        protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
            return super.calculateTabWidth(tabPlacement, tabIndex, metrics) + 16;
        }
    }

    public static JButton createModernButton(String text, Color bgColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 10, 10));

                g2.setColor(getBackground().darker());
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 10, 10));

                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bgColor);
        btn.setForeground(BG_DARK);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        Color hoverColor = brighter(bgColor, 0.2f);
        Color pressColor = bgColor.darker();

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(hoverColor);
                btn.repaint();
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(bgColor);
                btn.repaint();
            }
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                btn.setBackground(pressColor);
                btn.repaint();
            }
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                btn.setBackground(hoverColor);
                btn.repaint();
            }
        });

        return btn;
    }

    private static Color brighter(Color c, float factor) {
        int r = Math.min(255, (int)(c.getRed() + (255 - c.getRed()) * factor));
        int g = Math.min(255, (int)(c.getGreen() + (255 - c.getGreen()) * factor));
        int b = Math.min(255, (int)(c.getBlue() + (255 - c.getBlue()) * factor));
        return new Color(r, g, b);
    }
}
