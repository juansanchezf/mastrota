<%@ page import="java.sql.*"%>

<%
boolean shouldRedirect = false;
try {
    String action = request.getParameter("action");
    

    if ("addSupplier".equals(action)) {
        String CIF_supp = request.getParameter("CIF_supp");
        String denomination = request.getParameter("denomination");
        String address = request.getParameter("address");
        String type = request.getParameter("type");

        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es", "x7766471", "x7766471");
        PreparedStatement ps = conn.prepareStatement("INSERT INTO SUPPLIER (CIF_supp, denomination, address, type) VALUES (?, ?, ?, ?)");
        ps.setString(1, CIF_supp);
        ps.setString(2, denomination);
        ps.setString(3, address);
        ps.setString(4, type);

        int z = ps.executeUpdate();
        if (z != 0) {
            shouldRedirect = true;
        } else {
            out.print("Something went wrong");
        } 
        conn.close();
        ps.close();
    } else if ("listSupplier".equals(action)) {
        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es", "x7766471", "x7766471");
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM SUPPLIER");
        ResultSet rs = ps.executeQuery();

        out.print("<table border='1'>");
        out.print("<tr><th>CIF Supplier</th><th>Denomination</th><th>Address</th><th>Type</th></tr>");

        while (rs.next()) {
            out.print("<tr>");
            out.print("<td>" + rs.getString("CIF_supp") + "</td>");
            out.print("<td>" + rs.getString("denomination") + "</td>");
            out.print("<td>" + rs.getString("address") + "</td>");
            out.print("<td>" + rs.getString("type") + "</td>");
            out.print("</tr>");
        }
        out.print("</table>");
        conn.close();
        ps.close();
    } else if ("deleteSupplier".equals(action)) {
        String CIF_supp = request.getParameter("CIF_supp");

        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es", "x7766471", "x7766471");
        PreparedStatement ps = conn.prepareStatement("DELETE FROM SUPPLIER WHERE CIF_supp = ?");
        ps.setString(1, CIF_supp);

        int i = ps.executeUpdate();
        if (i != 0) {
            shouldRedirect = true;
        } else {
            out.print("Something went wrong");
        } 
        conn.close();
        ps.close();
    } else if ("modifySupplier".equals(action)) {
        String CIF_supp = request.getParameter("CIF_supp");
        String denomination = request.getParameter("denomination");
        String address = request.getParameter("address");
        String type = request.getParameter("type");

        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es", "x7766471", "x7766471");
        PreparedStatement ps = conn.prepareStatement("UPDATE SUPPLIER SET denomination = ?, address = ?, type = ? WHERE CIF_supp = ?");
        ps.setString(1, denomination);
        ps.setString(2, address);
        ps.setString(3, type);
        ps.setString(4, CIF_supp);

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
