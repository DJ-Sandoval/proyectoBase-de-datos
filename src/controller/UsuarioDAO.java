package controller;
import conection.Conexion;
import model.TipoUsuario;
import model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UsuarioDAO {
    private static final Logger LOGGER = Logger.getLogger(UsuarioDAO.class.getName());

    // Registrar un nuevo usuario
    public boolean registrarUsuario(Usuario usuario) {
        String sql = "INSERT INTO Usuarios (nombre, apellido, id_tipo_usuario, correo) VALUES (?, ?, ?, ?)";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getApellido());
            stmt.setInt(3, usuario.getTipoUsuario().getIdTipoUsuario());
            stmt.setString(4, usuario.getCorreo());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al registrar usuario", e);
            return false;
        }
    }

    // Actualizar un usuario existente
    public boolean actualizarUsuario(Usuario usuario) {
        String sql = "UPDATE Usuarios SET nombre = ?, apellido = ?, id_tipo_usuario = ?, correo = ? WHERE id_usuario = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getApellido());
            stmt.setInt(3, usuario.getTipoUsuario().getIdTipoUsuario());
            stmt.setString(4, usuario.getCorreo());
            stmt.setInt(5, usuario.getIdUsuario());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al actualizar usuario", e);
            return false;
        }
    }

    // Consultar usuarios por nombre, apellido o tipo
    public List<Usuario> consultarUsuarios(String criterio, String valor) {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT u.id_usuario, u.nombre, u.apellido, u.id_tipo_usuario, u.correo, t.descripcion " +
                "FROM Usuarios u JOIN Tipos_Usuario t ON u.id_tipo_usuario = t.id_tipo_usuario " +
                "WHERE " + criterio + " LIKE ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + valor + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                TipoUsuario tipo = new TipoUsuario(rs.getInt("id_tipo_usuario"), rs.getString("descripcion"));
                Usuario usuario = new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        tipo,
                        rs.getString("correo")
                );
                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al consultar usuarios", e);
        }
        return usuarios;
    }

    // Eliminar usuario con validación de préstamos o reservas activas
    public boolean eliminarUsuario(int idUsuario) {
        String sqlPrestamos = "SELECT COUNT(*) FROM Prestamos WHERE id_usuario = ?";
        String sqlReservas = "SELECT COUNT(*) FROM Reservas WHERE id_usuario = ?";

        try (Connection conn = Conexion.getConnection()) {
            // Verificar préstamos
            PreparedStatement stmtPrestamos = conn.prepareStatement(sqlPrestamos);
            stmtPrestamos.setInt(1, idUsuario);
            ResultSet rsPrestamos = stmtPrestamos.executeQuery();
            rsPrestamos.next();
            if (rsPrestamos.getInt(1) > 0) {
                return false; // No eliminar si hay préstamos
            }

            // Verificar reservas
            PreparedStatement stmtReservas = conn.prepareStatement(sqlReservas);
            stmtReservas.setInt(1, idUsuario);
            ResultSet rsReservas = stmtReservas.executeQuery();
            rsReservas.next();
            if (rsReservas.getInt(1) > 0) {
                return false; // No eliminar si hay reservas
            }

            // Eliminar usuario
            String sqlEliminar = "DELETE FROM Usuarios WHERE id_usuario = ?";
            PreparedStatement stmtEliminar = conn.prepareStatement(sqlEliminar);
            stmtEliminar.setInt(1, idUsuario);
            return stmtEliminar.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar usuario: " + e.getMessage(), e);
            return false;
        }
    }

    // Obtener usuario por ID
    public Usuario obtenerUsuarioPorId(int idUsuario) {
        String sql = "SELECT u.id_usuario, u.nombre, u.apellido, u.id_tipo_usuario, u.correo, t.descripcion " +
                "FROM Usuarios u JOIN Tipos_Usuario t ON u.id_tipo_usuario = t.id_tipo_usuario " +
                "WHERE u.id_usuario = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                TipoUsuario tipo = new TipoUsuario(rs.getInt("id_tipo_usuario"), rs.getString("descripcion"));
                return new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        tipo,
                        rs.getString("correo")
                );
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener usuario por ID", e);
        }
        return null;
    }
}
