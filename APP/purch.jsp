<%@ page import="java.sql.*"%>

<%
boolean shouldRedirect = false;
try {
    String action = request.getParameter("action");
   

    if ("addPurchase".equals(action)) {
        int price = Integer.parseInt(request.getParameter("price"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        int material_id = Integer.parseInt(request.getParameter("material_id"));
        String CIF_supp = request.getParameter("CIF_supp");

        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es", "x7766471", "x7766471");
        PreparedStatement ps = conn.prepareStatement("INSERT INTO PURCHASE (price, quantity, material_id, CIF_supp) VALUES (?, ?, ?, ?)");
        ps.setInt(1, price);
        ps.setInt(2, quantity);
        ps.setInt(3, material_id);
        ps.setString(4, CIF_supp);

        int x = ps.executeUpdate();
        if (x != 0) {
            shouldRedirect = true;
        } else {
            out.print("Something went wrong");
        } 
        conn.close();
        ps.close();
    } else if ("listPurchase".equals(action)) {
        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es", "x7766471", "x7766471");
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM PURCHASE");
        ResultSet rs = ps.executeQuery();

        out.print("<table border='1'>");
        out.print("<tr><th>Purchase ID</th><th>Price</th><th>Quantity</th><th>Material ID</th><th>CIF Supplier</th></tr>");

        while (rs.next()) {
            out.print("<tr>");
            out.print("<td>" + rs.getString("purchase_id") + "</td>");
            out.print("<td>" + rs.getInt("price") + "</td>");
            out.print("<td>" + rs.getInt("quantity") + "</td>");
            out.print("<td>" + rs.getInt("material_id") + "</td>");
            out.print("<td>" + rs.getString("CIF_supp") + "</td>");
            out.print("</tr>");
        }
        out.print("</table>");
        conn.close();
        ps.close();
    } else if ("deletePurchase".equals(action)) {
        String purchase_id = request.getParameter("purchase_id");

        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es", "x7766471", "x7766471");
        PreparedStatement ps = conn.prepareStatement("DELETE FROM PURCHASE WHERE purchase_id = ?");
        ps.setString(1, purchase_id);

        int i = ps.executeUpdate();
        if (i != 0) {
            shouldRedirect = true;
        } else {
            out.print("Something went wrong");
        } 
        conn.close();
        ps.close();
    } else if ("modifyPurchase".equals(action)) {
        String purchase_id = request.getParameter("purchase_id");
        int price = Integer.parseInt(request.getParameter("price"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        int material_id = Integer.parseInt(request.getParameter("material_id"));
        String CIF_supp = request.getParameter("CIF_supp");

        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es", "x7766471", "x7766471");
        PreparedStatement ps = conn.prepareStatement("UPDATE PURCHASE SET price = ?, quantity = ?, material_id = ?, CIF_supp = ? WHERE purchase_id = ?");
        ps.setInt(1, price);
        ps.setInt(2, quantity);
        ps.setInt(3, material_id);
        ps.setString(4, CIF_supp);
        ps.setString(5, purchase_id);

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
