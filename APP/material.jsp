<%@ page import="java.sql.*"%>

<%
boolean shouldRedirect = false;
try {
    String action = request.getParameter("action");
    

    if ("addMaterial".equals(action)) {
        String name = request.getParameter("name");
        int length = Integer.parseInt(request.getParameter("length"));
        int height = Integer.parseInt(request.getParameter("height"));
        int width = Integer.parseInt(request.getParameter("width"));
        int weight = Integer.parseInt(request.getParameter("weight"));

        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es", "x7766471", "x7766471");
        PreparedStatement ps = conn.prepareStatement("INSERT INTO MATERIAL (name, length, height, width, weight) VALUES (?, ?, ?, ?, ?)");
        ps.setString(1, name);
        ps.setInt(2, length);
        ps.setInt(3, height);
        ps.setInt(4, width);
        ps.setInt(5, weight);

        int x = ps.executeUpdate();
        if (x != 0) {
            shouldRedirect = true;
        } else {
            out.print("Something went wrong");
        } 
        conn.close();
        ps.close();
    } else if ("listMaterial".equals(action)) {
        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es", "x7766471", "x7766471");
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM MATERIAL");
        ResultSet rs = ps.executeQuery();

        out.print("<table border='1'>");
        out.print("<tr><th>Material ID</th><th>Name</th><th>Length</th><th>Height</th><th>Width</th><th>Weight</th></tr>");

        while (rs.next()) {
            out.print("<tr>");
            out.print("<td>" + rs.getString("material_id") + "</td>");
            out.print("<td>" + rs.getString("name") + "</td>");
            out.print("<td>" + rs.getInt("length") + "</td>");
            out.print("<td>" + rs.getInt("height") + "</td>");
            out.print("<td>" + rs.getInt("width") + "</td>");
            out.print("<td>" + rs.getInt("weight") + "</td>");
            out.print("</tr>");
        }
        out.print("</table>");
        conn.close();
        ps.close();
    } else if ("deleteMaterial".equals(action)) {
        String material_id = request.getParameter("material_id");

        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es", "x7766471", "x7766471");
        PreparedStatement ps = conn.prepareStatement("DELETE FROM MATERIAL WHERE material_id = ?");
        ps.setString(1, material_id);

        int i = ps.executeUpdate();
        if (i != 0) {
            shouldRedirect = true;
        } else {
            out.print("Something went wrong");
        } 
        conn.close();
        ps.close();
    } else if ("modifyMaterial".equals(action)) {
        String material_id = request.getParameter("material_id");
        String name = request.getParameter("name");
        int length = Integer.parseInt(request.getParameter("length"));
        int height = Integer.parseInt(request.getParameter("height"));
        int width = Integer.parseInt(request.getParameter("width"));
        int weight = Integer.parseInt(request.getParameter("weight"));

        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es", "x7766471", "x7766471");
        PreparedStatement ps = conn.prepareStatement("UPDATE MATERIAL SET name = ?, length = ?, height = ?, width = ?, weight = ? WHERE material_id = ?");
        ps.setString(1, name);
        ps.setInt(2, length);
        ps.setInt(3, height);
        ps.setInt(4, width);
        ps.setInt(5, weight);
        ps.setString(6, material_id);

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

