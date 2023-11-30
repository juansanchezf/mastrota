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
        crearTabla("RawMaterial", "ID INT(6) PRIMARY KEY, Name_of_material VARCHAR2(20), length NUMBER, height NUMBER, width NUMBER, Weight NUMBER"); 
        crearTabla("Car", "Serial_number INT(6) PRIMARY KEY, name_of_the_car VARCHAR2(20), power INT(3), engine_displacement INT(4)"); 
        crearTabla("MakeCar", "ID_raw_material INT(6), Serial_number INT(6), PRIMARY KEY(ID_raw_material, Serial_number), FOREIGN KEY (ID_raw_material) REFERENCES RawMaterial(ID), FOREIGN KEY (Serial_number) REFERENCES Car(Serial_number)"); 
        crearTabla("Empleado", "Telefono VARCHAR2(15) PRIMARY KEY, Numero_de_empleado INT(5) PRIMARY KEY, DNI VARCHAR2(20) PRIMARY KEY, Nombre VARCHAR2(100), Direccion VARCHAR2(255), Fecha_de_contratacion DATE"); 
        crearTabla("Contrato", "Numero_de_contrato INT(6) PRIMARY KEY, Fecha_de_inicio DATE, Fecha_de_fin DATE, Horario VARCHAR2(100), Sueldo NUMBER, Departamento VARCHAR2(50)");
        crearTabla("Tiene", "Telefono VARCHAR2(15), Numero_de_empleado INT(5), DNI VARCHAR2(20), Numero_de_contrato INT(6), PRIMARY KEY(Numero_de_contrato, Numero_de_empleado), PRIMARY KEY(Numero_de_contrato, DNI), PRIMARY KEY(Numero_de_contrato, Telefono), FOREIGN KEY (Telefono, Numero_de_empleado, DNI) REFERENCES Empleado(Telefono, Numero_de_empleado, DNI), FOREIGN KEY (Numero_de_contrato) REFERENCES Contrato(Numero_de_contrato) ");
        crearTabla("Gasto", "Descripcion TEXT, Cantidad NUMBER, ID_gasto INT(6) PRIMARY KEY, Categoria VARCHAR2(20)");//
        crearTabla("Supplier", "Cif VARCHAR2(10) PRIMARY KEY, Denomination VARCHAR2(20), Address VARCHAR2(50)");//
        crearTabla("Clients", "Cif VARCHAR2(10) PRIMARY KEY, Denomination VARCHAR2(20), Address VARCHAR2(50), Type VARCHAR2(10)");//
        crearTabla("Order", "Cif VARCHAR2(10), Serial_number INT(10), Price NUMBER, FOREIGN KEY (Cif) REFERENCES Clients(Cif), FOREIGN KEY (Serial_number) REFERENCES Car(Serial_number) "); //
        crearTabla("Purchase", "Cif VARCHAR2(10), ID_raw_material INT(6), Price NUMBER, FOREIGN KEY (Cif) REFERENCES Supplier(Cif), FOREIGN KEY (ID_raw_material) REFERENCES RawMaterial(ID)"); //
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

