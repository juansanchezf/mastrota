<%@ page import="java.sql.*"%>
<%@ page import="java.text.SimpleDateFormat" %>
<%
boolean shouldRedirect = false;
try {
    String action = request.getParameter("action");
    

    if ("addIngreso".equals(action)) {
        int cantidad = Integer.parseInt(request.getParameter("cantidad"));
        String fecha = request.getParameter("fecha");
        String categoria = request.getParameter("categoria");
        int order_id = Integer.parseInt(request.getParameter("order_id"));

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        java.util.Date parsedDate = sdf.parse(fecha);
        java.sql.Date sqlDate = new java.sql.Date(parsedDate.getTime());

        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es", "x7766471", "x7766471");
        PreparedStatement ps = conn.prepareStatement("INSERT INTO INGRESO (cantidad, fecha, categoria, order_id) VALUES (?, ?, ?, ?)");
        ps.setInt(1, cantidad);
        ps.setDate(2, sqlDate);
        ps.setString(3, categoria);
        ps.setInt(4, order_id);

        int y = ps.executeUpdate();
        if (y != 0) {
            shouldRedirect = true;
        } else {
            out.print("Something went wrong");
        } 
        conn.close();
        ps.close();
    } else if ("listIngreso".equals(action)) {
        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es", "x7766471", "x7766471");
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM INGRESO");
        ResultSet rs = ps.executeQuery();

        out.print("<table border='1'>");
        out.print("<tr><th>ID</th><th>Cantidad</th><th>Fecha</th><th>Categoria</th><th>Order ID</th></tr>");

        while (rs.next()) {
            out.print("<tr>");
            out.print("<td>" + rs.getString("ingreso_id") + "</td>");
            out.print("<td>" + rs.getInt("cantidad") + "</td>");
            out.print("<td>" + rs.getString("fecha") + "</td>");
            out.print("<td>" + rs.getString("categoria") + "</td>");
            out.print("<td>" + rs.getInt("order_id") + "</td>");
            out.print("</tr>");
        }
        out.print("</table>");
        conn.close();
        ps.close();
    } else if ("deleteIngreso".equals(action)) {
        String ingreso_id = request.getParameter("ingreso_id");

        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es", "x7766471", "x7766471");
        PreparedStatement ps = conn.prepareStatement("DELETE FROM INGRESO WHERE ingreso_id = ?");
        ps.setString(1, ingreso_id);

        int i = ps.executeUpdate();
        if (i != 0) {
            shouldRedirect = true;
        } else {
            out.print("Something went wrong");
        } 
        conn.close();
        ps.close();
    } else if ("modifyIngreso".equals(action)) {
        String ingreso_id = request.getParameter("ingreso_id");
        int cantidad = Integer.parseInt(request.getParameter("cantidad"));
        String fecha = request.getParameter("fecha");
        String categoria = request.getParameter("categoria");
        int order_id = Integer.parseInt(request.getParameter("order_id"));

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        java.util.Date parsedDate = sdf.parse(fecha);
        java.sql.Date sqlDate = new java.sql.Date(parsedDate.getTime());
        
        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es", "x7766471", "x7766471");
        PreparedStatement ps = conn.prepareStatement("UPDATE INGRESO SET cantidad = ?, fecha = ?, categoria = ?, order_id = ? WHERE ingreso_id = ?");
        ps.setInt(1, cantidad);
        ps.setDate(2, sqlDate);
        ps.setString(3, categoria);
        ps.setInt(4, order_id);
        ps.setString(5, ingreso_id);

        int y = ps.executeUpdate();
        if (y != 0) {
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
    response.sendRedirect("index.html");
}

%>
