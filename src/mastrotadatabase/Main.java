package mastrotadatabase;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        
        // Usa try-with-resources para asegurar que la conexión se cierre automáticamente
        try (DatabaseConnector dbConnector = new DatabaseConnector()) {
            Connection conexion = dbConnector.getConnection();
            dbConnector.crearTabla();
            
            // Aquí puedes comenzar a interactuar con la base de datos
            // Por ejemplo, ejecutar consultas SQL, insertar datos, etc.
        } catch (SQLException ex) {
            System.err.println("Error al conectar con la base de datos: " + ex.getMessage());
            ex.printStackTrace();
        }
        
    }
}
