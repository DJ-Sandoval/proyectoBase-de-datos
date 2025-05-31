package view;

import conection.Conexion;
import model.Categoria;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 *
 * @author jose
 */
public class frmCategorias extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(frmCategorias.class.getName());
    private DefaultTableModel tableModel;

    public frmCategorias() {
        initComponents();
        this.setTitle("Gestion de categorias");
        this.setLocationRelativeTo(null);
        setupTable();
        loadCategories();
        addSearchListener();
    }

    private void setupTable() {
        // Set up table model with appropriate columns
        tableModel = new DefaultTableModel(new Object[]{"ID", "Nombre Categoría"}, 0);
        tbtCategorias.setModel(tableModel);
    }

    private void loadCategories() {
        // Clear existing rows
        tableModel.setRowCount(0);
        String query = "SELECT id_categoria, nombre_categoria FROM Categorias";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id_categoria"),
                        rs.getString("nombre_categoria")
                });
            }
        } catch (SQLException ex) {
            logger.log(java.util.logging.Level.SEVERE, "Error al cargar categorías", ex);
            JOptionPane.showMessageDialog(this, "Error al cargar categorías.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadCategoriesById(int id) {
        // Clear existing rows
        tableModel.setRowCount(0);
        String query = "SELECT id_categoria, nombre_categoria FROM Categorias WHERE id_categoria = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    tableModel.addRow(new Object[]{
                            rs.getInt("id_categoria"),
                            rs.getString("nombre_categoria")
                    });
                }
            }
        } catch (SQLException ex) {
            logger.log(java.util.logging.Level.SEVERE, "Error al buscar categoría por ID", ex);
            JOptionPane.showMessageDialog(this, "Error al buscar categoría.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addSearchListener() {
        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = txtBuscar.getText().trim();
                if (text.isEmpty()) {
                    loadCategories();
                } else {
                    try {
                        int id = Integer.parseInt(text);
                        loadCategoriesById(id);
                    } catch (NumberFormatException ex) {
                        loadCategories(); // If invalid input, reload all
                    }
                }
            }
        });
    }

    private JDialog createCategoryDialog(boolean isEdit, Categoria categoria) {
        JDialog dialog = new JDialog(this, isEdit ? "Modificar Categoría" : "Registrar Categoría", true);
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(this);

        JLabel lblNombre = new JLabel("Nombre Categoría:");
        JTextField txtNombre = new JTextField(20);
        JButton btnSave = new JButton(isEdit ? "Modificar" : "Registrar");
        JButton btnCancel = new JButton("Cancelar");

        // Pre-fill field if editing
        if (isEdit && categoria != null) {
            txtNombre.setText(categoria.getNombre());
        }

        btnSave.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "El nombre de la categoría es obligatorio.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = Conexion.getConnection()) {
                if (isEdit && categoria != null) {
                    // Update category
                    String query = "UPDATE Categorias SET nombre_categoria = ? WHERE id_categoria = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(query)) {
                        stmt.setString(1, nombre);
                        stmt.setInt(2, categoria.getIdCategoria());
                        stmt.executeUpdate();
                        JOptionPane.showMessageDialog(dialog, "Categoría modificada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    // Insert new category
                    String query = "INSERT INTO Categorias (nombre_categoria) VALUES (?)";
                    try (PreparedStatement stmt = conn.prepareStatement(query)) {
                        stmt.setString(1, nombre);
                        stmt.executeUpdate();
                        JOptionPane.showMessageDialog(dialog, "Categoría registrada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                loadCategories(); // Refresh table
                dialog.dispose();
            } catch (SQLException ex) {
                logger.log(java.util.logging.Level.SEVERE, "Error al guardar categoría", ex);
                JOptionPane.showMessageDialog(dialog, "Error al guardar categoría.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        // Layout for dialog
        GroupLayout layout = new GroupLayout(dialog.getContentPane());
        dialog.getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(lblNombre)
                                .addComponent(txtNombre))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(btnSave)
                                .addComponent(btnCancel))
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblNombre)
                                .addComponent(txtNombre))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(btnSave)
                                .addComponent(btnCancel))
        );

        return dialog;
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbtCategorias = new javax.swing.JTable();
        btnRegistrar = new javax.swing.JButton();
        btnModificar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        txtBuscar = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        btnBuscar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE); // Changed to DISPOSE_ON_CLOSE

        jLabel1.setText("Apartado de categorías");

        tbtCategorias.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Nombre Categoría"}
        ));
        jScrollPane1.setViewportView(tbtCategorias);

        btnRegistrar.setText("Registrar");
        btnRegistrar.addActionListener(e -> {
            JDialog dialog = createCategoryDialog(false, null);
            dialog.setVisible(true);
        });

        btnModificar.setText("Modificar");
        btnModificar.addActionListener(e -> {
            int selectedRow = tbtCategorias.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione una categoría para modificar.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            String nombre = (String) tableModel.getValueAt(selectedRow, 1);
            Categoria categoria = new Categoria(id, nombre);
            JDialog dialog = createCategoryDialog(true, categoria);
            dialog.setVisible(true);
        });

        btnEliminar.setText("Eliminar");
        btnEliminar.addActionListener(e -> {
            int selectedRow = tbtCategorias.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione una categoría para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar esta categoría?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = Conexion.getConnection();
                     PreparedStatement stmt = conn.prepareStatement("DELETE FROM Categorias WHERE id_categoria = ?")) {
                    stmt.setInt(1, id);
                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Categoría eliminada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    loadCategories();
                } catch (SQLException ex) {
                    logger.log(java.util.logging.Level.SEVERE, "Error al eliminar categoría", ex);
                    JOptionPane.showMessageDialog(this, "Error al eliminar categoría. Puede estar en uso.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        jLabel2.setText("Buscar:");

        btnBuscar.setText("Buscar");
        btnBuscar.addActionListener(e -> {
            String text = txtBuscar.getText().trim();
            if (text.isEmpty()) {
                loadCategories();
            } else {
                try {
                    int id = Integer.parseInt(text);
                    loadCategoriesById(id);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Por favor, ingrese un ID válido.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap(46, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGap(31, 31, 31))
                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                        .addComponent(jLabel1)
                                                        .addGap(188, 188, 188)))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(btnRegistrar)
                                                .addGap(78, 78, 78)
                                                .addComponent(btnModificar)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(btnEliminar)
                                                .addGap(39, 39, 39))))
                        .addGroup(layout.createSequentialGroup()
                                .addGap(58, 58, 58)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(35, 35, 35)
                                .addComponent(btnBuscar)
                                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel2)
                                .addComponent(btnBuscar))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnRegistrar)
                                .addComponent(btnModificar)
                                .addComponent(btnEliminar))
                        .addGap(47, 47, 47)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(22, 22, 22)
        );

        pack();
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
        java.awt.EventQueue.invokeLater(() -> new frmCategorias().setVisible(true));
    }

    // Variables declaration
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnModificar;
    private javax.swing.JButton btnRegistrar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tbtCategorias;
    private javax.swing.JTextField txtBuscar;
}
