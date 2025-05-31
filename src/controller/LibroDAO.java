package controller;
import conection.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Autor;
import model.Categoria;
import model.Editorial;
import model.Libro;
/**
 *
 * @author jose
 */
public class LibroDAO {
    // Agregar libro
    public void agregarLibro(Libro libro) throws SQLException {
        String sqlLibro = "INSERT INTO Libros (ISBN, titulo, id_editorial, ano_publicacion, id_categoria) VALUES (?, ?, ?, ?, ?)";
        String sqlLibroAutor = "INSERT INTO Libro_Autor (ISBN, id_autor) VALUES (?, ?)";

        try (Connection conn = Conexion.getConnection()) {
            conn.setAutoCommit(false);

            // Insertar libro
            try (PreparedStatement stmt = conn.prepareStatement(sqlLibro)) {
                stmt.setString(1, libro.getISBN());
                stmt.setString(2, libro.getTitulo());
                stmt.setInt(3, libro.getIdEditorial());
                stmt.setInt(4, libro.getAnioPublicacion());
                stmt.setInt(5, libro.getIdCategoria());
                stmt.executeUpdate();
            }

            // Insertar relaciones libro-autor
            try (PreparedStatement stmt = conn.prepareStatement(sqlLibroAutor)) {
                for (Autor autor : libro.getAutores()) {
                    stmt.setString(1, libro.getISBN());
                    stmt.setInt(2, autor.getIdAutor());
                    stmt.executeUpdate();
                }
            }

            conn.commit();
        } catch (SQLException e) {
            throw e;
        }
    }

    // Modificar libro
    public void modificarLibro(Libro libro) throws SQLException {
        String sqlLibro = "UPDATE Libros SET titulo = ?, id_editorial = ?, ano_publicacion = ?, id_categoria = ? WHERE ISBN = ?";
        String sqlDeleteAutores = "DELETE FROM Libro_Autor WHERE ISBN = ?";
        String sqlLibroAutor = "INSERT INTO Libro_Autor (ISBN, id_autor) VALUES (?, ?)";

        try (Connection conn = Conexion.getConnection()) {
            conn.setAutoCommit(false);

            // Actualizar libro
            try (PreparedStatement stmt = conn.prepareStatement(sqlLibro)) {
                stmt.setString(1, libro.getTitulo());
                stmt.setInt(2, libro.getIdEditorial());
                stmt.setInt(3, libro.getAnioPublicacion());
                stmt.setInt(4, libro.getIdCategoria());
                stmt.setString(5, libro.getISBN());
                stmt.executeUpdate();
            }

            // Eliminar autores existentes
            try (PreparedStatement stmt = conn.prepareStatement(sqlDeleteAutores)) {
                stmt.setString(1, libro.getISBN());
                stmt.executeUpdate();
            }

            // Insertar nuevos autores
            try (PreparedStatement stmt = conn.prepareStatement(sqlLibroAutor)) {
                for (Autor autor : libro.getAutores()) {
                    stmt.setString(1, libro.getISBN());
                    stmt.setInt(2, autor.getIdAutor());
                    stmt.executeUpdate();
                }
            }

            conn.commit();
        } catch (SQLException e) {
            throw e;
        }
    }

    // Buscar libros
    public List<Libro> buscarLibros(String criterio, String valor) throws SQLException {
        String sql = "SELECT l.*, c.nombre_categoria, e.nombre as nombre_editorial " +
                "FROM Libros l " +
                "JOIN Categorias c ON l.id_categoria = c.id_categoria " +
                "JOIN Editoriales e ON l.id_editorial = e.id_editorial " +
                "WHERE " + criterio + " LIKE ?";

        List<Libro> libros = new ArrayList<>();

        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + valor + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Libro libro = new Libro();
                    libro.setISBN(rs.getString("ISBN"));
                    libro.setTitulo(rs.getString("titulo"));
                    libro.setIdEditorial(rs.getInt("id_editorial"));
                    libro.setAnioPublicacion(rs.getInt("ano_publicacion"));
                    libro.setIdCategoria(rs.getInt("id_categoria"));

                    // Obtener autores
                    List<Autor> autores = getAutoresPorLibro(libro.getISBN());
                    libro.setAutores(autores);

                    libros.add(libro);
                }
            }
        }
        return libros;
    }

    // Obtener autores por libro
    private List<Autor> getAutoresPorLibro(String isbn) throws SQLException {
        String sql = "SELECT a.* FROM Autores a JOIN Libro_Autor la ON a.id_autor = la.id_autor WHERE la.ISBN = ?";
        List<Autor> autores = new ArrayList<>();

        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, isbn);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Autor autor = new Autor();
                    autor.setIdAutor(rs.getInt("id_autor"));
                    autor.setNombre(rs.getString("nombre"));
                    autor.setNacionalidad(rs.getString("nacionalidad"));
                    autores.add(autor);
                }
            }
        }
        return autores;
    }

    // Eliminar libro
    public boolean eliminarLibro(String isbn) throws SQLException {
        // Verificar préstamos activos
        String sqlPrestamos = "SELECT COUNT(*) FROM Prestamos WHERE ISBN = ? AND fecha_devolucion IS NULL";
        String sqlDeleteLibroAutor = "DELETE FROM Libro_Autor WHERE ISBN = ?";
        String sqlDeleteLibro = "DELETE FROM Libros WHERE ISBN = ?";

        try (Connection conn = Conexion.getConnection()) {
            conn.setAutoCommit(false);

            // Verificar préstamos
            try (PreparedStatement stmt = conn.prepareStatement(sqlPrestamos)) {
                stmt.setString(1, isbn);
                ResultSet rs = stmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    return false; // Hay préstamos activos
                }
            }

            // Eliminar relaciones libro-autor
            try (PreparedStatement stmt = conn.prepareStatement(sqlDeleteLibroAutor)) {
                stmt.setString(1, isbn);
                stmt.executeUpdate();
            }

            // Eliminar libro
            try (PreparedStatement stmt = conn.prepareStatement(sqlDeleteLibro)) {
                stmt.setString(1, isbn);
                stmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            throw e;
        }
    }

    public List<Editorial> getAllEditoriales() throws SQLException {
        List<Editorial> editoriales = new ArrayList<>();
        String sql = "SELECT * FROM Editoriales";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Editorial editorial = new Editorial();
                editorial.setIdEditorial(rs.getInt("id_editorial"));
                editorial.setNombre(rs.getString("nombre"));
                editoriales.add(editorial);
            }
        }
        return editoriales;
    }

    public List<Categoria> getAllCategorias() throws SQLException {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT * FROM Categorias";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Categoria categoria = new Categoria();
                categoria.setIdCategoria(rs.getInt("id_categoria"));
                categoria.setNombre(rs.getString("nombre_categoria"));
                categorias.add(categoria);
            }
        }
        return categorias;
    }



}
