package mastrotadatabase;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try (DatabaseConnector dbConnector = new DatabaseConnector()) {
            dbConnector.crearTablasSiNoExisten(); // Crear tablas si no existen
            dbConnector.poblarTablas();
            // ... resto de tu código ...
        } catch (SQLException ex) {
            System.err.println("Error al conectar con la base de datos: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
