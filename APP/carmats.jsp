<%@ page import="java.sql.*"%>

<%
boolean shouldRedirect = false;
try {
    String action = request.getParameter("action");
    

    if ("addCarMaterial".equals(action)) {
        // Add Car Material
        String serial_n = request.getParameter("serial_n");
        int material_id = Integer.parseInt(request.getParameter("material_id"));

        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es", "x7766471", "x7766471");
        PreparedStatement ps = conn.prepareStatement("INSERT INTO CAR_MATERIAL (serial_n, material_id) VALUES (?, ?)");
        ps.setString(1, serial_n);
        ps.setInt(2, material_id);

        int x = ps.executeUpdate();
        if (x != 0) {
            shouldRedirect = true;
        } else {
            out.print("Something went wrong");
        } 
        conn.close();
        ps.close();
        
    } else if ("listCarMaterials".equals(action)) {
        // List Car Materials
        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es", "x7766471", "x7766471");
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM CAR_MATERIAL");
        ResultSet rs = ps.executeQuery();

        out.print("<table border='1'>");
        out.print("<tr><th>Serial Number</th><th>Material ID</th></tr>");

        while (rs.next()) {
            out.print("<tr>");
            out.print("<td>" + rs.getString("serial_n") + "</td>");
            out.print("<td>" + rs.getInt("material_id") + "</td>");
            out.print("</tr>");
        }

        out.print("</table>");
        conn.close();
        ps.close();
    } 

} catch (Exception e) {
    e.printStackTrace();
    out.print(e.getMessage());
}

if (shouldRedirect) {
    // Redirect only if shouldRedirect is true
    response.sendRedirect("index.html");
}
%>
