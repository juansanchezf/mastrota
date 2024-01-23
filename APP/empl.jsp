<%@ page import="java.sql.*"%>

<%
boolean shouldRedirect = false;
try {
    
    String action = request.getParameter("action");
    
    if ("addEmployee".equals(action)) {
     
    String nombre = request.getParameter("nombre");
    String apellido = request.getParameter("apellido");
    String dni = request.getParameter("dni");
    String telefono = request.getParameter("telefono");
    String direccion = request.getParameter("direccion");

    Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es", "x7766471", "x7766471");
    PreparedStatement ps = conn.prepareStatement("INSERT INTO EMPLEADO (nombre, apellidos, dni, telefono, direccion) VALUES (?, ?, ?, ?, ?)");
    ps.setString(1, nombre);
    ps.setString(2, apellido);
    ps.setString(3, dni);
    ps.setString(4, telefono);
    ps.setString(5, direccion);

    int x = ps.executeUpdate();
    if (x != 0) {
        shouldRedirect = true;
    } else {
        out.print("Something went wrong");
    }
    conn.close();
    ps.close();
    
    }

    else if ("listEmployees".equals(action)) {
    Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es", "x7766471", "x7766471");
    PreparedStatement ps = conn.prepareStatement("SELECT * FROM EMPLEADO");
    ResultSet rs = ps.executeQuery();

    out.print("<table border='1'>");
    out.print("<tr><th>n_empleado</th><th>Nombre</th><th>Apellido</th><th>DNI</th><th>Telefono</th><th>Direccion</th></tr>");

    while (rs.next()) {
        out.print("<tr>");
        out.print("<td>" + rs.getString("n_empleado") + "</td>");
        out.print("<td>" + rs.getString("nombre") + "</td>");
        out.print("<td>" + rs.getString("apellidos") + "</td>");
        out.print("<td>" + rs.getString("dni") + "</td>");
        out.print("<td>" + rs.getString("telefono") + "</td>");
        out.print("<td>" + rs.getString("direccion") + "</td>");
        out.print("</tr>");
    }

    out.print("</table>");
    conn.close();
    ps.close();
    }

    else if ("deleteEmployee".equals(action)) {
        String n_empleado = request.getParameter("n_empleado");
    
        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es", "x7766471", "x7766471");
        PreparedStatement ps = conn.prepareStatement("DELETE FROM EMPLEADO WHERE n_empleado = ?");
        ps.setString(1, n_empleado);
    
        int x = ps.executeUpdate();
        if (x != 0) {
            shouldRedirect = true;
        } else {
            out.print("Something went wrong");
        }
        conn.close();
        ps.close();
    }

    else if ("editEmployee".equals(action)) {
        String n_empleado = request.getParameter("n_empleado");
        // Recupera i nuovi dati dal modulo HTML
        String nombre = request.getParameter("nombre");
        String apellido = request.getParameter("apellido");
        String dni = request.getParameter("dni");
        String telefono = request.getParameter("telefono");
        String direccion = request.getParameter("direccion");
    
        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es", "x7766471", "x7766471");
        PreparedStatement ps = conn.prepareStatement("UPDATE EMPLEADO SET nombre = ?, apellidos = ?, dni= ?, telefono= ?, direccion= ? WHERE n_empleado = ?");
        ps.setString(1, nombre);
        ps.setString(2, apellido);
        ps.setString(3, dni);
        ps.setString(4, telefono);
        ps.setString(5, direccion);
        ps.setString(6, n_empleado);
    
        int x = ps.executeUpdate();
        if (x != 0) {
            shouldRedirect = true;
        } else {
            out.print("Something went wrong");
        }
        conn.close();
        ps.close();
    }
    

} catch (Exception e) {
    e.printStackTrace();
    out.print(e.getMessage());
}

if (shouldRedirect) {
    // Esegue il reindirizzamento solo se shouldRedirect è true
    response.sendRedirect("index.html");
}
%>