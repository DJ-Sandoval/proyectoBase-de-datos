package view;

import conection.Conexion;
import controller.MultaDAO;
import model.Multa;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JOptionPane;
import java.util.logging.Logger;

public class frmMultas extends javax.swing.JFrame {

    private static final Logger logger = Logger.getLogger(frmMultas.class.getName());
    private final MultaDAO multaDAO;

    public frmMultas() {
        initComponents();
        this.setTitle("Gestión de Multas");
        this.setLocationRelativeTo(null);
        multaDAO = new MultaDAO();
        configureTable();
        loadPendingFines();
    }

    private void configureTable() {
        DefaultTableModel model = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID Multa", "ID Préstamo", "Usuario", "Monto", "Fecha Emisión", "Estado"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tbtMultas.setModel(model);
        tbtMultas.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tbtMultas.getColumnModel().getColumn(0).setPreferredWidth(80);
        tbtMultas.getColumnModel().getColumn(1).setPreferredWidth(80);
        tbtMultas.getColumnModel().getColumn(2).setPreferredWidth(150);
        tbtMultas.getColumnModel().getColumn(3).setPreferredWidth(100);
        tbtMultas.getColumnModel().getColumn(4).setPreferredWidth(150);
        tbtMultas.getColumnModel().getColumn(5).setPreferredWidth(100);
    }

    private void loadPendingFines() {
        try {
            List<Multa> multas = multaDAO.consultarMultasPendientes(0); // 0 to get all pending fines
            DefaultTableModel model = (DefaultTableModel) tbtMultas.getModel();
            model.setRowCount(0);
            for (Multa multa : multas) {
                String usuarioNombre = getNombreUsuario(multa.getIdPrestamo());
                model.addRow(new Object[]{
                        multa.getIdMulta(),
                        multa.getIdPrestamo(),
                        usuarioNombre,
                        multa.getMonto(),
                        multa.getFechaEmision(),
                        multa.getEstado()
                });
            }
            if (multas.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay multas pendientes en el sistema", "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar multas pendientes: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getNombreUsuario(int idPrestamo) throws SQLException {
        String sql = "SELECT u.nombre, u.apellido FROM Usuarios u JOIN Prestamos p ON u.id_usuario = p.id_usuario WHERE p.id_prestamo = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPrestamo);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("nombre") + " " + rs.getString("apellido");
            }
        }
        return "Desconocido";
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        jScrollPane1 = new javax.swing.JScrollPane();
        tbtMultas = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        btnGenerarMulta = new javax.swing.JButton();
        btnRegistrarPago = new javax.swing.JButton();
        btnConsultar = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtIdUsuario = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtIdPrestamo = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtIdMulta = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        tbtMultas.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{"ID Multa", "ID Préstamo", "Usuario", "Monto", "Fecha Emisión", "Estado"}
        ));
        jScrollPane1.setViewportView(tbtMultas);

        jLabel1.setFont(new java.awt.Font("Liberation Sans", 1, 18));
        jLabel1.setText("Apartado de Multas");

        btnGenerarMulta.setText("Generar Multa");
        btnGenerarMulta.addActionListener(evt -> btnGenerarMultaActionPerformed(evt));

        btnRegistrarPago.setText("Registrar Pago");
        btnRegistrarPago.addActionListener(evt -> btnRegistrarPagoActionPerformed(evt));

        btnConsultar.setText("Consultar por Usuario");
        btnConsultar.addActionListener(evt -> btnConsultarActionPerformed(evt));

        jLabel2.setText("ID Usuario:");
        txtIdUsuario.setText("");

        jLabel3.setText("ID Préstamo:");
        txtIdPrestamo.setText("");

        jLabel4.setText("ID Multa:");
        txtIdMulta.setText("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(181, 181, 181)
                                .addComponent(jLabel1)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap(64, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel2)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(txtIdUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(jLabel3)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(txtIdPrestamo, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(jLabel4)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(txtIdMulta, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 600, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(btnGenerarMulta)
                                                .addGap(30, 30, 30)
                                                .addComponent(btnRegistrarPago)
                                                .addGap(30, 30, 30)
                                                .addComponent(btnConsultar)))
                                .addGap(38, 38, 38))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel2)
                                        .addComponent(txtIdUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel3)
                                        .addComponent(txtIdPrestamo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel4)
                                        .addComponent(txtIdMulta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnGenerarMulta)
                                        .addComponent(btnRegistrarPago)
                                        .addComponent(btnConsultar))
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(40, Short.MAX_VALUE))
        );

        pack();
    }

    private void btnGenerarMultaActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            int idPrestamo = Integer.parseInt(txtIdPrestamo.getText().trim());
            double monto = 5.0; // Fixed fine amount
            boolean success = multaDAO.generarMulta(idPrestamo, monto);
            if (success) {
                JOptionPane.showMessageDialog(this, "Multa generada exitosamente por $" + monto);
                loadPendingFines();
            } else {
                JOptionPane.showMessageDialog(this, "Error al generar la multa", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese un ID de préstamo válido", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btnRegistrarPagoActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            int idMulta = Integer.parseInt(txtIdMulta.getText().trim());
            boolean success = multaDAO.registrarPago(idMulta);
            if (success) {
                JOptionPane.showMessageDialog(this, "Pago registrado exitosamente");
                loadPendingFines();
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar el pago", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese un ID de multa válido", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btnConsultarActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            int idUsuario = Integer.parseInt(txtIdUsuario.getText().trim());
            List<Multa> multas = multaDAO.consultarMultasPendientes(idUsuario);
            DefaultTableModel model = (DefaultTableModel) tbtMultas.getModel();
            model.setRowCount(0);
            for (Multa multa : multas) {
                String usuarioNombre = getNombreUsuario(multa.getIdPrestamo());
                model.addRow(new Object[]{
                        multa.getIdMulta(),
                        multa.getIdPrestamo(),
                        usuarioNombre,
                        multa.getMonto(),
                        multa.getFechaEmision(),
                        multa.getEstado()
                });
            }
            if (multas.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay multas pendientes para este usuario", "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese un ID de usuario válido", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al consultar multas: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

        java.awt.EventQueue.invokeLater(() -> new frmMultas().setVisible(true));
    }

    private javax.swing.JButton btnConsultar;
    private javax.swing.JButton btnGenerarMulta;
    private javax.swing.JButton btnRegistrarPago;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tbtMultas;
    private javax.swing.JTextField txtIdUsuario;
    private javax.swing.JTextField txtIdPrestamo;
    private javax.swing.JTextField txtIdMulta;
}