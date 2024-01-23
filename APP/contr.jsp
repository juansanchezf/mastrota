<%@ page import="java.sql.*"%>
<%@ page import="java.text.SimpleDateFormat" %>
<%
boolean shouldRedirect = false;
try{
    String action = request.getParameter("action");
    

    if ("addContrato".equals(action)) {
    String departamento=request.getParameter("departamento");
    String fecha_inicio=request.getParameter("fecha_inicio");
    String duracionStr=request.getParameter("duracion");
    int duracion = Integer.parseInt(duracionStr);
    String n_empleado=request.getParameter("n_empleado");
    
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    java.util.Date parsedDate = sdf.parse(fecha_inicio);
    java.sql.Date sqlDate = new java.sql.Date(parsedDate.getTime());

    Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es", "x7766471", "x7766471");
    PreparedStatement ps = conn.prepareStatement("INSERT INTO CONTRATO (departamento, fecha_inicio, DURACION, n_empleado) VALUES (?, ?, ?, ?)");
    ps.setString(1,departamento);
    ps.setDate(2,sqlDate);
    ps.setInt(3,duracion);
    ps.setString(4,n_empleado);
    int y= ps.executeUpdate();
   
    if(y!=0){
        shouldRedirect = true;
    }else{
        out.print("Something went wrong");
    }
    ps.close();
    conn.close();
    }else if ("listContrato".equals(action)) {
        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es", "x7766471", "x7766471");
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM CONTRATO");
        ResultSet rs = ps.executeQuery();

        out.print("<table border='1'>");
        out.print("<tr><th>n_contrato</th><th>Departamento</th><th>Fecha_inicio</th><th>Duracion</th><th>n_empleado</th></tr>");

        while (rs.next()) {
            out.print("<tr>");
            out.print("<td>" + rs.getString("n_contrato") + "</td>");
            out.print("<td>" + rs.getString("departamento") + "</td>");
            out.print("<td>" + rs.getString("fecha_inicio") + "</td>");
            out.print("<td>" + rs.getInt("duracion") + "</td>");
            out.print("<td>" + rs.getString("n_empleado") + "</td>");
            out.print("</tr>");
        }
        out.print("</table>");
    }else if ("deleteContrato".equals(action)) {
        String n_contrato = request.getParameter("n_contrato");

        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es", "x7766471", "x7766471");
        PreparedStatement ps = conn.prepareStatement("DELETE FROM CONTRATO WHERE n_contrato = ?");
        ps.setString(1, n_contrato);
        
        int i = ps.executeUpdate();
        if (i != 0) {
            shouldRedirect = true;
        } else {
            out.print("Something went wrong");
        } 
        ps.close();
        conn.close();
    }else if ("modifyContr".equals(action)) {
        String n_contrato = request.getParameter("n_contrato");
        String departamento=request.getParameter("departamento");
        String fecha_inicio=request.getParameter("fecha_inicio");
        String duracionStr=request.getParameter("duracion");
        int duracion = Integer.parseInt(duracionStr);
        String n_empleado=request.getParameter("n_empleado");
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        java.util.Date parsedDate = sdf.parse(fecha_inicio);
        java.sql.Date sqlDate = new java.sql.Date(parsedDate.getTime());
    
        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es", "x7766471", "x7766471");
        PreparedStatement ps = conn.prepareStatement("UPDATE CONTRATO SET departamento = ?, fecha_inicio = ?, duracion = ?,n_empleado = ? WHERE n_contrato = ?");
        ps.setString(1,departamento);
        ps.setDate(2,sqlDate);
        ps.setInt(3,duracion);
        ps.setString(4,n_empleado);
        ps.setString(5,n_contrato);
        int y= ps.executeUpdate();
        if(y!=0){
            shouldRedirect = true;
        }else{
            out.print("Something went wrong");
            
        }
        ps.close();
        conn.close();
    }
  } catch(Exception e){
        e.printStackTrace();
        out.print(e.getMessage());
    }
    if (shouldRedirect) {
        // Redirect only if shouldRedirect is true
        response.sendRedirect("index.html");
    }
    %>