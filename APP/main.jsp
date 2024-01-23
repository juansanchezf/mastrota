<%@ page import="java.sql.*"%>

<%
try {
    
    String action = request.getParameter("action");
    
    if ("crear".equals(action)) {
            String command = request.getParameter("command");
        try {
        
            Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es", "x7766471", "x7766471");
            Statement stmt = connection.createStatement();
            System.out.println(command);
            System.out.println("Connessione al database stabilita con successo!");
            String a = "CREATE TABLE empleado(n_empleado NUMBER GENERATED ALWAYS AS IDENTITY, nombre VARCHAR(50), apellidos VARCHAR(60), DNI VARCHAR2(20) UNIQUE, telefono VARCHAR2(15), direccion VARCHAR2(255), PRIMARY KEY(n_empleado))";
            String b = "CREATE TABLE contrato(n_contrato NUMBER GENERATED ALWAYS AS IDENTITY, departamento VARCHAR2(255), fecha_inicio DATE, duracion INT, estado VARCHAR2(8) CHECK(estado IN('activo','inactivo')), n_empleado NUMBER, PRIMARY KEY(n_contrato), FOREIGN KEY(n_empleado) REFERENCES empleado(n_empleado) ON DELETE SET NULL)";
            String c = "CREATE TABLE supplier(CIF_supp VARCHAR2(20), denomination VARCHAR2(255), address VARCHAR2(255), type VARCHAR2(255), PRIMARY KEY (CIF_supp))";
            String d = "CREATE TABLE client(CIF_client VARCHAR2(20), denomination VARCHAR2(255), address VARCHAR2(255), type VARCHAR2(255), PRIMARY KEY(CIF_client))";
            String e = "CREATE TABLE material(material_id NUMBER GENERATED ALWAYS AS IDENTITY, name VARCHAR2(255), length NUMBER, height NUMBER, width NUMBER, weight NUMBER, PRIMARY KEY(material_id))";
            String f = "CREATE TABLE purchase(purchase_id NUMBER GENERATED ALWAYS AS IDENTITY, price NUMBER, quantity NUMBER, material_id NUMBER, CIF_supp VARCHAR2(20), PRIMARY KEY (purchase_id), FOREIGN KEY(material_id) REFERENCES material(material_id) ON DELETE SET NULL, FOREIGN KEY(CIF_supp) REFERENCES supplier(CIF_supp) ON DELETE SET NULL)";
            String g = "CREATE TABLE gasto(id_gasto NUMBER GENERATED ALWAYS AS IDENTITY, cantidad NUMBER, fecha DATE, categoria VARCHAR2(255), purchase_id NUMBER, n_contrato NUMBER, PRIMARY KEY(id_gasto), FOREIGN KEY (purchase_id) REFERENCES purchase(purchase_id) ON DELETE CASCADE, FOREIGN KEY (n_contrato) REFERENCES contrato(n_contrato) ON DELETE CASCADE)";
            String h = "CREATE TABLE car(serial_n VARCHAR2(255), name VARCHAR2(255), power NUMBER, engine_disp NUMBER, PRIMARY KEY(serial_n))";
            String i = "CREATE TABLE car_material(serial_n VARCHAR2(255), material_id NUMBER, FOREIGN KEY (serial_n) REFERENCES car(serial_n) ON DELETE CASCADE, FOREIGN KEY (material_id) REFERENCES material(material_id) ON DELETE CASCADE, PRIMARY KEY(serial_n, material_id))";
            String j = "CREATE TABLE order_(order_id NUMBER GENERATED ALWAYS AS IDENTITY, price NUMBER, serial_n VARCHAR2(255), CIF_client VARCHAR(20), PRIMARY KEY (order_id), FOREIGN KEY (serial_n) REFERENCES car(serial_n) ON DELETE CASCADE, FOREIGN KEY (CIF_client) REFERENCES client(CIF_client) ON DELETE CASCADE)";
            String k = "CREATE TABLE ingreso(ingreso_id NUMBER GENERATED ALWAYS AS IDENTITY, cantidad NUMBER, fecha DATE, categoria VARCHAR(255), order_id NUMBER, PRIMARY KEY(ingreso_id), FOREIGN KEY(order_id) REFERENCES order_(order_id) ON DELETE CASCADE)";
            
        
        if("empleado".equals(command)){stmt.executeUpdate(a); }
        else if("contrato".equals(command)){stmt.executeUpdate(b);}
        else if("supplier".equals(command)){stmt.executeUpdate(c); }
        else if("client".equals(command)){stmt.executeUpdate(d);}
        else if("material".equals(command)){stmt.executeUpdate(e); }
        else if("purchase".equals(command)){stmt.executeUpdate(f);}
        else if("gasto".equals(command)){stmt.executeUpdate(g); }
        else if("car".equals(command)){stmt.executeUpdate(h);}
        else if("car_material".equals(command)){stmt.executeUpdate(i);} 
        else if("order_".equals(command)){stmt.executeUpdate(j);}
        else if("ingreso".equals(command)){stmt.executeUpdate(k);}

            stmt.close();
            connection.close();
        System.out.println("Tablas creadas o verificadas exitosamente.");
        response.sendRedirect("index.html");

    } catch (SQLException e) {
        System.err.println("Error al crear las tablas: " + e.getMessage());
        e.printStackTrace();
    }
    } 

        

    else if ("borrar".equals(action)) {

        try {
            Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es", "x7766471", "x7766471");
            Statement stmt = connection.createStatement();

            // Eliminare tabelle con vincoli di chiave esterna in ordine
            String[] tablesToDelete = {"INGRESO", "ORDER_", "CAR_MATERIAL", "CAR", "GASTO", "PURCHASE", "MATERIAL", "CLIENT", "SUPPLIER", "CONTRATO", "EMPLEADO"};

    for (String tableName : tablesToDelete) {
        String dropTableQuery = "DROP TABLE " + tableName;
        stmt.executeUpdate(dropTableQuery);
    }

    stmt.close();
    connection.close();
    System.out.println("Tablas eliminadas exitosamente.");
    response.sendRedirect("index.html");
            } catch (SQLException e) {
                System.err.println("Error al eliminar las tablas: " + e.getMessage());
            }
            
        } 
            

    

} catch (Exception e) {
    e.printStackTrace();
    out.print(e.getMessage());
}

%>