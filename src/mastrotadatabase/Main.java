package mastrotadatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnector implements AutoCloseable {
    private Connection connection;
    private String url = "jdbc:oracle:thin:@oracle0.ugr.es:1521/practbd.oracle0.ugr.es";
    private String usuario = "x7447650";
    private String contraseña = "x7447650";

    public DatabaseConnector() throws SQLException {
        try {
            // Asegúrate de que el driver JDBC de Oracle esté cargado
            Class.forName("oracle.jdbc.driver.OracleDriver");
            // Establece la conexión con la base de datos
            this.connection = DriverManager.getConnection(url, usuario, contraseña);
            System.out.println("Conexión exitosa a la base de datos.");
            
            // Crea las tablas
            crearTablas();
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

    public void crearTablas() {
        crearTabla("RawMaterial", "ID INT(6) PRIMARY KEY, Name_of_material VARCHAR2(20), length NUMBER, height NUMBER, width NUMBER, Weight NUMBER"); //
        crearTabla("Car", "Serial_number INT PRIMARY KEY, name_of_the_car VARCHAR2(100), power NUMBER, engine_displacement NUMBER");
        crearTabla("MakeCar", "ID_raw_material INT, Serial_number INT, FOREIGN KEY (ID_raw_material) REFERENCES RawMaterial(ID)");
        crearTabla("Empleado", "Nombre VARCHAR2(100), Direccion VARCHAR2(255), Fecha_de_contratacion DATE, Telefono VARCHAR2(20), Numero_de_empleado INT PRIMARY KEY, DNI VARCHAR2(20)");
        crearTabla("Tiene", "Telefono VARCHAR2(20), Numero_de_empleado INT, DNI VARCHAR2(20), Numero_de_contrato INT, FOREIGN KEY (Telefono, Numero_de_empleado, DNI) REFERENCES Empleado(Telefono, Numero_de_empleado, DNI)");
        crearTabla("Contrato", "Numero_de_contrato INT PRIMARY KEY, Fecha_de_inicio DATE, Fecha_de_fin DATE, Horario VARCHAR2(50), Sueldo NUMBER, Departamento VARCHAR2(50)");
        crearTabla("Gasto", "Descripcion VARCHAR2(255), Cantidad NUMBER, ID_gasto INT PRIMARY KEY, Categoria VARCHAR2(50)");
        crearTabla("Supplier", "Cif VARCHAR2(20) PRIMARY KEY, Denomination VARCHAR2(255), Address VARCHAR2(255), Type VARCHAR2(50)");
        crearTabla("Clients", "Cif VARCHAR2(20) PRIMARY KEY, Denomination VARCHAR2(255), Address VARCHAR2(255), Type VARCHAR2(50)");
        crearTabla("Order", "Cif VARCHAR2(20), Serial_number INT PRIMARY KEY, Price NUMBER, FOREIGN KEY (Cif) REFERENCES Clients(Cif)");
        crearTabla("Purchase", "Cif VARCHAR2(20), ID_raw_material INT, Price NUMBER, FOREIGN KEY (Cif) REFERENCES Supplier(Cif), FOREIGN KEY (ID_raw_material) REFERENCES RawMaterial(ID)");
    }

    private void crearTabla(String nombreTabla, String columnas) {
        String sqlCreate = "CREATE TABLE " + nombreTabla + " (" + columnas + ")";
        try (Statement stmt = this.connection.createStatement()) {
            stmt.execute(sqlCreate);
            System.out.println("Tabla " + nombreTabla + " creada exitosamente.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al crear la tabla " + nombreTabla + ": " + e.getMessage());
        }
    }
}

