package controller;

import conection.Conexion;
import model.Multa;
import model.Prestamo;
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
            logger.info("Multa generada para id_prestamo: " + idPrestamo);
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
     * Retrieves pending fines for a specific user.
     */
    public List<Multa> consultarMultasPendientes(int idUsuario) {
        List<Multa> multas = new ArrayList<>();
        String sql = "SELECT m.* FROM Multas m JOIN Prestamos p ON m.id_prestamo = p.id_prestamo " +
                "WHERE p.id_usuario = ? AND m.estado = 'Pendiente'";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUsuario);
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
            logger.info("Consultadas multas pendientes para id_usuario: " + idUsuario);
        } catch (SQLException e) {
            logger.severe("Error al consultar multas pendientes: " + e.getMessage());
        }
        return multas;
    }

    /**
     * Checks if a loan is overdue and calculates the fine amount.
     */
    public double calcularMultaPorRetraso(int idPrestamo) {
        String sql = "SELECT fecha_devolucion FROM Prestamos WHERE id_prestamo = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idPrestamo);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Date fechaDevolucion = rs.getDate("fecha_devolucion");
                Date today = new Date();
                if (fechaDevolucion != null && today.after(fechaDevolucion)) {
                    long diffInMillies = today.getTime() - fechaDevolucion.getTime();
                    long diasRetraso = diffInMillies / (1000 * 60 * 60 * 24);
                    double montoPorDia = 1.0; // $1 per day of delay
                    return diasRetraso * montoPorDia;
                }
            }
        } catch (SQLException e) {
            logger.severe("Error al calcular multa por retraso: " + e.getMessage());
        }
        return 0.0;
    }

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
