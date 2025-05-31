package conection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Connection;
/**
 *
 * @author jose
 */
public class Conexion {
    private static final String URL = "jdbc:mysql://localhost:3306/bdbiblioteca";
    private static final String USER = "jose";
    private static final String PASSWORD = "12345";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
