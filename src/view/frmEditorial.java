package view;
import conection.Conexion;
import model.Editorial;
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
public class frmEditorial extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(frmEditorial.class.getName());
    private DefaultTableModel tableModel;

    public frmEditorial() {
        initComponents();
        this.setTitle("Gestion de editoriales");
        this.setLocationRelativeTo(null);
        setupTable();
        loadEditorials();
        addSearchListener();
    }

    private void setupTable() {
        // Set up table model with appropriate columns
        tableModel = new DefaultTableModel(new Object[]{"ID", "Nombre", "País"}, 0);
        tbtEditoriales.setModel(tableModel);
    }

    private void loadEditorials() {
        // Clear existing rows
        tableModel.setRowCount(0);
        String query = "SELECT id_editorial, nombre, pais FROM Editoriales";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id_editorial"),
                        rs.getString("nombre"),
                        rs.getString("pais")
                });
            }
        } catch (SQLException ex) {
            logger.log(java.util.logging.Level.SEVERE, "Error al cargar editoriales", ex);
            JOptionPane.showMessageDialog(this, "Error al cargar editoriales.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadEditorialsById(int id) {
        // Clear existing rows
        tableModel.setRowCount(0);
        String query = "SELECT id_editorial, nombre, pais FROM Editoriales WHERE id_editorial = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    tableModel.addRow(new Object[]{
                            rs.getInt("id_editorial"),
                            rs.getString("nombre"),
                            rs.getString("pais")
                    });
                }
            }
        } catch (SQLException ex) {
            logger.log(java.util.logging.Level.SEVERE, "Error al buscar editorial por ID", ex);
            JOptionPane.showMessageDialog(this, "Error al buscar editorial.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addSearchListener() {
        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = txtBuscar.getText().trim();
                if (text.isEmpty()) {
                    loadEditorials();
                } else {
                    try {
                        int id = Integer.parseInt(text);
                        loadEditorialsById(id);
                    } catch (NumberFormatException ex) {
                        loadEditorials(); // If invalid input, reload all
                    }
                }
            }
        });
    }

    private JDialog createEditorialDialog(boolean isEdit, Editorial editorial) {
        JDialog dialog = new JDialog(this, isEdit ? "Modificar Editorial" : "Registrar Editorial", true);
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);

        JLabel lblNombre = new JLabel("Nombre:");
        JLabel lblPais = new JLabel("País:");
        JTextField txtNombre = new JTextField(20);
        JTextField txtPais = new JTextField(20);
        JButton btnSave = new JButton(isEdit ? "Modificar" : "Registrar");
        JButton btnCancel = new JButton("Cancelar");

        // Pre-fill fields if editing
        if (isEdit && editorial != null) {
            txtNombre.setText(editorial.getNombre());
            txtPais.setText(editorial.getPais());
        }

        btnSave.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String pais = txtPais.getText().trim();
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "El nombre es obligatorio.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = Conexion.getConnection()) {
                if (isEdit && editorial != null) {
                    // Update editorial
                    String query = "UPDATE Editoriales SET nombre = ?, pais = ? WHERE id_editorial = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(query)) {
                        stmt.setString(1, nombre);
                        stmt.setString(2, pais.isEmpty() ? null : pais);
                        stmt.setInt(3, editorial.getIdEditorial());
                        stmt.executeUpdate();
                        JOptionPane.showMessageDialog(dialog, "Editorial modificada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    // Insert new editorial
                    String query = "INSERT INTO Editoriales (nombre, pais) VALUES (?, ?)";
                    try (PreparedStatement stmt = conn.prepareStatement(query)) {
                        stmt.setString(1, nombre);
                        stmt.setString(2, pais.isEmpty() ? null : pais);
                        stmt.executeUpdate();
                        JOptionPane.showMessageDialog(dialog, "Editorial registrada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                loadEditorials(); // Refresh table
                dialog.dispose();
            } catch (SQLException ex) {
                logger.log(java.util.logging.Level.SEVERE, "Error al guardar editorial", ex);
                JOptionPane.showMessageDialog(dialog, "Error al guardar editorial.", "Error", JOptionPane.ERROR_MESSAGE);
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
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(lblNombre)
                                        .addComponent(lblPais))
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(txtNombre)
                                        .addComponent(txtPais)))
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
                                .addComponent(lblPais)
                                .addComponent(txtPais))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(btnSave)
                                .addComponent(btnCancel))
        );

        return dialog;
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        jScrollPane1 = new javax.swing.JScrollPane();
        tbtEditoriales = new javax.swing.JTable();
        txtBuscar = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        btnBuscar = new javax.swing.JButton();
        btnRegistrar = new javax.swing.JButton();
        btnModificar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE); // Changed to DISPOSE_ON_CLOSE

        tbtEditoriales.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Nombre", "País"}
        ));
        jScrollPane1.setViewportView(tbtEditoriales);

        jLabel1.setText("Buscar:");

        btnBuscar.setText("Buscar");
        btnBuscar.addActionListener(e -> {
            String text = txtBuscar.getText().trim();
            if (text.isEmpty()) {
                loadEditorials();
            } else {
                try {
                    int id = Integer.parseInt(text);
                    loadEditorialsById(id);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Por favor, ingrese un ID válido.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnRegistrar.setText("Registrar");
        btnRegistrar.addActionListener(e -> {
            JDialog dialog = createEditorialDialog(false, null);
            dialog.setVisible(true);
        });

        btnModificar.setText("Modificar");
        btnModificar.addActionListener(e -> {
            int selectedRow = tbtEditoriales.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione una editorial para modificar.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            String nombre = (String) tableModel.getValueAt(selectedRow, 1);
            String pais = (String) tableModel.getValueAt(selectedRow, 2);
            Editorial editorial = new Editorial(id, nombre, pais);
            JDialog dialog = createEditorialDialog(true, editorial);
            dialog.setVisible(true);
        });

        btnEliminar.setText("Eliminar");
        btnEliminar.addActionListener(e -> {
            int selectedRow = tbtEditoriales.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione una editorial para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar esta editorial?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = Conexion.getConnection();
                     PreparedStatement stmt = conn.prepareStatement("DELETE FROM Editoriales WHERE id_editorial = ?")) {
                    stmt.setInt(1, id);
                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Editorial eliminada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    loadEditorials();
                } catch (SQLException ex) {
                    logger.log(java.util.logging.Level.SEVERE, "Error al eliminar editorial", ex);
                    JOptionPane.showMessageDialog(this, "Error al eliminar editorial. Puede estar en uso.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        jLabel2.setText("Apartado de Editoriales");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap(71, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel1)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(70, 70, 70)
                                                .addComponent(btnBuscar))
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(btnRegistrar)
                                                .addGap(88, 88, 88)
                                                .addComponent(btnModificar)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(btnEliminar)))
                                .addGap(49, 49, 49))
                        .addGroup(layout.createSequentialGroup()
                                .addGap(198, 198, 198)
                                .addComponent(jLabel2)
                                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel2)
                                .addGap(36, 36, 36)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1)
                                        .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnBuscar))
                                .addGap(46, 46, 46)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnRegistrar)
                                        .addComponent(btnModificar)
                                        .addComponent(btnEliminar))
                                .addGap(76, 76, 76)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(36, Short.MAX_VALUE))
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
        java.awt.EventQueue.invokeLater(() -> new frmEditorial().setVisible(true));
    }

    // Variables declaration
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnModificar;
    private javax.swing.JButton btnRegistrar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tbtEditoriales;
    private javax.swing.JTextField txtBuscar;
}
