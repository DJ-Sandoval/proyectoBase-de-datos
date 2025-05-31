package controller;
import model.Reserva;
import model.Usuario;
import model.Libro;
import conection.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReservaDAO {
    private static final Logger logger = Logger.getLogger(ReservaDAO.class.getName());

    // Create a new reservation
    public boolean crearReserva(Reserva reserva) {
        String sql = "INSERT INTO Reservas (id_usuario, ISBN, fecha_reserva) VALUES (?, ?, ?)";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reserva.getIdUsuario());
            pstmt.setString(2, reserva.getISBN());
            pstmt.setTimestamp(3, new java.sql.Timestamp(reserva.getFechaReserva().getTime()));
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al crear reserva", e);
            return false;
        }
    }

    // Cancel a reservation
    public boolean cancelarReserva(int idReserva) {
        String sql = "DELETE FROM Reservas WHERE id_reserva = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idReserva);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al cancelar reserva", e);
            return false;
        }
    }

    // Get all reservations
    public List<Reserva> consultarReservas() {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT r.id_reserva, r.id_usuario, r.ISBN, r.fecha_reserva, " +
                "u.nombre AS nombre_usuario, u.apellido AS apellido_usuario, l.titulo AS titulo_libro " +
                "FROM Reservas r " +
                "JOIN Usuarios u ON r.id_usuario = u.id_usuario " +
                "JOIN Libros l ON r.ISBN = l.ISBN";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Reserva reserva = new Reserva();
                reserva.setIdReserva(rs.getInt("id_reserva"));
                reserva.setIdUsuario(rs.getInt("id_usuario"));
                reserva.setISBN(rs.getString("ISBN"));
                reserva.setFechaReserva(rs.getTimestamp("fecha_reserva"));
                // Optionally, you can enhance the Reserva object to store additional display info
                reservas.add(reserva);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al consultar reservas", e);
        }
        return reservas;
    }

    // Get active reservations for a specific user
    public List<Reserva> consultarReservasActivas(int idUsuario) {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT r.id_reserva, r.id_usuario, r.ISBN, r.fecha_reserva, " +
                "u.nombre AS nombre_usuario, u.apellido AS apellido_usuario, l.titulo AS titulo_libro " +
                "FROM Reservas r " +
                "JOIN Usuarios u ON r.id_usuario = u.id_usuario " +
                "JOIN Libros l ON r.ISBN = l.ISBN " +
                "WHERE r.id_usuario = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUsuario);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Reserva reserva = new Reserva();
                    reserva.setIdReserva(rs.getInt("id_reserva"));
                    reserva.setIdUsuario(rs.getInt("id_usuario"));
                    reserva.setISBN(rs.getString("ISBN"));
                    reserva.setFechaReserva(rs.getTimestamp("fecha_reserva"));
                    reservas.add(reserva);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al consultar reservas activas", e);
        }
        return reservas;
    }

    // Check if a book is available (not currently borrowed)
    // Check if a book is available (not currently borrowed)
    public boolean isLibroDisponible(String ISBN) { // Cambiado a String ISBN
        String sql = "SELECT COUNT(*) FROM Prestamos WHERE ISBN = ? AND fecha_devolucion IS NULL";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ISBN); // Cambiado a setString
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0; // Book is available if count is 0
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al verificar disponibilidad del libro", e);
        }
        return false;
    }
}
