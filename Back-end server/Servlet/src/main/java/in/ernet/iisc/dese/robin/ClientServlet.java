package in.ernet.iisc.dese.robin;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

/**
 * Servlet implementation class ClientServlet
 */
@WebServlet(name = "ClientServlet",value="/client")
public class ClientServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;  
    /**
     * @see HttpServlet#HttpServlet()
     */
	public static final int TIMEOUT = 5000;
	public static final int TICK = 100;
	public static final int MAX_COUNT= TIMEOUT/TICK;
	public static final long DISCARD_TIME_OUT=TIMEOUT*60;
    public ClientServlet() {
        super();
    }
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    private Connection conn;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out=response.getWriter();
		try {
		// TODO Auto-generated method stub
		String connectionIdString =request.getParameter("connectionId");
		response.setContentType("application/json");
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/besafe","robin","Sonal_09");
			Statement stmt = conn.createStatement();
			String sql = "INSERT INTO ClientRequestData "+"(connectionId,requestedOn) VALUES (X'"+connectionIdString+"',NOW());";
			stmt.executeUpdate(sql);
			int count=0;
			Date now=new Date();
			String modules=null;
			outerLoop:do
			{
				sql = "SELECT id,requestedOn,gatewayId,modules FROM GatewayRequestData WHERE GatewayRequestData.connectionId = X'"+connectionIdString+"';";
				ResultSet rs=stmt.executeQuery(sql);
				while(rs.next()){
					if(now.getTime()-rs.getTimestamp("requestedOn").getTime()>DISCARD_TIME_OUT){
						sql = "DELETE FROM GatewayRequestData WHERE id='"+rs.getInt("id")+"';";
						conn.createStatement().executeUpdate(sql);
						continue;
					}
					Blob blob=rs.getBlob("gatewayId");
					modules=rs.getString("modules");
					returnGatewayDetails(out,blob.getBytes(1, (int) blob.length()),modules);
					sql = "DELETE FROM GatewayRequestData WHERE connectionId=X'"+connectionIdString+"';";
					stmt.executeUpdate(sql);
					sql = "DELETE FROM ClientRequestData WHERE connectionId=X'"+connectionIdString+"';";
					stmt.executeUpdate(sql);
					break outerLoop;
				}
				Thread.sleep(TICK);
				count++;
				if(count>=MAX_COUNT){
					sql = "DELETE FROM ClientRequestData WHERE connectionId=X'"+connectionIdString+"';";
					stmt.executeUpdate(sql);
					break;
				}
			}while(true);
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void returnGatewayDetails(PrintWriter out, byte[] bs, String modules) throws SQLException {
		Statement stmt=conn.createStatement();
		String sql= "SELECT Vehicles.vehicleNumber, Drivers.name, Drivers.sex, "+
					"Drivers.age,VehicleModels.modelNumber,VehicleBrands.brandName "+
					"FROM Vehicles,VehicleBrands,VehicleModels,Drivers,Gateways "+
					"WHERE Gateways.vehicleId = Vehicles.id AND Vehicles.modelId = VehicleModels.id "+
					"AND VehicleModels.brandId = VehicleBrands.id AND Vehicles.currentDriverId = Drivers.id "+
					"AND Gateways.id = X'"+DatatypeConverter.printHexBinary(bs)+"';";
		ResultSet rs=stmt.executeQuery(sql);
		if(rs.next()){
			JSONObject jsonObject=new JSONObject();
			jsonObject.put("DriverName",rs.getString("Drivers.name"));
			jsonObject.put("DriverSex",rs.getBoolean("Drivers.sex"));
			jsonObject.put("DriverAge",rs.getInt("Drivers.age"));
			jsonObject.put("VehicleNumber",rs.getString("Vehicles.vehicleNumber"));
			jsonObject.put("VehicleModel",rs.getString("VehicleModels.modelNumber"));
			jsonObject.put("VehicleBrand",rs.getString("VehicleBrands.brandName"));
			jsonObject.put("modules",new JSONArray(modules));
            out.write(jsonObject.toString());
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
