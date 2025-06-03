package view;

import controller.PrestamoDAO;
import model.Prestamo;
import model.Usuario;
import model.Libro;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class frmPrestamos extends javax.swing.JFrame {

    private PrestamoDAO prestamoDAO;
    private DefaultTableModel activeTableModel;
    private DefaultTableModel overdueTableModel;
    private static final Logger logger = Logger.getLogger(frmPrestamos.class.getName());

    public frmPrestamos() {
        initComponents();
        this.setTitle("Gestión de Préstamos");
        this.setLocationRelativeTo(null);
        prestamoDAO = new PrestamoDAO();
        setupTables();
        loadPrestamos();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        jTabbedPane = new JTabbedPane();
        jScrollPaneActive = new javax.swing.JScrollPane();
        tbtPrestamosActivos = new javax.swing.JTable();
        jScrollPaneOverdue = new javax.swing.JScrollPane();
        tbtPrestamosVencidos = new javax.swing.JTable();
        btnRegistrar = new javax.swing.JButton();
        btnDevolucion = new javax.swing.JButton();
        btnHistorial = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        activeTableModel = new DefaultTableModel(
                new Object[]{"ID Préstamo", "Usuario", "Libro", "Fecha Préstamo", "Fecha Límite"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tbtPrestamosActivos.setModel(activeTableModel);
        jScrollPaneActive.setViewportView(tbtPrestamosActivos);

        overdueTableModel = new DefaultTableModel(
                new Object[]{"ID Préstamo", "Usuario", "Libro", "Fecha Préstamo", "Fecha Límite"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tbtPrestamosVencidos.setModel(overdueTableModel);
        jScrollPaneOverdue.setViewportView(tbtPrestamosVencidos);

        jTabbedPane.addTab("Préstamos Activos", jScrollPaneActive);
        jTabbedPane.addTab("Préstamos Vencidos", jScrollPaneOverdue);

        btnRegistrar.setText("Registrar");
        btnRegistrar.addActionListener(evt -> btnRegistrarActionPerformed(evt));

        btnDevolucion.setText("Devolución");
        btnDevolucion.addActionListener(evt -> btnDevolucionActionPerformed(evt));

        btnHistorial.setText("Historial");
        btnHistorial.addActionListener(evt -> btnHistorialActionPerformed(evt));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(27, 27, 27)
                                .addComponent(btnRegistrar)
                                .addGap(43, 43, 43)
                                .addComponent(btnDevolucion)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                                .addComponent(btnHistorial)
                                .addGap(42, 42, 42))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 507, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(58, 58, 58))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnRegistrar)
                                        .addComponent(btnDevolucion)
                                        .addComponent(btnHistorial))
                                .addGap(18, 18, 18)
                                .addComponent(jTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(37, 37, 37))
        );

        pack();
    }

    private void btnRegistrarActionPerformed(java.awt.event.ActionEvent evt) {
        JDialog dialog = new JDialog(this, "Registrar Préstamo", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);

        GridBagLayout layout = new GridBagLayout();
        dialog.setLayout(layout);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<String> cmbUsuario = new JComboBox<>(getUsuarios());
        JComboBox<String> cmbLibro = new JComboBox<>(getLibros());
        JTextField txtFechaPrestamo = new JTextField(new Date().toString(), 20);
        txtFechaPrestamo.setEnabled(false);

        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Usuario:"), gbc);
        gbc.gridx = 1;
        dialog.add(cmbUsuario, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Libro (ISBN):"), gbc);
        gbc.gridx = 1;
        dialog.add(cmbLibro, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Fecha Préstamo:"), gbc);
        gbc.gridx = 1;
        dialog.add(txtFechaPrestamo, gbc);

        JButton btnSave = new JButton("Guardar");
        JButton btnCancel = new JButton("Cancelar");

        btnSave.addActionListener(e -> {
            try {
                if (cmbUsuario.getSelectedIndex() == -1 || cmbLibro.getSelectedIndex() == -1) {
                    JOptionPane.showMessageDialog(dialog, "Seleccione un usuario y un libro");
                    return;
                }

                Prestamo prestamo = new Prestamo();
                prestamo.setIdUsuario(getUsuariosIds()[cmbUsuario.getSelectedIndex()]);
                prestamo.setISBN(getLibrosIsbns()[cmbLibro.getSelectedIndex()]);
                prestamo.setFechaPrestamo(new Date());

                prestamoDAO.registrarPrestamo(prestamo);
                loadPrestamos();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Préstamo registrado exitosamente");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error al registrar préstamo: " + ex.getMessage());
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);

        dialog.setVisible(true);
    }

    private void btnDevolucionActionPerformed(java.awt.event.ActionEvent evt) {
        JTable selectedTable = jTabbedPane.getSelectedIndex() == 0 ? tbtPrestamosActivos : tbtPrestamosVencidos;
        DefaultTableModel selectedModel = (DefaultTableModel) selectedTable.getModel();
        int selectedRow = selectedTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un préstamo para registrar devolución");
            return;
        }

        int idPrestamo = (int) selectedModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Registrar devolución para el préstamo con ID " + idPrestamo + "?",
                "Confirmar Devolución",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                prestamoDAO.registrarDevolucion(idPrestamo);
                loadPrestamos();
                JOptionPane.showMessageDialog(this, "Devolución registrada exitosamente");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al registrar devolución: " + ex.getMessage());
            }
        }
    }

    private void btnHistorialActionPerformed(java.awt.event.ActionEvent evt) {
        JDialog dialog = new JDialog(this, "Consultar Historial", true);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout());
        GridBagLayout layout = new GridBagLayout();
        JPanel inputPanel = new JPanel(layout);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<String> cmbCriterio = new JComboBox<>(new String[]{"id_usuario", "ISBN"});
        JTextField txtValor = new JTextField(20);

        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Criterio:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(cmbCriterio, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("Valor:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(txtValor, gbc);

        DefaultTableModel historyTableModel = new DefaultTableModel(
                new Object[]{"ID Préstamo", "Usuario", "Libro", "Fecha Préstamo", "Fecha Devolución", "Fecha Límite"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable historyTable = new JTable(historyTableModel);
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(historyTable);

        historyTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        historyTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        historyTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        historyTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        historyTable.getColumnModel().getColumn(4).setPreferredWidth(150);
        historyTable.getColumnModel().getColumn(5).setPreferredWidth(150);

        JButton btnBuscar = new JButton("Buscar");
        JButton btnCancel = new JButton("Cancelar");

        btnBuscar.addActionListener(e -> {
            try {
                String criterio = (String) cmbCriterio.getSelectedItem();
                String valor = txtValor.getText().trim();
                historyTableModel.setRowCount(0);
                List<Prestamo> prestamos = prestamoDAO.getHistorialPrestamos(criterio, valor);
                for (Prestamo prestamo : prestamos) {
                    Object[] row = {
                            prestamo.getIdPrestamo(),
                            prestamo.getIdUsuario() + " - " + getNombreUsuario(prestamo.getIdUsuario()),
                            prestamo.getISBN() + " - " + getTituloLibro(prestamo.getISBN()),
                            prestamo.getFechaPrestamo(),
                            prestamo.getFechaDevolucion(),
                            prestamo.getFechaLimite()
                    };
                    historyTableModel.addRow(row);
                }
                if (prestamos.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "No se encontraron préstamos para el criterio especificado.");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error al consultar historial: " + ex.getMessage());
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnBuscar);
        buttonPanel.add(btnCancel);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        inputPanel.add(buttonPanel, gbc);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void setupTables() {
        tbtPrestamosActivos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbtPrestamosVencidos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tbtPrestamosActivos.getColumnModel().getColumn(0).setPreferredWidth(80);
        tbtPrestamosActivos.getColumnModel().getColumn(1).setPreferredWidth(150);
        tbtPrestamosActivos.getColumnModel().getColumn(2).setPreferredWidth(200);
        tbtPrestamosActivos.getColumnModel().getColumn(3).setPreferredWidth(150);
        tbtPrestamosActivos.getColumnModel().getColumn(4).setPreferredWidth(150);

        tbtPrestamosVencidos.getColumnModel().getColumn(0).setPreferredWidth(80);
        tbtPrestamosVencidos.getColumnModel().getColumn(1).setPreferredWidth(150);
        tbtPrestamosVencidos.getColumnModel().getColumn(2).setPreferredWidth(200);
        tbtPrestamosVencidos.getColumnModel().getColumn(3).setPreferredWidth(150);
        tbtPrestamosVencidos.getColumnModel().getColumn(4).setPreferredWidth(150);

        MouseAdapter mouseListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                btnDevolucion.setEnabled(tbtPrestamosActivos.getSelectedRow() != -1 || tbtPrestamosVencidos.getSelectedRow() != -1);
            }
        };
        tbtPrestamosActivos.addMouseListener(mouseListener);
        tbtPrestamosVencidos.addMouseListener(mouseListener);
    }

    private void loadPrestamos() {
        try {
            // Check and generate fines before loading
            prestamoDAO.checkAndGenerateFines();

            activeTableModel.setRowCount(0);
            List<Prestamo> prestamosActivos = prestamoDAO.getPrestamosActivos();
            for (Prestamo prestamo : prestamosActivos) {
                Object[] row = {
                        prestamo.getIdPrestamo(),
                        prestamo.getIdUsuario() + " - " + getNombreUsuario(prestamo.getIdUsuario()),
                        prestamo.getISBN() + " - " + getTituloLibro(prestamo.getISBN()),
                        prestamo.getFechaPrestamo(),
                        prestamo.getFechaLimite()
                };
                activeTableModel.addRow(row);
            }

            overdueTableModel.setRowCount(0);
            List<Prestamo> prestamosVencidos = prestamoDAO.getPrestamosVencidos();
            for (Prestamo prestamo : prestamosVencidos) {
                Object[] row = {
                        prestamo.getIdPrestamo(),
                        prestamo.getIdUsuario() + " - " + getNombreUsuario(prestamo.getIdUsuario()),
                        prestamo.getISBN() + " - " + getTituloLibro(prestamo.getISBN()),
                        prestamo.getFechaPrestamo(),
                        prestamo.getFechaLimite()
                };
                overdueTableModel.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar préstamos: " + ex.getMessage());
        }
    }

    private String getNombreUsuario(int idUsuario) throws SQLException {
        List<Usuario> usuarios = prestamoDAO.getAllUsuarios();
        return usuarios.stream()
                .filter(u -> u.getIdUsuario() == idUsuario)
                .findFirst()
                .map(u -> u.getNombre() + " " + u.getApellido())
                .orElse("Desconocido");
    }

    private String getTituloLibro(String ISBN) throws SQLException {
        List<Libro> libros = prestamoDAO.getAllLibros();
        return libros.stream()
                .filter(l -> l.getISBN().equals(ISBN))
                .findFirst()
                .map(Libro::getTitulo)
                .orElse("Desconocido");
    }

    private String[] getUsuarios() {
        try {
            List<Usuario> usuarios = prestamoDAO.getAllUsuarios();
            return usuarios.stream()
                    .map(u -> u.getIdUsuario() + " - " + u.getNombre() + " " + u.getApellido())
                    .toArray(String[]::new);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar usuarios: " + ex.getMessage());
            return new String[]{};
        }
    }

    private int[] getUsuariosIds() {
        try {
            List<Usuario> usuarios = prestamoDAO.getAllUsuarios();
            return usuarios.stream().mapToInt(Usuario::getIdUsuario).toArray();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar usuarios: " + ex.getMessage());
            return new int[]{};
        }
    }

    private String[] getLibros() {
        try {
            List<Libro> libros = prestamoDAO.getAllLibros();
            if (libros.isEmpty()) {
                logger.warning("No books found in the database.");
                JOptionPane.showMessageDialog(this, "No se encontraron libros en la base de datos.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return new String[]{"No hay libros disponibles"};
            }
            return libros.stream()
                    .map(l -> l.getISBN() + " - " + l.getTitulo())
                    .toArray(String[]::new);
        } catch (SQLException ex) {
            logger.severe("Error loading books: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Error al cargar libros: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return new String[]{"Error al cargar libros"};
        }
    }

    private String[] getLibrosIsbns() {
        try {
            List<Libro> libros = prestamoDAO.getAllLibros();
            return libros.stream().map(Libro::getISBN).toArray(String[]::new);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar libros: " + ex.getMessage());
            return new String[]{};
        }
    }

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> new frmPrestamos().setVisible(true));
    }

    private javax.swing.JButton btnDevolucion;
    private javax.swing.JButton btnHistorial;
    private javax.swing.JButton btnRegistrar;
    private javax.swing.JScrollPane jScrollPaneActive;
    private javax.swing.JScrollPane jScrollPaneOverdue;
    private JTabbedPane jTabbedPane;
    private javax.swing.JTable tbtPrestamosActivos;
    private javax.swing.JTable tbtPrestamosVencidos;
}