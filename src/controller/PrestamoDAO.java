package controller;
import conection.Conexion;
import model.Prestamo;
import model.Libro;
import model.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.TipoUsuario;

import javax.swing.*;
import java.util.logging.Logger;

public class PrestamoDAO {
    private static final Logger logger = Logger.getLogger(PrestamoDAO.class.getName());
    private final MultaDAO multaDAO = new MultaDAO();

    // Registrar préstamo
    public void registrarPrestamo(Prestamo prestamo) throws SQLException {
        // Verificar cantidad de préstamos activos del usuario
        String checkCountSql = "SELECT COUNT(*) FROM Prestamos WHERE id_usuario = ? AND fecha_devolucion IS NULL";
        String checkBookSql = "SELECT COUNT(*) FROM Prestamos WHERE id_usuario = ? AND ISBN = ? AND fecha_devolucion IS NULL";
        String checkBookTotalLoansSql = "SELECT COUNT(*) FROM Prestamos WHERE ISBN = ? AND fecha_devolucion IS NULL";

        try (Connection conn = Conexion.getConnection()) {
            // Verificar si el usuario ya tiene 3 préstamos activos
            try (PreparedStatement countStmt = conn.prepareStatement(checkCountSql)) {
                countStmt.setInt(1, prestamo.getIdUsuario());
                try (ResultSet rs = countStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) >= 3) {
                        throw new SQLException("El usuario ya tiene 3 libros en préstamo.");
                    }
                }
            }

            // Verificar si el libro ya está prestado por el usuario
            try (PreparedStatement bookStmt = conn.prepareStatement(checkBookSql)) {
                bookStmt.setInt(1, prestamo.getIdUsuario());
                bookStmt.setString(2, prestamo.getISBN());
                try (ResultSet rs = bookStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        throw new SQLException("El usuario ya tiene este libro en préstamo.");
                    }
                }
            }

            // Verificar si el libro ya tiene 2 préstamos activos en total
            try (PreparedStatement bookTotalStmt = conn.prepareStatement(checkBookTotalLoansSql)) {
                bookTotalStmt.setString(1, prestamo.getISBN());
                try (ResultSet rs = bookTotalStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) >= 2) {
                        throw new SQLException("El libro ya ha sido prestado 2 veces y no está disponible.");
                    }
                }
            }

            // Proceder con el registro del préstamo
            String sql = "INSERT INTO Prestamos (id_usuario, ISBN, fecha_prestamo, fecha_limite) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, prestamo.getIdUsuario());
                stmt.setString(2, prestamo.getISBN());
                stmt.setDate(3, new java.sql.Date(prestamo.getFechaPrestamo().getTime()));
                java.util.Date fechaLimite = new java.util.Date(prestamo.getFechaPrestamo().getTime() + (3L * 24 * 60 * 60 * 1000));
                stmt.setDate(4, new java.sql.Date(fechaLimite.getTime()));
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Préstamo registrado para usuario: " + prestamo.getIdUsuario() + ", ISBN: " + prestamo.getISBN());
            }
        }
    }

    // Registrar devolución
    public void registrarDevolucion(int idPrestamo) throws SQLException {
        String sqlUpdate = "UPDATE Prestamos SET fecha_devolucion = ? WHERE id_prestamo = ?";
        String sqlDelete = "DELETE FROM Prestamos WHERE id_prestamo = ? AND fecha_devolucion IS NOT NULL";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate);
             PreparedStatement stmtDelete = conn.prepareStatement(sqlDelete)) {
            // Actualizar la fecha de devolución
            stmtUpdate.setDate(1, new java.sql.Date(new java.util.Date().getTime()));
            stmtUpdate.setInt(2, idPrestamo);
            int rowsAffected = stmtUpdate.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No se encontró el préstamo con ID: " + idPrestamo);
            }

            // Eliminar el registro si ya tiene fecha de devolución
            stmtDelete.setInt(1, idPrestamo);
            stmtDelete.executeUpdate();
            JOptionPane.showMessageDialog(null, "Devolución y registro eliminados para préstamo ID: " + idPrestamo, "Exito", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Check for overdue loans and generate fines
    public void checkAndGenerateFines() throws SQLException {
        multaDAO.checkAndGenerateFines();
    }

    // Consultar préstamos activos
    public List<Prestamo> getPrestamosActivos() throws SQLException {
        String sql = "SELECT p.id_prestamo, p.id_usuario, p.ISBN, p.fecha_prestamo, p.fecha_devolucion, p.fecha_limite, " +
                "u.nombre AS nombre_usuario, u.apellido, l.titulo " +
                "FROM Prestamos p " +
                "JOIN Usuarios u ON p.id_usuario = u.id_usuario " +
                "JOIN Libros l ON p.ISBN = l.ISBN " +
                "WHERE p.fecha_devolucion IS NULL AND p.fecha_limite >= ?";

        List<Prestamo> prestamos = new ArrayList<>();
        java.util.Date today = new java.util.Date();

        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(today.getTime()));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Prestamo prestamo = new Prestamo();
                    prestamo.setIdPrestamo(rs.getInt("id_prestamo"));
                    prestamo.setIdUsuario(rs.getInt("id_usuario"));
                    prestamo.setISBN(rs.getString("ISBN"));
                    prestamo.setFechaPrestamo(rs.getDate("fecha_prestamo"));
                    prestamo.setFechaDevolucion(rs.getDate("fecha_devolucion"));
                    prestamo.setFechaLimite(rs.getDate("fecha_limite"));
                    prestamos.add(prestamo);
                }
            }
        }
        return prestamos;
    }

    // Consultar préstamos vencidos
    public List<Prestamo> getPrestamosVencidos() throws SQLException {
        String sql = "SELECT p.id_prestamo, p.id_usuario, p.ISBN, p.fecha_prestamo, p.fecha_devolucion, p.fecha_limite, " +
                "u.nombre AS nombre_usuario, u.apellido, l.titulo " +
                "FROM Prestamos p " +
                "JOIN Usuarios u ON p.id_usuario = u.id_usuario " +
                "JOIN Libros l ON p.ISBN = l.ISBN " +
                "WHERE p.fecha_devolucion IS NULL AND p.fecha_limite < ?";

        List<Prestamo> prestamos = new ArrayList<>();
        java.util.Date today = new java.util.Date();

        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(today.getTime()));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Prestamo prestamo = new Prestamo();
                    prestamo.setIdPrestamo(rs.getInt("id_prestamo"));
                    prestamo.setIdUsuario(rs.getInt("id_usuario"));
                    prestamo.setISBN(rs.getString("ISBN"));
                    prestamo.setFechaPrestamo(rs.getDate("fecha_prestamo"));
                    prestamo.setFechaDevolucion(rs.getDate("fecha_devolucion"));
                    prestamo.setFechaLimite(rs.getDate("fecha_limite"));
                    prestamos.add(prestamo);
                }
            }
        }
        return prestamos;
    }

    // Consultar historial de préstamos por usuario o libro
    public List<Prestamo> getHistorialPrestamos(String criterio, String valor) throws SQLException {
        if (!criterio.equals("id_usuario") && !criterio.equals("ISBN")) {
            throw new SQLException("Criterio de búsqueda no válido. Use 'id_usuario' o 'ISBN'");
        }

        String sql = "SELECT p.id_prestamo, p.id_usuario, p.ISBN, p.fecha_prestamo, p.fecha_devolucion, p.fecha_limite, " +
                "u.nombre AS nombre_usuario, u.apellido, l.titulo " +
                "FROM Prestamos p " +
                "JOIN Usuarios u ON p.id_usuario = u.id_usuario " +
                "JOIN Libros l ON p.ISBN = l.ISBN " +
                "WHERE p." + criterio + " LIKE ?";

        List<Prestamo> prestamos = new ArrayList<>();

        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + valor + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Prestamo prestamo = new Prestamo();
                    prestamo.setIdPrestamo(rs.getInt("id_prestamo"));
                    prestamo.setIdUsuario(rs.getInt("id_usuario"));
                    prestamo.setISBN(rs.getString("ISBN"));
                    prestamo.setFechaPrestamo(rs.getDate("fecha_prestamo"));
                    prestamo.setFechaDevolucion(rs.getDate("fecha_devolucion"));
                    prestamo.setFechaLimite(rs.getDate("fecha_limite"));
                    prestamos.add(prestamo);
                }
            }
        }
        return prestamos;
    }

    // Obtener todos los usuarios
    public List<Usuario> getAllUsuarios() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String query = "SELECT u.id_usuario, u.nombre, u.apellido, u.id_tipo_usuario, u.correo, t.descripcion " +
                "FROM Usuarios u JOIN Tipos_Usuario t ON u.id_tipo_usuario = t.id_tipo_usuario";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                TipoUsuario tipoUsuario = new TipoUsuario(rs.getInt("id_tipo_usuario"), rs.getString("descripcion"));
                Usuario usuario = new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        tipoUsuario,
                        rs.getString("correo")
                );
                usuarios.add(usuario);
            }
        }
        return usuarios;
    }

    // Obtener todos los libros
    public List<Libro> getAllLibros() throws SQLException {
        String sql = "SELECT * FROM Libros";
        List<Libro> libros = new ArrayList<>();

        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Libro libro = new Libro();
                libro.setISBN(rs.getString("ISBN"));
                libro.setTitulo(rs.getString("titulo"));
                libro.setIdEditorial(rs.getInt("id_editorial"));
                libro.setAnioPublicacion(rs.getInt("ano_publicacion"));
                libro.setIdCategoria(rs.getInt("id_categoria"));
                libros.add(libro);
            }
        }
        return libros;
    }
}