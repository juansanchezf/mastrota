package mastrotadatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class DatabaseConnector implements AutoCloseable {
    private Connection connection;
    private String url = "jdbc:oracle:thin:@oracle0.ugr.es:1521/practbd.oracle0.ugr.es";
    private String usuario = "x7766471";
    private String contraseña = "x7766471";

    public DatabaseConnector() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            this.connection = DriverManager.getConnection(url, usuario, contraseña);
            System.out.println("Conexión exitosa a la base de datos.");
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("El driver JDBC de Oracle no se encontró en el classpath.", ex);
        } catch (SQLException ex) {
            System.err.println("No se pudo establecer la conexión con la base de datos.");
            //ex.printStackTrace();
            throw new RuntimeException("Error al establecer la conexión con la base de datos.", ex);
        }
    }

    public Connection getConnection() {
        return this.connection;
    }

    public java.util.Date parseFecha(String fechaStr) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        return formatter.parse(fechaStr);
    }

    private boolean existeTabla(Statement stmt, String nombreTabla) throws SQLException {
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM user_tables WHERE table_name = '" + nombreTabla + "'");
        rs.next();
        return rs.getInt(1) > 0;
    }

    /**
     * @brief Crea la tabla @nombreTabla
     * @param stmt
     * @param nombreTabla
     * @param sqlCreacion
     * @throws SQLException
     */
    private void crearTabla(Statement stmt, String nombreTabla, String sqlCreacion) throws SQLException {
        if (!existeTabla(stmt, nombreTabla)) {
            stmt.execute(sqlCreacion);
        }
    }

    /**
     * @brief Elimina la tabla definidida por @nombreTabla.
     * @param stmt
     * @param nombreTabla
     * @throws SQLException
     */
    private void eliminarTabla(Statement stmt, String nombreTabla) throws SQLException {
        if (existeTabla(stmt, nombreTabla))
            stmt.execute("DROP TABLE " + nombreTabla);
    }

    /**
     * @brief Crea todas las tablas de la base de datos
     */
    public void crearTablasSiNoExisten() {
        try (Statement stmt = connection.createStatement()) {
            crearTabla(stmt, "EMPLEADO",
                    "CREATE TABLE empleado(n_empleado NUMBER GENERATED ALWAYS AS IDENTITY, nombre VARCHAR(50), apellidos VARCHAR(60), DNI VARCHAR2(20) UNIQUE, telefono VARCHAR2(15), direccion VARCHAR2(255), PRIMARY KEY(n_empleado))");
            crearTabla(stmt, "CONTRATO",
                    "CREATE TABLE contrato(n_contrato NUMBER GENERATED ALWAYS AS IDENTITY, departamento VARCHAR2(255), fecha_inicio DATE, duracion INT, estado VARCHAR2(8) CHECK(estado IN('activo','inactivo')), n_empleado NUMBER, PRIMARY KEY(n_contrato), FOREIGN KEY(n_empleado) REFERENCES empleado(n_empleado) ON DELETE SET NULL)");
            crearTabla(stmt, "SUPPLIER",
                    "CREATE TABLE supplier(CIF_supp VARCHAR2(20), denomination VARCHAR2(255), address VARCHAR2(255), type VARCHAR2(255), PRIMARY KEY (CIF_supp))");
            crearTabla(stmt, "CLIENT",
                    "CREATE TABLE client(CIF_client VARCHAR2(20), denomination VARCHAR2(255), address VARCHAR2(255), type VARCHAR2(255), PRIMARY KEY(CIF_client))");
            crearTabla(stmt, "MATERIAL",
                    "CREATE TABLE material(material_id NUMBER GENERATED ALWAYS AS IDENTITY, name VARCHAR2(255), length NUMBER, height NUMBER, width NUMBER, weight NUMBER, PRIMARY KEY(material_id))");
            crearTabla(stmt, "PURCHASE",
                    "CREATE TABLE purchase(purchase_id NUMBER GENERATED ALWAYS AS IDENTITY, price NUMBER, quantity NUMBER, material_id NUMBER, CIF_supp VARCHAR2(20), PRIMARY KEY (purchase_id), FOREIGN KEY(material_id) REFERENCES material(material_id) ON DELETE SET NULL, FOREIGN KEY(CIF_supp) REFERENCES supplier(CIF_supp) ON DELETE SET NULL)");
            crearTabla(stmt, "GASTO",
                    "CREATE TABLE gasto(id_gasto NUMBER GENERATED ALWAYS AS IDENTITY, cantidad NUMBER, fecha DATE, categoria VARCHAR2(255), purchase_id NUMBER, n_contrato NUMBER, PRIMARY KEY(id_gasto), FOREIGN KEY (purchase_id) REFERENCES purchase(purchase_id) ON DELETE CASCADE, FOREIGN KEY (n_contrato) REFERENCES contrato(n_contrato) ON DELETE CASCADE)");
            crearTabla(stmt, "CAR",
                    "CREATE TABLE car(serial_n VARCHAR2(255), name VARCHAR2(255), power NUMBER, engine_disp NUMBER, PRIMARY KEY(serial_n))");
            crearTabla(stmt, "CAR_MATERIAL",
                    "CREATE TABLE car_material(serial_n VARCHAR2(255), material_id NUMBER, FOREIGN KEY (serial_n) REFERENCES car(serial_n) ON DELETE CASCADE, FOREIGN KEY (material_id) REFERENCES material(material_id) ON DELETE CASCADE, PRIMARY KEY(serial_n, material_id))");
            crearTabla(stmt, "ORDER_",
                    "CREATE TABLE order_(order_id NUMBER GENERATED ALWAYS AS IDENTITY, price NUMBER, serial_n VARCHAR2(255), CIF_client VARCHAR(20), PRIMARY KEY (order_id), FOREIGN KEY (serial_n) REFERENCES car(serial_n) ON DELETE CASCADE, FOREIGN KEY (CIF_client) REFERENCES client(CIF_client) ON DELETE CASCADE)");
            crearTabla(stmt, "INGRESO",
                    "CREATE TABLE ingreso(ingreso_id NUMBER GENERATED ALWAYS AS IDENTITY, cantidad NUMBER, fecha DATE, categoria VARCHAR2(255), order_id NUMBER, PRIMARY KEY(ingreso_id), FOREIGN KEY(order_id) REFERENCES order_(order_id) ON DELETE CASCADE)");

            System.out.println("Tablas creadas o verificadas exitosamente.");
        } catch (SQLException e) {
            System.err.println("Error al crear las tablas: " + e.getMessage());
        }
    }

    /**
     * @brief Elimina todas las tablas de la base de datos
     */
    public void eliminarTablasSiExisten() {
        try (Statement stmt = connection.createStatement()) {
            // Eliminar tablas
            eliminarTabla(stmt, "INGRESO");
            eliminarTabla(stmt, "ORDER_");
            eliminarTabla(stmt, "CAR_MATERIAL");
            eliminarTabla(stmt, "CAR");
            eliminarTabla(stmt, "GASTO");
            eliminarTabla(stmt, "PURCHASE");
            eliminarTabla(stmt, "MATERIAL");
            eliminarTabla(stmt, "CLIENT");
            eliminarTabla(stmt, "SUPPLIER");
            eliminarTabla(stmt, "CONTRATO");
            eliminarTabla(stmt, "EMPLEADO");

            System.out.println("Tablas eliminadas exitosamente.");
        } catch (SQLException e) {
            System.err.println("Error al eliminar las tablas: " + e.getMessage());
        }
    }

    // MÉTODOS PARA AÑADIR DATOS//
    
    /**
     * @brief Este método inserta un empleado concreto
     * @param nombre
     * @param apellidos
     * @param DNI (Debe ser único)
     * @param telefono
     * @param direccion
     */
    public void insertarEmpleado(String nombre, String apellidos, String DNI, String telefono, String direccion) {
    try {
        String sql = "INSERT INTO empleado (nombre, apellidos, DNI, telefono, direccion) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            pstmt.setString(2, apellidos);
            pstmt.setString(3, DNI);
            pstmt.setString(4, telefono);
            pstmt.setString(5, direccion);
            
            pstmt.executeUpdate();
        }
    } catch (SQLException e) {
        if (e.getErrorCode() == 1) { // El código de error específico para violaciones de la restricción UNIQUE
            System.err.println("El DNI(" + DNI + ") que estás intentando "
            + "insertar con "+ nombre + " " +apellidos + " ya existe."
            + "\nEmpleado no añadido.");
        } else {
            System.err.println("Error al insertar empleado: " + e.getMessage());
        }
    }
}

    public void insertarContrato(String departamento, String fechaInicio, int duracion, String estado, Integer nEmpleado) {
        try {
            // Validar estado
            if (!estado.equals("activo") && !estado.equals("inactivo")) {
                throw new SQLException("El estado debe ser 'activo' o 'inactivo'");
            }

            String sql = "INSERT INTO contrato (departamento, fecha_inicio, "
                    + "duracion, estado, n_empleado) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, departamento);
                
                java.util.Date fechaInicioUtil;
                try{
                    fechaInicioUtil = parseFecha(fechaInicio);
                }catch (ParseException e) {
                    throw new SQLException("Formato de fecha inválido", e);
                }
                java.sql.Date fechaInicioSql = new java.sql.Date(fechaInicioUtil.getTime());
                pstmt.setDate(2, fechaInicioSql);
                
                pstmt.setInt(3, duracion);
                pstmt.setString(4, estado);

                if (nEmpleado == null) {
                    pstmt.setNull(5, java.sql.Types.INTEGER); // Establecer n_empleado como NULL si es null
                } else {
                    pstmt.setInt(5, nEmpleado);
                }

                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar contrato: " + e.getMessage());
            // Dependiendo del caso, aquí podrías lanzar una excepción personalizada o manejar la situación de manera específica
        }
    }
    
    public void poblarEmpleado(){
        insertarEmpleado("Juan", "Sanchez Fernandez", "77766471K", "608050821", "Calle Arabial 36");
        insertarEmpleado("Ana", "Gómez Martínez", "12345678A", "600123456", "Avenida de la Constitución 1");
        insertarEmpleado("Carlos", "López García", "23456789B", "601234567", "Plaza de España 2");
        insertarEmpleado("Marta", "Ruiz López", "34567890C", "602345678", "Calle Gran Vía 3");
        insertarEmpleado("Pedro", "Díaz Fernández", "45678901D", "603456789", "Calle Mayor 4");
        insertarEmpleado("Lucía", "Santos González", "56789012E", "604567890", "Calle del Prado 5");
        insertarEmpleado("Javier", "Muñoz Ramírez", "59789012E", "605678901", "Paseo de la Castellana 6");
    }

    public void poblarContrato(){
        //insertarContrato("Logistica", fechaInicio, int duracion, String estado, Integer nEmpleado) 
        
    }
    public void poblarTablas() {
        poblarEmpleado();
        System.out.println("Tablas pobladas correctamente.");   
    }

    @Override
    public void close() throws SQLException {
        if (this.connection != null && !this.connection.isClosed()) {
            eliminarTablasSiExisten();
            this.connection.close();
            System.out.println("Conexión cerrada exitosamente.");
        }
    }
}
