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
            ex.printStackTrace();
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
    
    private boolean existeTabla(Statement stmt, String nombreTabla) throws SQLException{
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM user_tables WHERE table_name = '" + nombreTabla + "'");
        rs.next();
        if (rs.getInt(1) > 0) {
            return true;
        }
        else{
            return false;
        }
    }
    
    /**
     * @brief Crea una tabla concreta
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
     * @brief Elimina una tabla concreta.
     * @param stmt
     * @param nombreTabla
     * @throws SQLException 
     */
    private void eliminarTabla(Statement stmt, String nombreTabla) throws SQLException {
        if(existeTabla(stmt, nombreTabla))
            stmt.execute("DROP TABLE " + nombreTabla);
    }
    

    /**
     * @brief Crea todas las tablas de la base de datos
     */
    public void crearTablasSiNoExisten() {
    try (Statement stmt = connection.createStatement()) {
        crearTabla(stmt, "EMPLEADO", "CREATE TABLE empleado(n_empleado NUMBER GENERATED ALWAYS AS IDENTITY, nombre VARCHAR(50), apellidos VARCHAR(60), DNI VARCHAR2(20), telefono VARCHAR2(15), direccion VARCHAR2(255), fecha_contratacion DATE, departamento VARCHAR2(255), PRIMARY KEY(n_empleado))");
        crearTabla(stmt, "CONTRATO", "CREATE TABLE contrato(n_contrato NUMBER GENERATED ALWAYS AS IDENTITY, departamento VARCHAR2(255), fecha_inicio DATE, duracion INT, estado VARCHAR2(8) CHECK(estado IN('activo','inactivo')), n_empleado NUMBER, PRIMARY KEY(n_contrato), FOREIGN KEY(n_empleado) REFERENCES empleado(n_empleado) ON DELETE SET NULL)");
        crearTabla(stmt, "SUPPLIER", "CREATE TABLE supplier(CIF_supp VARCHAR2(20), denomination VARCHAR2(255), address VARCHAR2(255), type VARCHAR2(255), PRIMARY KEY (CIF_supp))");
        crearTabla(stmt, "CLIENT", "CREATE TABLE client(CIF_client VARCHAR2(20), denomination VARCHAR2(255), address VARCHAR2(255), type VARCHAR2(255), PRIMARY KEY(CIF_client))");
        crearTabla(stmt, "MATERIAL", "CREATE TABLE material(material_id NUMBER GENERATED ALWAYS AS IDENTITY, name VARCHAR2(255), length NUMBER, height NUMBER, width NUMBER, weight NUMBER, PRIMARY KEY(material_id))");
        crearTabla(stmt, "PURCHASE", "CREATE TABLE purchase(purchase_id NUMBER GENERATED ALWAYS AS IDENTITY, price NUMBER, quantity NUMBER, material_id NUMBER, CIF_supp VARCHAR2(20), PRIMARY KEY (purchase_id), FOREIGN KEY(material_id) REFERENCES material(material_id) ON DELETE SET NULL, FOREIGN KEY(CIF_supp) REFERENCES supplier(CIF_supp) ON DELETE SET NULL)");
        crearTabla(stmt, "GASTO", "CREATE TABLE gasto(id_gasto NUMBER GENERATED ALWAYS AS IDENTITY, cantidad NUMBER, fecha DATE, categoria VARCHAR2(255), purchase_id NUMBER, n_contrato NUMBER, PRIMARY KEY(id_gasto), FOREIGN KEY (purchase_id) REFERENCES purchase(purchase_id) ON DELETE CASCADE, FOREIGN KEY (n_contrato) REFERENCES contrato(n_contrato) ON DELETE CASCADE)");
        crearTabla(stmt, "CAR", "CREATE TABLE car(serial_n VARCHAR2(255), name VARCHAR2(255), power NUMBER, engine_disp NUMBER, PRIMARY KEY(serial_n))");
        crearTabla(stmt, "CAR_MATERIAL", "CREATE TABLE car_material(serial_n VARCHAR2(255), material_id NUMBER, FOREIGN KEY (serial_n) REFERENCES car(serial_n) ON DELETE CASCADE, FOREIGN KEY (material_id) REFERENCES material(material_id) ON DELETE CASCADE, PRIMARY KEY(serial_n, material_id))");
        crearTabla(stmt, "ORDER_", "CREATE TABLE order_(order_id NUMBER GENERATED ALWAYS AS IDENTITY, price NUMBER, serial_n VARCHAR2(255), CIF_client VARCHAR(20), PRIMARY KEY (order_id), FOREIGN KEY (serial_n) REFERENCES car(serial_n) ON DELETE CASCADE, FOREIGN KEY (CIF_client) REFERENCES client(CIF_client) ON DELETE CASCADE)");
        crearTabla(stmt, "INGRESO", "CREATE TABLE ingreso(ingreso_id NUMBER GENERATED ALWAYS AS IDENTITY, cantidad NUMBER, fecha DATE, categoria VARCHAR2(255), order_id NUMBER, PRIMARY KEY(ingreso_id), FOREIGN KEY(order_id) REFERENCES order_(order_id) ON DELETE CASCADE)");
        
        System.out.println("Tablas creadas o verificadas exitosamente.");
    } catch (SQLException e) {
        System.err.println("Error al crear las tablas: " + e.getMessage());
        e.printStackTrace();
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
            e.printStackTrace();
        }
    }
    
    // MÉTODOS PARA AÑADIR DATOS//
    /**
     * 
     * @param DNI
     * @param telefono
     * @param direccion
     * @param fechaContratacion
     * @param departamento
     * @throws SQLException 
     */
    public void insertarEmpleado(String nombre, String apellidos, String DNI, String telefono, String direccion, String fechaContratacion, String departamento) throws SQLException {
    String sql = "INSERT INTO empleado (DNI, telefono, direccion, fecha_contratacion, departamento) VALUES (?, ?, ?, ?, ?)";
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setString(1, nombre);
        pstmt.setString(2, apellidos);
        pstmt.setString(3, DNI);
        pstmt.setString(4, telefono);
        pstmt.setString(5, direccion);
        
        java.util.Date fechaContratacionUtil;
        try {
            fechaContratacionUtil = parseFecha(fechaContratacion);
        } catch (ParseException e) {
            throw new SQLException("Formato de fecha inválido", e);
        }
        java.sql.Date fechaContratacionSql = new java.sql.Date(fechaContratacionUtil.getTime());
        pstmt.setDate(6, fechaContratacionSql);
        
        pstmt.setString(7, departamento);
        pstmt.executeUpdate();
    }
}

    public void poblarTablas(){
        try {
            insertarEmpleado("Juan", "Sanchez Fernandez", "77766471K", "608050821", "Calle Arabial 36", "04-08-2002", "Finanzas");
        } catch (SQLException e) {
            System.err.println("Error al insertar empleado: " + e.getMessage());
            e.printStackTrace();
        }
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
