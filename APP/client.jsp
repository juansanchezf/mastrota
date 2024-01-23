<%@ page import="java.sql.*"%>

<%
boolean shouldRedirect = false;
try {
    String action = request.getParameter("action");
    

    if ("addCliente".equals(action)) {
        // Add Client
        String CIF_client = request.getParameter("CIF_client");
        String denomination = request.getParameter("denomination");
        String address = request.getParameter("address");
        String type = request.getParameter("type");

        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es", "x7766471", "x7766471");
        PreparedStatement ps = conn.prepareStatement("INSERT INTO CLIENT (CIF_client, denomination, address, type) VALUES (?, ?, ?, ?)");
        ps.setString(1, CIF_client);
        ps.setString(2, denomination);
        ps.setString(3, address);
        ps.setString(4, type);

        int i = ps.executeUpdate();
        if (i != 0) {
            shouldRedirect = true;
        } else {
            out.print("Something went wrong");
        } 
        conn.close();
        ps.close();
    } else if ("listCliente".equals(action)) {
        // List Clients
        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es", "x7766471", "x7766471");
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM CLIENT");
        ResultSet rs = ps.executeQuery();

        out.print("<table border='1'>");
        out.print("<tr><th>CIF_client</th><th>Denomination</th><th>Address</th><th>Type</th></tr>");

        while (rs.next()) {
            out.print("<tr>");
            out.print("<td>" + rs.getString("CIF_client") + "</td>");
            out.print("<td>" + rs.getString("denomination") + "</td>");
            out.print("<td>" + rs.getString("address") + "</td>");
            out.print("<td>" + rs.getString("type") + "</td>");
            out.print("</tr>");
        }

        out.print("</table>");
        conn.close();
        ps.close();
    } else if ("deleteCliente".equals(action)) {
        // Delete Client
        String CIF_client = request.getParameter("CIF_client");

        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es", "x7766471", "x7766471");
        PreparedStatement ps = conn.prepareStatement("DELETE FROM CLIENT WHERE CIF_client = ?");
        ps.setString(1, CIF_client);

        int i = ps.executeUpdate();
        if (i != 0) {
            shouldRedirect = true;
        } else {
            out.print("Something went wrong");
        } 
        conn.close();
        ps.close();
    } else if ("modifyCliente".equals(action)) {
        // Modify Client
        String CIF_client = request.getParameter("CIF_client");
        String denomination = request.getParameter("denomination");
        String address = request.getParameter("address");
        String type = request.getParameter("type");

        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es", "x7766471", "x7766471");
        PreparedStatement ps = conn.prepareStatement("UPDATE CLIENT SET denomination = ?, address = ?, type = ? WHERE CIF_client = ?");
        ps.setString(1, denomination);
        ps.setString(2, address);
        ps.setString(3, type);
        ps.setString(4, CIF_client);

        int i = ps.executeUpdate();
        if (i != 0) {
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
    // Redirect only if shouldRedirect is true
    response.sendRedirect("index.html");
}
%>
