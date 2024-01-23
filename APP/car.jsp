<%@ page import="java.sql.*"%>

<%
boolean shouldRedirect = false;
try {
    String action = request.getParameter("action");
    

    if ("addCar".equals(action)) {
        // Add Car
        String serial_n = request.getParameter("serial_n");
        String name = request.getParameter("name");
        int power = Integer.parseInt(request.getParameter("power"));
        int engine_disp = Integer.parseInt(request.getParameter("engine_disp"));

        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es", "x7766471", "x7766471");
        PreparedStatement ps = conn.prepareStatement("INSERT INTO CAR (serial_n, name, power, engine_disp) VALUES (?, ?, ?, ?)");
        ps.setString(1, serial_n);
        ps.setString(2, name);
        ps.setInt(3, power);
        ps.setInt(4, engine_disp);

        int i = ps.executeUpdate();
        if (i != 0) {
            shouldRedirect = true;
        } else {
            out.print("Something went wrong");
        } 
        conn.close();
        ps.close();
        
    } else if ("listCars".equals(action)) {
        // List Cars
        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es", "x7766471", "x7766471");
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM CAR");
        ResultSet rs = ps.executeQuery();

        out.print("<table border='1'>");
        out.print("<tr><th>Serial Number</th><th>Name</th><th>Power</th><th>Engine Displacement</th></tr>");

        while (rs.next()) {
            out.print("<tr>");
            out.print("<td>" + rs.getString("serial_n") + "</td>");
            out.print("<td>" + rs.getString("name") + "</td>");
            out.print("<td>" + rs.getInt("power") + "</td>");
            out.print("<td>" + rs.getInt("engine_disp") + "</td>");
            out.print("</tr>");
        }

        out.print("</table>");
        conn.close();
        ps.close();
    } else if ("deleteCar".equals(action)) {
        // Delete Car
        String serial_n = request.getParameter("serial_n");

        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es", "x7766471", "x7766471");
        PreparedStatement ps = conn.prepareStatement("DELETE FROM CAR WHERE serial_n = ?");
        ps.setString(1, serial_n);

        int i = ps.executeUpdate();
        if (i != 0) {
            shouldRedirect = true;
        } else {
            out.print("Something went wrong");
        } 
        conn.close();
        ps.close();
          
        
    } else if ("editCar".equals(action)) {
        // Edit Car
        String serial_n = request.getParameter("serial_n");
        String name = request.getParameter("name");
        int power = Integer.parseInt(request.getParameter("power"));
        int engine_disp = Integer.parseInt(request.getParameter("engine_disp"));

        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es", "x7766471", "x7766471");
        PreparedStatement ps = conn.prepareStatement("UPDATE CAR SET name = ?, power = ?, engine_disp = ? WHERE serial_n = ?");
        ps.setString(1, name);
        ps.setInt(2, power);
        ps.setInt(3, engine_disp);
        ps.setString(4, serial_n);

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
