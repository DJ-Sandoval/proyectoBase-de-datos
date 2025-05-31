package controller;
import static com.mysql.cj.conf.PropertyKey.logger;
import conection.Conexion;
import static conection.Conexion.getConnection;
import model.Prestamo;
import model.Libro;
import model.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.TipoUsuario;
/**
 *
 * @author jose
 */
public class PrestamoDAO {
    // Registrar préstamo
    public void registrarPrestamo(Prestamo prestamo) throws SQLException {
        String sql = "INSERT INTO Prestamos (id_usuario, ISBN, fecha_prestamo) VALUES (?, ?, ?)";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, prestamo.getIdUsuario());
            stmt.setString(2, prestamo.getISBN());
            stmt.setDate(3, new java.sql.Date(prestamo.getFechaPrestamo().getTime()));
            stmt.executeUpdate();
        }
    }

    // Registrar devolución
    public void registrarDevolucion(int idPrestamo) throws SQLException {
        String sql = "UPDATE Prestamos SET fecha_devolucion = ? WHERE id_prestamo = ?";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(new java.util.Date().getTime())); // Fecha actual
            stmt.setInt(2, idPrestamo);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No se encontró el préstamo con ID: " + idPrestamo);
            }
        }
    }

    // Consultar préstamos activos
    public List<Prestamo> getPrestamosActivos() throws SQLException {
        String sql = "SELECT p.id_prestamo, p.id_usuario, p.ISBN, p.fecha_prestamo, p.fecha_devolucion, " +
                "u.nombre AS nombre_usuario, u.apellido, l.titulo " +
                "FROM Prestamos p " +
                "JOIN Usuarios u ON p.id_usuario = u.id_usuario " +
                "JOIN Libros l ON p.ISBN = l.ISBN " +
                "WHERE p.fecha_devolucion IS NULL";

        List<Prestamo> prestamos = new ArrayList<>();

        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Prestamo prestamo = new Prestamo();
                prestamo.setIdPrestamo(rs.getInt("id_prestamo"));
                prestamo.setIdUsuario(rs.getInt("id_usuario"));
                prestamo.setISBN(rs.getString("ISBN"));
                prestamo.setFechaPrestamo(rs.getDate("fecha_prestamo"));
                prestamo.setFechaDevolucion(rs.getDate("fecha_devolucion"));
                prestamos.add(prestamo);
            }
        }
        return prestamos;
    }

    // Consultar historial de préstamos por usuario o libro
    public List<Prestamo> getHistorialPrestamos(String criterio, String valor) throws SQLException {
        // Validar criterio
        if (!criterio.equals("id_usuario") && !criterio.equals("ISBN")) {
            throw new SQLException("Criterio de búsqueda no válido. Use 'id_usuario' o 'ISBN'");
        }

        String sql = "SELECT p.id_prestamo, p.id_usuario, p.ISBN, p.fecha_prestamo, p.fecha_devolucion, " +
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