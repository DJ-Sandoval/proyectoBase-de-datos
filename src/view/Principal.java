package view;

import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import conection.Conexion;

public class Principal extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Principal.class.getName());

    public Principal() {
        initComponents();
        this.setTitle("Biblioteca - Sistema de Gestión");
        this.setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        opcAcercade = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        opcSalir = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        opcUsuarios = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel7.setText("Hola bienvenido al sistema de biblioteca");


        jMenu1.setText("Archivo");

        opcAcercade.setText("AcercaDe");
        opcAcercade.addActionListener(evt -> opcAcercadeActionPerformed(evt));
        jMenu1.add(opcAcercade);

        jMenuItem2.setText("Dashboard");
        jMenuItem2.addActionListener(evt -> jMenuItem2ActionPerformed(evt));
        jMenu1.add(jMenuItem2);

        opcSalir.setText("Salir");
        opcSalir.addActionListener(evt -> opcSalirActionPerformed(evt));
        jMenu1.add(opcSalir);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Sistema");

        opcUsuarios.setText("Usuarios");
        opcUsuarios.addActionListener(evt -> {
            new frmUsuarios().setVisible(true);
        });
        jMenu2.add(opcUsuarios);

        jMenuItem1.setText("Libros");
        jMenuItem1.addActionListener(evt -> {
            new frmLibros().setVisible(true);
        });
        jMenu2.add(jMenuItem1);

        jMenuItem3.setText("Prestamos");
        jMenuItem3.addActionListener(evt -> {
            new frmPrestamos().setVisible(true);
        });
        jMenu2.add(jMenuItem3);

        jMenuItem4.setText("Reservas");
        jMenuItem4.addActionListener(evt -> {
            new frmReserva().setVisible(true);
        });
        jMenu2.add(jMenuItem4);

        jMenuItem8.setText("Multas");
        jMenuItem8.addActionListener(evt -> {
            new frmMultas().setVisible(true);
        });
        jMenu2.add(jMenuItem8);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Administracion");

        jMenuItem5.setText("Gestion de categorías");
        jMenuItem5.addActionListener(evt -> {
            new frmCategorias().setVisible(true);
        });
        jMenu3.add(jMenuItem5);

        jMenuItem6.setText("Gestion de editoriales");
        jMenuItem6.addActionListener(evt -> {
            new frmEditorial().setVisible(true);
        });
        jMenu3.add(jMenuItem6);

        jMenuItem7.setText("Gestion de tipos de usuario");
        jMenuItem7.addActionListener(evt -> {
            JOptionPane.showMessageDialog(this, "Funcionalidad no implementada.", "Información", JOptionPane.INFORMATION_MESSAGE);
        });
        jMenu3.add(jMenuItem7);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel8)
                                .addGap(145, 145, 145))
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(65, 65, 65)
                                                .addComponent(jLabel7))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(187, 187, 187)
                                                .addComponent(jLabel9)))
                                .addContainerGap(77, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(48, 48, 48)
                                .addComponent(jLabel7)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel9)
                                .addContainerGap(149, Short.MAX_VALUE))
        );

        pack();
    }

    private void opcAcercadeActionPerformed(java.awt.event.ActionEvent evt) {
        JOptionPane.showMessageDialog(
                null,
                "Proyecto: Biblioteca\n" +
                        "Materia: Fundamentos de bases de datos\n" +
                        "Alumnos: Jose Armando Sandoval Santana\n" +
                        "          Eduardo Beltran Solano\n" +
                        "NC: 22290963",
                "Acerca de",
                JOptionPane.PLAIN_MESSAGE
        );
    }

    private void opcSalirActionPerformed(java.awt.event.ActionEvent evt) {
        System.exit(0);
    }

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {
        StringBuilder message = new StringBuilder();
        message.append("=== Dashboard ===\n\n");

        String popularBooksQuery =
                "SELECT l.titulo, COUNT(p.ISBN) as total_prestamos " +
                        "FROM Prestamos p " +
                        "JOIN Libros l ON p.ISBN = l.ISBN " +
                        "GROUP BY p.ISBN, l.titulo " +
                        "ORDER BY total_prestamos DESC " +
                        "LIMIT 5";

        String overdueLoansQuery =
                "SELECT p.id_prestamo, u.nombre, u.apellido, l.titulo, p.fecha_devolucion " +
                        "FROM Prestamos p " +
                        "JOIN Usuarios u ON p.id_usuario = u.id_usuario " +
                        "JOIN Libros l ON p.ISBN = l.ISBN " +
                        "JOIN Multas m ON p.id_prestamo = m.id_prestamo " +
                        "WHERE p.fecha_devolucion < CURDATE() AND m.estado = 'Pendiente'";

        try (Connection conn = Conexion.getConnection()) {
            message.append("Libros más populares:\n");
            try (PreparedStatement stmt = conn.prepareStatement(popularBooksQuery);
                 ResultSet rs = stmt.executeQuery()) {
                int count = 1;
                while (rs.next()) {
                    String titulo = rs.getString("titulo");
                    int totalPrestamos = rs.getInt("total_prestamos");
                    message.append(String.format("%d. %s (%d préstamos)\n", count++, titulo, totalPrestamos));
                }
            }

            message.append("\nPréstamos atrasados:\n");
            try (PreparedStatement stmt = conn.prepareStatement(overdueLoansQuery);
                 ResultSet rs = stmt.executeQuery()) {
                int count = 1;
                while (rs.next()) {
                    int idPrestamo = rs.getInt("id_prestamo");
                    String nombre = rs.getString("nombre");
                    String apellido = rs.getString("apellido");
                    String titulo = rs.getString("titulo");
                    String fechaDevolucion = rs.getString("fecha_devolucion");
                    message.append(String.format("%d. Préstamo ID: %d, %s %s, Libro: %s, Fecha de devolución: %s\n",
                            count++, idPrestamo, nombre, apellido, titulo, fechaDevolucion));
                }
            }

            JOptionPane.showMessageDialog(null, message.toString(), "Dashboard", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            logger.log(java.util.logging.Level.SEVERE, "Error al consultar la base de datos", ex);
            JOptionPane.showMessageDialog(null, "Error al cargar los datos del dashboard.", "Error", JOptionPane.ERROR_MESSAGE);
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
        java.awt.EventQueue.invokeLater(() -> new Principal().setVisible(true));
    }

    // Variables declaration
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem opcAcercade;
    private javax.swing.JMenuItem opcSalir;
    private javax.swing.JMenuItem opcUsuarios;
}