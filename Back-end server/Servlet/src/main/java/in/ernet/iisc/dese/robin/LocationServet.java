package in.ernet.iisc.dese.robin;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by robin on 6/5/16.
 */
@WebServlet(name = "LocationServet",value = "/location")
public class LocationServet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String cipher=request.getParameter("cipher");
        String location=request.getParameter("location");
        String sql;
        Statement stmt = null;
        byte[] deCipherCode=CipherHandler.getInstance().decrypt(DatatypeConverter.parseHexBinary(cipher));
        String gatewayId=DatatypeConverter.printHexBinary(deCipherCode);
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/besafe","robin","Sonal_09");
            stmt=conn.createStatement();
            sql="INSERT INTO Locations "+"(timestamp,location,gatewayId) VALUES (NOW(),'"+location+"',X'"+gatewayId+"')";
            stmt.executeUpdate(sql);
            System.out.print(gatewayId);
            conn.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }
}
