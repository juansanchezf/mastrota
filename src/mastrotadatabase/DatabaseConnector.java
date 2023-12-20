package mastrotadatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

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
            crearTabla(stmt, "EMPLEADO",    "CREATE TABLE empleado(n_empleado NUMBER GENERATED ALWAYS AS IDENTITY, nombre VARCHAR(50), apellidos VARCHAR(60), DNI VARCHAR2(20) UNIQUE, telefono VARCHAR2(15), direccion VARCHAR2(255), PRIMARY KEY(n_empleado))");
            crearTabla(stmt, "CONTRATO",    "CREATE TABLE contrato(n_contrato NUMBER GENERATED ALWAYS AS IDENTITY, departamento VARCHAR2(255), fecha_inicio DATE, duracion INT, estado VARCHAR2(8) CHECK(estado IN('activo','inactivo')), n_empleado NUMBER, PRIMARY KEY(n_contrato), FOREIGN KEY(n_empleado) REFERENCES empleado(n_empleado) ON DELETE SET NULL)");
            crearTabla(stmt, "SUPPLIER",    "CREATE TABLE supplier(CIF_supp VARCHAR2(20), denomination VARCHAR2(255), address VARCHAR2(255), type VARCHAR2(255), PRIMARY KEY (CIF_supp))");
            crearTabla(stmt, "CLIENT",      "CREATE TABLE client(CIF_client VARCHAR2(20), denomination VARCHAR2(255), address VARCHAR2(255), type VARCHAR2(255), PRIMARY KEY(CIF_client))");
            crearTabla(stmt, "MATERIAL",    "CREATE TABLE material(material_id NUMBER GENERATED ALWAYS AS IDENTITY, name VARCHAR2(255), length NUMBER, height NUMBER, width NUMBER, weight NUMBER, PRIMARY KEY(material_id))");
            crearTabla(stmt, "PURCHASE",    "CREATE TABLE purchase(purchase_id NUMBER GENERATED ALWAYS AS IDENTITY, price NUMBER, quantity NUMBER, material_id NUMBER, CIF_supp VARCHAR2(20), PRIMARY KEY (purchase_id), FOREIGN KEY(material_id) REFERENCES material(material_id) ON DELETE SET NULL, FOREIGN KEY(CIF_supp) REFERENCES supplier(CIF_supp) ON DELETE SET NULL)");
            crearTabla(stmt, "GASTO",       "CREATE TABLE gasto(id_gasto NUMBER GENERATED ALWAYS AS IDENTITY, cantidad NUMBER, fecha DATE, categoria VARCHAR2(255), purchase_id NUMBER, n_contrato NUMBER, PRIMARY KEY(id_gasto), FOREIGN KEY (purchase_id) REFERENCES purchase(purchase_id) ON DELETE CASCADE, FOREIGN KEY (n_contrato) REFERENCES contrato(n_contrato) ON DELETE CASCADE)");
            crearTabla(stmt, "CAR",         "CREATE TABLE car(serial_n VARCHAR2(255), name VARCHAR2(255), power NUMBER, engine_disp NUMBER, PRIMARY KEY(serial_n))");
            crearTabla(stmt, "CAR_MATERIAL","CREATE TABLE car_material(serial_n VARCHAR2(255), material_id NUMBER, FOREIGN KEY (serial_n) REFERENCES car(serial_n) ON DELETE CASCADE, FOREIGN KEY (material_id) REFERENCES material(material_id) ON DELETE CASCADE, PRIMARY KEY(serial_n, material_id))");
            crearTabla(stmt, "ORDER_",      "CREATE TABLE order_(order_id NUMBER GENERATED ALWAYS AS IDENTITY, price NUMBER, serial_n VARCHAR2(255), CIF_client VARCHAR(20), PRIMARY KEY (order_id), FOREIGN KEY (serial_n) REFERENCES car(serial_n) ON DELETE CASCADE, FOREIGN KEY (CIF_client) REFERENCES client(CIF_client) ON DELETE CASCADE)");
            crearTabla(stmt, "INGRESO",     "CREATE TABLE ingreso(ingreso_id NUMBER GENERATED ALWAYS AS IDENTITY, cantidad NUMBER, fecha DATE, categoria VARCHAR2(255), order_id NUMBER, PRIMARY KEY(ingreso_id), FOREIGN KEY(order_id) REFERENCES order_(order_id) ON DELETE CASCADE)");

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

    /**
     * @brief Añade un contrato
     * @param departamento
     * @param fechaInicio
     * @param duracion
     * @param estado
     * @param nEmpleado 
     */
    public void insertarContrato(String departamento, String fechaInicio, double duracion, String estado, Integer nEmpleado) {
        try {
            // Validar estado
            if (!estado.equals("activo") && !estado.equals("inactivo")) {
                throw new SQLException("El estado debe ser 'activo' o 'inactivo'");
            }

            String sql = "INSERT INTO contrato (departamento, fecha_inicio, "
                    + "duracion, estado, n_empleado) VALUES (?, TO_DATE(?, 'DD-MM-YYYY'), ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, departamento);
                pstmt.setString(2, fechaInicio);
                pstmt.setDouble(3, duracion);
                pstmt.setString(4, estado);

                if (nEmpleado == null) {
                    pstmt.setNull(5, java.sql.Types.INTEGER);
                } else {
                    pstmt.setInt(5, nEmpleado);
                }

                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            // Comprobar si el error es debido a una violación de la clave foránea
            // El código de error específico depende del SGBD; por ejemplo, en Oracle, podría ser ORA-02291
            switch (e.getErrorCode()) {
                case 2291 -> // Reemplaza 2291 con el código de error específico de tu SGBD
                    System.err.println("Error: El número de empleado " + nEmpleado + " no existe.");
                case 1 -> System.err.println("Error: El estado debe ser 'activo' o 'inactivo'");
                default -> System.err.println("Error al insertar contrato: " + e.getMessage());
            }
        }
    }
    
    /**
     * @brief Añade un proveedor
     * @param CIF
     * @param denomination
     * @param address
     * @param type 
     */
    public void insertarSupplier(String CIF, String denomination, String address, String type){
        try {
            String sql = "INSERT INTO supplier (CIF_supp, denomination, "
                    + "address, type) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, CIF);
                pstmt.setString(2, denomination);
                pstmt.setString(3, address);
                pstmt.setString(4, type);
                
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            if(e.getErrorCode()==1){
                System.err.println("El CIF " + CIF + " ya estaba introducido previamente.");
            }
            else{
                System.err.println("Error al insertar supplier: " + e.getMessage());
            }
            
        }
    }
    
    /**
     * @brief Añade un cliente
     * @param CIF_client
     * @param denomination
     * @param address
     * @param type 
     */
    public void insertarClient(String CIF_client, String denomination, String address, String type){
        try{
            String sql = "INSERT INTO client (CIF_client, denomination, "
                    + "address, type) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, CIF_client);
                pstmt.setString(2, denomination);
                pstmt.setString(3, address);
                pstmt.setString(4, type);
                
                pstmt.executeUpdate();
            }
        }catch (SQLException e){
            
        }
    }
    
    /**
     * @brief Añade un material 
     * @param name
     * @param length
     * @param height
     * @param width
     * @param weight 
     */
    public void insertarMaterial(String name, double length, double height, double width, double weight){
        try {
            String sql = "INSERT INTO material (name, length, height, width, weight) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setDouble(2, length);
                pstmt.setDouble(3, height);
                pstmt.setDouble(4, width);
                pstmt.setDouble(5, weight);

                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar material: " + e.getMessage());
        }
    }
    
    /**
     * @brief Añade compras
     * @param price
     * @param quantity
     * @param material_id
     * @param CIF_supp 
     */
    public void insertarPurchase(double price, Integer quantity, Integer material_id, String CIF_supp) {
        try {
            String sql = "INSERT INTO purchase (price, quantity, material_id, CIF_supp) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setDouble(1, price);
                pstmt.setDouble(2, quantity);

                if (material_id == null) {
                    pstmt.setNull(3, java.sql.Types.INTEGER);
                } else {
                    pstmt.setInt(3, material_id);
                }

                pstmt.setString(4, CIF_supp);

                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 2291) { // Asumiendo que 2291 es el código de error para una violación de clave foránea
                System.err.println("Error: material_id o CIF_supp no existe.");
            } else {
                System.err.println("Error al insertar compra: " + e.getMessage());
            }
        }
    }

    /**
     * @brief Añade gasto
     * @param cantidad
     * @param fecha
     * @param categoria
     * @param purchase_id
     * @param n_contrato 
     */
    public void insertarGasto(double cantidad, String fecha, String categoria, Integer purchase_id, Integer n_contrato) {
        try {
            String sql = "INSERT INTO gasto (cantidad, fecha, categoria, purchase_id, n_contrato) VALUES (?, TO_DATE(?, 'DD-MM-YYYY'), ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setDouble(1, cantidad);
                pstmt.setString(2, fecha);
                pstmt.setString(3, categoria);

                if (purchase_id != null) {
                    pstmt.setInt(4, purchase_id);
                    pstmt.setNull(5, java.sql.Types.INTEGER);
                } else if (n_contrato != null) {
                    pstmt.setNull(4, java.sql.Types.INTEGER);
                    pstmt.setInt(5, n_contrato);
                } else {
                    throw new SQLException("Uno de purchase_id o n_contrato debe tener un valor");
                }

                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 2291) { // Asumiendo que 2291 es el código de error para una violación de clave foránea
                System.err.println("Error: purchase_id o n_contrato no existe en la base de datos.");
            } else {
                System.err.println("Error al insertar gasto: " + e.getMessage());
            }
        }
    }

    /**
     * @brief Añade un coche a la tabla
     * @param serial_n
     * @param name
     * @param power
     * @param engine_disp 
     */
    public void insertarCar(String serial_n, String name, double power, double engine_disp){
        try {
            String sql = "INSERT INTO car (serial_n, name, power, engine_disp) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, serial_n);
                pstmt.setString(2, name);
                pstmt.setDouble(3, power);
                pstmt.setDouble(4, engine_disp);

                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1) { // El código de error específico para violaciones de la restricción UNIQUE
                System.err.println("El numero de serie (" + serial_n + ") que estás intentando "
                + "insertar con el coche "+ name + " ya existe."
                + "\nCoche no añadido.");
            } else {
                System.err.println("Error al insertar coche: " + e.getMessage());
            }
        }
    }
    
    /**
     * @brief Añade una relación en carmaterial
     * @param serial_n
     * @param material_id 
     */
    public void insertarCarMaterial(String serial_n, Integer material_id) {
    try {
        String sql = "INSERT INTO car_material (serial_n, material_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, serial_n);
            pstmt.setInt(2, material_id);

            pstmt.executeUpdate();
        }
    } catch (SQLException e) {
        if (e.getErrorCode() == 2291) {
            System.err.println("Error: serial_n (" + serial_n + ") o material_id (" + material_id + ") no existen en las tablas car o material.");
        } else {
            System.err.println("Error al insertar en car_material: " + e.getMessage());
        }
    }
}

    /**
     * @brief Añade una orden en la tabla orden
     * @param price
     * @param serial_n
     * @param CIF_client 
     */
    public void insertarOrder(double price, String serial_n, String CIF_client){
        try {
            String sql = "INSERT INTO order_ (price, serial_n, CIF_client) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setDouble(1, price);
                pstmt.setString(2,serial_n);
                pstmt.setString(3,CIF_client);

                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 2291) { // Asumiendo que 2291 es el código de error para una violación de clave foránea
                System.err.println("Error: el número de serie (" + serial_n + ") o el CIF_supp(" +CIF_client + ") no existen.");
                System.err.println("Pedido no añadido");
            } else {
                System.err.println("Error al insertar orden: " + e.getMessage());
            }
        }
    }
    
    /**
     * @brief Añade un ingreso en la tabla
     * @param cantidad
     * @param fecha
     * @param categoria
     * @param order_id 
     */
    public void insertarIngreso(double cantidad, String fecha, String categoria, Integer order_id) {
        try {
            String sql = "INSERT INTO ingreso (cantidad, fecha, categoria, order_id) VALUES (?, TO_DATE(?, 'DD-MM-YYYY'), ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setDouble(1, cantidad);
                pstmt.setString(2, fecha);
                pstmt.setString(3, categoria);

                if (order_id != null) {
                    pstmt.setInt(4, order_id);
                } else {
                    pstmt.setNull(4, java.sql.Types.INTEGER);
                }

                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 2291) {
                System.err.println("Error: order_id (" + order_id + ") no existe en la base de datos.");
            } else {
                System.err.println("Error al insertar ingreso: " + e.getMessage());
            }
        }
    }

    
    //POBLACION DE TABLAS//
    
    /**
     * @brief Añade información a la tabla empleado
     */
    public void poblarEmpleado(){
        insertarEmpleado("Juan", "Sanchez Fernandez", "77766471K", "608050821", "Calle Arabial 36, Granada");
        insertarEmpleado("Ana", "Gómez Martínez", "12345678A", "600123456", "Avenida de la Constitución 1, Madrid");
        insertarEmpleado("Carlos", "López García", "23456789B", "601234567", "Plaza de España 2, Valencia");
        insertarEmpleado("Marta", "Ruiz López", "34567890C", "602345678", "Calle Gran Vía 3, Sevilla");
        insertarEmpleado("Pedro", "Díaz Fernández", "45678901D", "603456789", "Calle Mayor 4, Almería");
        insertarEmpleado("Lucía", "Santos González", "56789012E", "604567890", "Calle del Prado 5, Jaén");
        insertarEmpleado("Pedro", "Fuentes Tirado", "59789012E", "605678901", "Paseo de la Castellana 6, Alcalá la Real");
    }
    
    /**
     * @brief Añade información a la tabla Contrato
     */
    public void poblarContrato(){
        insertarContrato("Dirección", "02-04-2002", 7.5, "activo", 1); // Suponiendo que Juan tiene n_empleado = 1
        insertarContrato("Recursos Humanos", "15-05-2010", 5.0, "activo", 2); // Suponiendo que Ana tiene n_empleado = 2
        insertarContrato("Marketing", "07-07-2011", 4.0, "activo", 3); // Suponiendo que Carlos tiene n_empleado = 3
        insertarContrato("Ventas", "12-09-2015", 3.5, "activo", 4); // Suponiendo que Marta tiene n_empleado = 4
        insertarContrato("IT", "23-01-2013", 6.5, "activo", 5); // Suponiendo que Pedro tiene n_empleado = 5
        insertarContrato("Atención al Cliente", "30-11-2018", 2.5, "activo", 6); // Suponiendo que Lucía tiene n_empleado = 6
        insertarContrato("Logística", "16-07-2014", 7.0, "activo", null); // Sin empleado asignado
        insertarContrato("Producción", "04-08-2017", 8.0, "inactivo", null); // Sin empleado asignado
        insertarContrato("Investigación y Desarrollo", "21-02-2012", 6.0, "inactivo", null); // Sin empleado asignado
        insertarContrato("Legal", "09-10-2019", 4.5, "activo", 7); // Suponiendo que Javier tiene n_empleado = 7
    }
    
    /**
     * @brief Añade información a la tabla Supplier
     */
    public void poblarSupplier(){
        insertarSupplier("75690375J", "Supplies Co", "1234 Main St", "branded");
        insertarSupplier("84622740L", "Office Essentials", "5678 Second St", "generic");
        insertarSupplier("94611648K", "Industrial Goods", "9101 Third Ave", "branded");
        insertarSupplier("94612740V", "Global Parts", "1122 Fourth Blvd", "generic");
        insertarSupplier("22747569O", "Tech Supplies", "1314 Fifth Road", "branded");
        insertarSupplier("95302847H", "General Equipments", "1516 Sixth Lane", "generic");
    }
    
    /**
     * @brief Añade clientes
     */
    public void poblarClient(){
        insertarClient("12384978A", "Miguel Torres", "Calle Gran Vía 28, Madrid", "branded");
        insertarClient("23456789B", "Laura García", "Paseo de Gracia 15, Barcelona", "generic");
        insertarClient("34236490C", "Alejandro Sánchez", "Avenida de la Constitución 5, Sevilla", "branded");
        insertarClient("45678901D", "Carmen López", "Calle Bailén 41, Granada", "generic");
        insertarClient("56329012E", "Francisco Martínez", "Plaza Nueva 22, Bilbao", "branded");
        insertarClient("67890123F", "Sofía González", "Calle Príncipe 25, Vigo", "generic");
        insertarClient("78901234G", "Juan Fernández", "Calle Mayor 30, Zaragoza", "branded");
        insertarClient("89012345H", "Elena Rodríguez", "Calle Tetuán 12, Valencia", "generic");
        insertarClient("90123456I", "Antonio Navarro", "Ronda Sant Pere 19, Palma", "branded");
        insertarClient("01234567J", "Isabel Díaz", "Calle San Francisco 6, Alicante", "generic");
    }
    
    /**
     * @brief añade información sobre materiales
     */
    public void poblarMaterial(){
        insertarMaterial("Motor", 1.2, 0.7, 0.8, 220.0);
        insertarMaterial("Parabrisas", 1.5, 0.02, 1.0, 15.0);
        insertarMaterial("Asiento", 0.5, 1.0, 0.6, 18.0);
        insertarMaterial("Rueda", 0.6, 0.6, 0.25, 12.0);
        insertarMaterial("Volante", 0.4, 0.1, 0.4, 3.5);
    }
    
    /**
     * Pobla la tabla purchase
     */
    public void poblarPurchase(){
        insertarPurchase(100.00, 5, 1, "75690375J"); // Compra 5 motores de Supplies Co
        insertarPurchase(200.00, 3, 2, "84622740L"); // Compra 3 parabrisas de Office Essentials
        insertarPurchase(150.00, 10, 3, "94611648K"); // Compra 10 asientos de Industrial Goods
        insertarPurchase(75.00, 20, 4, "94612740V"); // Compra 20 ruedas de Global Parts
        insertarPurchase(50.00, 15, 5, "22747569O"); // Compra 15 volantes de Tech Supplies
        insertarPurchase(120.00, 6, 1, "95302847H");
    }
    
    /**
     * @brief Pobla la tabla gasto
     */
    public void poblarGasto(){
         // Gastos asociados a compras
        insertarGasto(500, "05-04-2021", "Compra de motores", 1, null); // Gasto asociado a la primera compra
        insertarGasto(600, "10-05-2021", "Compra de parabrisas", 2, null); // Gasto asociado a la segunda compra
        insertarGasto(1500, "15-06-2021", "Compra de asientos", 3, null); // Gasto asociado a la tercera compra

        // Gastos asociados a contratos
        insertarGasto(2000, "20-04-2022", "Pago de contrato", null, 1); // Gasto asociado al primer contrato
        insertarGasto(2500, "25-05-2022", "Pago de contrato", null, 2); // Gasto asociado al segundo contrato
        insertarGasto(3000, "30-06-2022", "Pago de contrato", null, 3); // Gasto asociado al tercer contrato
    }
    
    /**
     * @brief Pobla la tabla Car
     */
    public void poblarCar(){
        insertarCar("MM1001", "Mastrota Veloz", 250, 2.0); // Coche deportivo
        insertarCar("MM1002", "Mastrota Familiar", 150, 1.6); // Coche familiar
        insertarCar("MM1003", "Mastrota Eco", 100, 1.2); // Coche económico
        insertarCar("MM1004", "Mastrota Lujo", 300, 3.0); // Coche de lujo
        insertarCar("MM1005", "Mastrota GT", 350, 3.5); // Gran turismo
        insertarCar("MM1006", "Mastrota Compacto", 130, 1.4); // Coche compacto
        insertarCar("MM1007", "Mastrota SUV", 200, 2.5); // SUV
        insertarCar("MM1008", "Mastrota Roadster", 280, 2.2); // Roadster
        insertarCar("MM1009", "Mastrota Crossover", 180, 2.0); // Crossover
        insertarCar("MM1010", "Mastrota Eléctrico", 150, 0.0); // Coche eléctrico
    }
    
    /**
     * @brief Puebla la tabla CarMaterial
     */
    public void poblarCarMaterial(){
        // Relacionar motores con varios coches
        insertarCarMaterial("MM1001", 1); // Motor en Mastrota Veloz
        insertarCarMaterial("MM1004", 1); // Motor en Mastrota Lujo
        insertarCarMaterial("MM1005", 1); // Motor en Mastrota GT

        // Relacionar parabrisas con coches
        insertarCarMaterial("MM1002", 2); // Parabrisas en Mastrota Familiar
        insertarCarMaterial("MM1003", 2); // Parabrisas en Mastrota Eco

        // Relacionar asientos con coches
        insertarCarMaterial("MM1006", 3); // Asiento en Mastrota Compacto
        insertarCarMaterial("MM1007", 3); // Asiento en Mastrota SUV

        // Relacionar ruedas con coches
        insertarCarMaterial("MM1008", 4); // Rueda en Mastrota Roadster
        insertarCarMaterial("MM1009", 4); // Rueda en Mastrota Crossover

        // Relacionar volantes con coches
        insertarCarMaterial("MM1010", 5); // Volante en Mastrota Eléctrico
        insertarCarMaterial("MM1001", 5); // Volante en Mastrota Veloz
    }
    
    /**
     * @brief Puebla la tabla order
     */
    public void poblarOrder(){
        insertarOrder(30000.00, "MM1001", "12384978A"); // Miguel Torres compra Mastrota Veloz
        insertarOrder(20000.00, "MM1002", "23456789B"); // Laura García compra Mastrota Familiar
        insertarOrder(18000.00, "MM1003", "34236490C"); // Alejandro Sánchez compra Mastrota Eco
        insertarOrder(40000.00, "MM1004", "45678901D"); // Carmen López compra Mastrota Lujo
        insertarOrder(35000.00, "MM1005", "56329012E"); // Francisco Martínez compra Mastrota GT
        insertarOrder(22000.00, "MM1006", "67890123F"); // Sofía González compra Mastrota Compacto
        insertarOrder(25000.00, "MM1007", "78901234G"); // Juan Fernández compra Mastrota SUV
        insertarOrder(28000.00, "MM1008", "89012345H"); // Elena Rodríguez compra Mastrota Roadster
        insertarOrder(27000.00, "MM1009", "90123456I"); // Antonio Navarro compra Mastrota Crossover
        insertarOrder(30000.00, "MM1010", "01234567J"); // Isabel Díaz compra Mastrota Eléctrico
    }
    
    /**
     * @brief puebla la tabla Ingreso
     */
    public void poblarIngreso(){
        //Ingresos relacionados con venta de coches.
        insertarIngreso(30000.00, "06-04-2021", "Venta de coche", 1); // Ingreso por la venta a Miguel Torres
        insertarIngreso(20000.00, "11-05-2021", "Venta de coche", 2); // Ingreso por la venta a Laura García
        insertarIngreso(18000.00, "16-06-2021", "Venta de coche", 3); // Ingreso por la venta a Alejandro Sánchez
        insertarIngreso(40000.00, "21-07-2021", "Venta de coche", 4); // Ingreso por la venta a Carmen López
        insertarIngreso(35000.00, "26-08-2021", "Venta de coche", 5); // Ingreso por la venta a Francisco Martíne
        
        // Ingresos por servicios (mantenimiento, reparaciones, etc.)
        insertarIngreso(5000.00, "10-07-2021", "Servicios de mantenimiento", null);

        // Ingresos por alquiler de instalaciones o equipos
        insertarIngreso(3000.00, "15-08-2021", "Alquiler de equipos", null);

        // Ingresos por actividades secundarias (por ejemplo, cursos de conducción)
        insertarIngreso(2000.00, "20-09-2021", "Cursos de conducción", null);

        // Ingresos por intereses (por ejemplo, intereses de inversiones financieras)
        insertarIngreso(1500.00, "25-10-2021", "Intereses financieros", null);
    }
    
    /**
     * @brief Llama al resto de métodos de población
     */
    public void poblarTablas() {
        poblarEmpleado();
        poblarContrato();
        poblarSupplier();
        poblarClient();
        poblarMaterial();
        poblarPurchase();
        poblarGasto();
        poblarCar();
        poblarCarMaterial();
        
        poblarOrder();
        poblarIngreso();
        
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