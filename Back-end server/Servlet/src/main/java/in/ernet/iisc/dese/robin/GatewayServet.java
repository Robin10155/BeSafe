package in.ernet.iisc.dese.robin;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.plaf.synth.SynthSeparatorUI;
import javax.xml.bind.DatatypeConverter;

/**
 * Servlet implementation class GatewayServet
 */
@WebServlet(name = "GatewayServet",value="/gateway")
public class GatewayServet extends HttpServlet {
	private static final long serialVersionUID = 1L;  
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GatewayServet() {
        super();
    }
    

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String cipher=request.getParameter("cipher");
		String sql;
		int connectionId=Integer.parseInt(request.getParameter("connectionId"),16);
		System.out.println("ConnectoinId: "+Integer.toString(connectionId,16));
		byte connectionIds[]=new byte[]{
				(byte)(connectionId&(0x000000FF)),
				(byte)((connectionId&(0x0000FF00))>>8),
				(byte)((connectionId&(0x00FF0000))>>16),
				(byte)((connectionId&(0xFF000000))>>24),
			};
		byte[] deCipherCode=CipherHandler.getInstance().decrypt(DatatypeConverter.parseHexBinary(cipher));
		response.setContentType("text/plain");
		PrintWriter out=response.getWriter();
		for(int i=0;i<32;i++){
			deCipherCode[i]=(byte)(deCipherCode[i]^connectionIds[i%4]);
		}
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/besafe","robin","Sonal_09");
			Statement stmt = conn.createStatement();
			sql = "DELETE FROM GatewayRequestData WHERE connectionId='"+connectionId+"';";
			stmt.executeUpdate(sql);
			String gatewayId=DatatypeConverter.printHexBinary(deCipherCode);
			out.println(gatewayId);
			sql = "INSERT INTO GatewayRequestData "+"(gatewayId,connectionId,requestedOn) VALUES (X'"+gatewayId+"','"+connectionId+"',NOW());";
			stmt.executeUpdate(sql);
			conn.close();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
