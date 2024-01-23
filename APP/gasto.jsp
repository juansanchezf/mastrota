<%@ page import="java.sql.*"%>
<%@ page import="java.text.SimpleDateFormat" %>
<%
boolean shouldRedirect = false;
try {
    String action = request.getParameter("action");
    

    if ("addGasto".equals(action)) {
        int cantidad = Integer.parseInt(request.getParameter("cantidad"));
        String fecha = request.getParameter("fecha");
        String categoria = request.getParameter("categoria");
        int purchase_id = Integer.parseInt(request.getParameter("purchase_id"));
        int n_contrato = Integer.parseInt(request.getParameter("n_contrato"));

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        java.util.Date parsedDate = sdf.parse(fecha);
        java.sql.Date sqlDate = new java.sql.Date(parsedDate.getTime());

        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es", "x7766471", "x7766471");
        PreparedStatement ps = null;
        int y = 0;

        
            ps = conn.prepareStatement("INSERT INTO GASTO (cantidad, fecha, categoria, purchase_id, n_contrato) VALUES (?, ?, ?, ?, ?)");
            ps.setInt(1, cantidad);
            ps.setDate(2, sqlDate);
            ps.setString(3, categoria);
            ps.setInt(4, purchase_id);
            ps.setInt(5, n_contrato);

            y = ps.executeUpdate();
            if (y != 0) {
                shouldRedirect = true;
            } else {
                out.print("Something went wrong");
            }
         
        conn.close();
        ps.close();
    } else if ("listGasto".equals(action)) {
        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es", "x7766471", "x7766471");
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM GASTO");
        ResultSet rs = ps.executeQuery();

        out.print("<table border='1'>");
        out.print("<tr><th>ID</th><th>Cantidad</th><th>Fecha</th><th>Categoria</th><th>Purchase ID</th><th>N Contrato</th></tr>");

        while (rs.next()) {
            out.print("<tr>");
            out.print("<td>" + rs.getString("id_gasto") + "</td>");
            out.print("<td>" + rs.getInt("cantidad") + "</td>");
            out.print("<td>" + rs.getString("fecha") + "</td>");
            out.print("<td>" + rs.getString("categoria") + "</td>");
            out.print("<td>" + rs.getInt("purchase_id") + "</td>");
            out.print("<td>" + rs.getInt("n_contrato") + "</td>");
            out.print("</tr>");
        }
        out.print("</table>");
        conn.close();
        ps.close();
    } else if ("deleteGasto".equals(action)) {
        String id_gasto = request.getParameter("id_gasto");
        
        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es", "x7766471", "x7766471");
        PreparedStatement ps = null;
        
            ps = conn.prepareStatement("DELETE FROM GASTO WHERE id_gasto = ?");
            ps.setString(1, id_gasto);
            int x = ps.executeUpdate();
            
            if (x != 0) {
                shouldRedirect = true;
            } else {
                out.print("Something went wrong");
            }
        
        conn.close();
        ps.close();
    } else if ("modifyGasto".equals(action)) {
        String id_gasto = request.getParameter("id_gasto");
        int cantidad = Integer.parseInt(request.getParameter("cantidad"));
        String fecha = request.getParameter("fecha");
        String categoria = request.getParameter("categoria");
        int purchase_id = Integer.parseInt(request.getParameter("purchase_id"));
        int n_contrato = Integer.parseInt(request.getParameter("n_contrato"));

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        java.util.Date parsedDate = sdf.parse(fecha);
        java.sql.Date sqlDate = new java.sql.Date(parsedDate.getTime());

        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es", "x7766471", "x7766471");
        PreparedStatement ps = null;
        int y = 0;

        
            ps = conn.prepareStatement("UPDATE GASTO SET cantidad = ?, fecha = ?, categoria = ?, purchase_id = ?, n_contrato = ? WHERE id_gasto = ?");
            ps.setInt(1, cantidad);
            ps.setDate(2, sqlDate);
            ps.setString(3, categoria);
            ps.setInt(4, purchase_id);
            ps.setInt(5, n_contrato);
            ps.setString(6, id_gasto);

            y = ps.executeUpdate();
            if (y != 0) {
                shouldRedirect = true;
            } else {
                out.print("Something went wrong");
            }
        
        conn.close();
        ps.close();
    }

    if (shouldRedirect) {
        response.sendRedirect("index.html");
    }
} catch (Exception e) {
    e.printStackTrace();
    out.print(e.getMessage());
}

%>
