<%@ page import="java.sql.*"%>

<%
boolean shouldRedirect = false;
try {
    String action = request.getParameter("action");
    

    if ("addOrder".equals(action)) {
        int price = Integer.parseInt(request.getParameter("price"));
        String serial_n = request.getParameter("serial_n");
        String CIF_client = request.getParameter("CIF_client");

        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es", "x7766471", "x7766471");
        PreparedStatement ps = conn.prepareStatement("INSERT INTO ORDER_ (price, serial_n, CIF_client) VALUES (?, ?, ?)");
        ps.setInt(1, price);
        ps.setString(2, serial_n);
        ps.setString(3, CIF_client);

        int x = ps.executeUpdate();
        if (x != 0) {
            shouldRedirect = true;
        } else {
            out.print("Something went wrong");
        } 
        conn.close();
        ps.close();
    } else if ("listOrder".equals(action)) {
        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es", "x7766471", "x7766471");
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM ORDER_");
        ResultSet rs = ps.executeQuery();

        out.print("<table border='1'>");
        out.print("<tr><th>Order ID</th><th>Price</th><th>Serial Number</th><th>CIF Client</th></tr>");

        while (rs.next()) {
            out.print("<tr>");
            out.print("<td>" + rs.getString("order_id") + "</td>");
            out.print("<td>" + rs.getInt("price") + "</td>");
            out.print("<td>" + rs.getString("serial_n") + "</td>");
            out.print("<td>" + rs.getString("CIF_client") + "</td>");
            out.print("</tr>");
        }
        out.print("</table>");
        conn.close();
        ps.close();

    } else if ("deleteOrder".equals(action)) {
        String order_id = request.getParameter("order_id");

        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es", "x7766471", "x7766471");
        PreparedStatement ps = conn.prepareStatement("DELETE FROM ORDER_ WHERE order_id = ?");
        ps.setString(1, order_id);

        int i = ps.executeUpdate();
        if (i != 0) {
            shouldRedirect = true;
        } else {
            out.print("Something went wrong");
        } 
        conn.close();
        ps.close();
    } else if ("modifyOrder".equals(action)) {
        String order_id = request.getParameter("order_id");
        int price = Integer.parseInt(request.getParameter("price"));
        String serial_n = request.getParameter("serial_n");
        String CIF_client = request.getParameter("CIF_client");

        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es", "x7766471", "x7766471");
        PreparedStatement ps = conn.prepareStatement("UPDATE ORDER_ SET price = ?, serial_n = ?, CIF_client = ? WHERE order_id = ?");
        ps.setInt(1, price);
        ps.setString(2, serial_n);
        ps.setString(3, CIF_client);
        ps.setString(4, order_id);

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
