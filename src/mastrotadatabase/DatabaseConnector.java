package mastrotadatabase;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author juan
 */

public class DatabaseConnector implements AutoCloseable{
    private Connection connection;
    private String url = "jdbc:oracle:thin:@oracle0.ugr.es:1521/practbd.oracle0.ugr.es";
    private String usuario = "x7766471";
    private String contraseña = "x7766471";

    public DatabaseConnector() throws SQLException {
        try {
            // Asegúrate de que el driver JDBC de Oracle esté cargado
            Class.forName("oracle.jdbc.driver.OracleDriver");
            // Establece la conexión con la base de datos
            this.connection = DriverManager.getConnection(url, usuario, contraseña);
            System.out.println("Conexión exitosa a la base de datos.");
        } catch (ClassNotFoundException ex) {
            System.err.println("El driver JDBC de Oracle no se encontró en el classpath.");
            ex.printStackTrace();
        } catch (SQLException ex) {
            System.err.println("No se pudo establecer la conexión con la base de datos.");
            ex.printStackTrace();
            throw ex; // Re-lanza la excepción para manejarla más arriba si es necesario
        }
    }

    public Connection getConnection() {
        return this.connection;
    }

     // Implementa el método close de la interfaz AutoCloseable
    @Override
    public void close() throws SQLException {
        if (this.connection != null && !this.connection.isClosed()) {
            this.connection.close();
            System.out.println("Conexión cerrada exitosamente.");
        }
    }
    
    public void crearTabla() {
    String sqlCreate = "CREATE TABLE ejemplo (" +
                       "id INT PRIMARY KEY, " +
                       "nombre VARCHAR2(100), " +
                       "descripcion VARCHAR2(255))";
    try (Statement stmt = this.connection.createStatement()) {
        stmt.execute(sqlCreate);
        System.out.println("Tabla creada exitosamente.");
    } catch (SQLException e) {
        e.printStackTrace();
        System.err.println("Error al crear la tabla: " + e.getMessage());
    }
    }
}

