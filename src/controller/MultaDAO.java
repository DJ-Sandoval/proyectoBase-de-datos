package controller;

import conection.Conexion;
import model.Multa;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class MultaDAO {
    private static final Logger logger = Logger.getLogger(MultaDAO.class.getName());

    /**
     * Generates a fine for overdue loans based on the loan ID and fine amount.
     */
    public boolean generarMulta(int idPrestamo, double monto) {
        String sql = "INSERT INTO Multas (id_prestamo, monto, fecha_emision, estado) VALUES (?, ?, ?, 'Pendiente')";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idPrestamo);
            pstmt.setDouble(2, monto);
            pstmt.setDate(3, new java.sql.Date(new Date().getTime()));
            int rowsAffected = pstmt.executeUpdate();
            logger.info("Multa generada para id_prestamo: " + idPrestamo + ", monto: " + monto);
            return rowsAffected > 0;
        } catch (SQLException e) {
            logger.severe("Error al generar multa: " + e.getMessage());
            return false;
        }
    }

    /**
     * Marks a fine as paid based on the fine ID.
     */
    public boolean registrarPago(int idMulta) {
        String sql = "UPDATE Multas SET estado = 'Pagada' WHERE id_multa = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idMulta);
            int rowsAffected = pstmt.executeUpdate();
            logger.info("Pago registrado para id_multa: " + idMulta);
            return rowsAffected > 0;
        } catch (SQLException e) {
            logger.severe("Error al registrar pago: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves pending fines for a specific user or all pending fines if idUsuario is 0.
     */
    public List<Multa> consultarMultasPendientes(int idUsuario) {
        List<Multa> multas = new ArrayList<>();
        String sql = idUsuario == 0 ?
                "SELECT * FROM Multas WHERE estado = 'Pendiente'" :
                "SELECT m.* FROM Multas m JOIN Prestamos p ON m.id_prestamo = p.id_prestamo " +
                        "WHERE p.id_usuario = ? AND m.estado = 'Pendiente'";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (idUsuario != 0) {
                pstmt.setInt(1, idUsuario);
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Multa multa = new Multa();
                multa.setIdMulta(rs.getInt("id_multa"));
                multa.setIdPrestamo(rs.getInt("id_prestamo"));
                multa.setMonto(rs.getDouble("monto"));
                multa.setFechaEmision(rs.getDate("fecha_emision"));
                multa.setEstado(rs.getString("estado"));
                multas.add(multa);
            }
            logger.info("Consultadas multas pendientes para id_usuario: " + (idUsuario == 0 ? "todos" : idUsuario));
        } catch (SQLException e) {
            logger.severe("Error al consultar multas pendientes: " + e.getMessage());
        }
        return multas;
    }

    /**
     * Checks if a loan is overdue and generates a fine for each overdue day.
     */
    public void checkAndGenerateFines() throws SQLException {
        String sql = "SELECT id_prestamo, fecha_limite FROM Prestamos WHERE fecha_devolucion IS NULL";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            java.util.Date today = new java.util.Date();
            while (rs.next()) {
                int idPrestamo = rs.getInt("id_prestamo");
                Date fechaLimite = rs.getDate("fecha_limite");
                if (fechaLimite != null && today.after(fechaLimite)) {
                    long diffInMillies = today.getTime() - fechaLimite.getTime();
                    long diasRetraso = diffInMillies / (1000 * 60 * 60 * 24) + 1; // Include the first overdue day
                    // Check existing fines for this loan
                    String checkFineSql = "SELECT COUNT(*) FROM Multas WHERE id_prestamo = ? AND estado = 'Pendiente'";
                    try (PreparedStatement checkStmt = conn.prepareStatement(checkFineSql)) {
                        checkStmt.setInt(1, idPrestamo);
                        ResultSet checkRs = checkStmt.executeQuery();
                        if (checkRs.next()) {
                            int existingFines = checkRs.getInt(1);
                            // Generate additional fines if needed
                            for (int i = existingFines + 1; i <= diasRetraso; i++) {
                                generarMulta(idPrestamo, 5.0); // 5 MXN per overdue day
                                logger.info("Multa generada automáticamente para préstamo ID: " + idPrestamo + ", día " + i);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Retrieves all fines.
     */
    public List<Multa> consultarTodasMultas() {
        List<Multa> multas = new ArrayList<>();
        String sql = "SELECT * FROM Multas";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Multa multa = new Multa();
                multa.setIdMulta(rs.getInt("id_multa"));
                multa.setIdPrestamo(rs.getInt("id_prestamo"));
                multa.setMonto(rs.getDouble("monto"));
                multa.setFechaEmision(rs.getDate("fecha_emision"));
                multa.setEstado(rs.getString("estado"));
                multas.add(multa);
            }
            logger.info("Consultadas todas las multas");
        } catch (SQLException e) {
            logger.severe("Error al consultar todas las multas: " + e.getMessage());
        }
        return multas;
    }
}
