import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONException;
import org.json.JSONObject;

import javax.xml.bind.DatatypeConverter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Created by robin on 22/5/16.
 */
public class GatewayConnection implements Runnable{
    public static final String DEFAULT_MQTT_HOST="127.0.0.1";
    public static final int DEFAULT_MQTT_PORT=1883;
    private MqttClient client;
    private MemoryPersistence persistence;
    public GatewayConnection(String host, int port) {
        persistence = new MemoryPersistence();
        try {
            client = new MqttClient("tcp://"+host+":"+port,"Server",persistence);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        connect();
        try {
            client.subscribe("/gateway/will",2);
            client.subscribe("/gateway/validation",2);
            client.subscribe("/gateway/panicEmergency",2);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    public GatewayConnection(){
        this(DEFAULT_MQTT_HOST,DEFAULT_MQTT_PORT);
    }

    @Override
    public void run() {

    }
    private void connect(){
        try {

            MqttConnectOptions connectOptions=new MqttConnectOptions();
            connectOptions.setCleanSession(true);
            client.setCallback(new GatewayMqttCallback());
            client.connect(connectOptions);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    private class GatewayMqttCallback implements MqttCallback {

        @Override
        public void connectionLost(Throwable cause) {
            System.out.println(cause);
            GatewayConnection.this.connect();
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            System.out.println(new String(message.getPayload()));
            switch (topic){
                case "/gateway/will":
                    new Thread(() -> {
                       handleWill(new String(message.getPayload()));
                    }).start();
                    break;
                case "/gateway/validation":
                    new Thread(() -> {
                        handleValidation(new String(message.getPayload()));
                    }).start();
                    break;
                case "/gateway/panicEmergency":
                    new Thread(() -> {
                            handleEmergency(new String(message.getPayload()));
                    }).start();
                    break;
                case "/gateway/location":
                    new Thread(() -> {
                       handleLocation(new String(message.getPayload()));
                    }).start();
                    break;
                case "/gateway/connect":
                    new Thread(() -> {
                        handleConnect(new String(message.getPayload()));
                    }).start();
                default:
                    break;
            }
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

        }
    }

    private void handleConnect(String json) {
        System.out.println("handle Connect");
        JSONObject message=new JSONObject(json);
        try {
            String cipher=message.getString("cipherCode");
            String location=message.getString("location");
            String modules=message.getJSONArray("modules").toString();
            String sql;
            Statement stmt = null;
            byte[] deCipherCode=CipherHandler.getInstance().decrypt(DatatypeConverter.parseHexBinary(cipher));
            String gatewayId=DatatypeConverter.printHexBinary(deCipherCode);

            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/besafe","robin","Sonal_09");
            stmt=conn.createStatement();
            sql="INSERT INTO Gateways "+"(id,isAlive,location,modules) VALUES (b'"+1+"',X'"+gatewayId+"','"+location+"',"+modules+")";
            stmt.executeUpdate(sql);
            conn.close();
        } catch (ClassNotFoundException | SQLException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void handleWill(String json) {
        System.out.println("handle Will");
        JSONObject message=new JSONObject(json);
        try {
            String cipher=message.getString("cipherCode");
            String location= message.getString("location");
            String modules=message.getJSONArray("modules").toString();
            String sql;
            Statement stmt = null;
            byte[] deCipherCode=CipherHandler.getInstance().decrypt(DatatypeConverter.parseHexBinary(cipher));
            String gatewayId=DatatypeConverter.printHexBinary(deCipherCode);

            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/besafe","robin","Sonal_09");
            stmt=conn.createStatement();
            sql="UPDATE Gateways SET "+"isAlive = b'"+0+"',location = '"+location+"',modules = '"+modules+"' WHERE id = X'"+gatewayId+"'";
            stmt.executeUpdate(sql);
            conn.close();
        } catch (ClassNotFoundException | SQLException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void handleEmergency(String json) {
        System.out.println("handle Emergency");
        JSONObject message=new JSONObject(json);
        try {
            String cipher=message.getString("cipherCode");
            String location= message.getString("location");
            String sql;
            Statement stmt = null;
            byte[] deCipherCode=CipherHandler.getInstance().decrypt(DatatypeConverter.parseHexBinary(cipher));
            String gatewayId=DatatypeConverter.printHexBinary(deCipherCode);
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/besafe","robin","Sonal_09");
            stmt=conn.createStatement();
            sql="INSERT INTO Emergency "+"(timestamp,locationAtRequest,gatewayId) VALUES (NOW(),'"+location+"',X'"+gatewayId+"')";
            stmt.executeUpdate(sql);
            System.out.print(gatewayId);
            stmt=conn.createStatement();
            sql="UPDATE Gateways SET location = '"+location+"' WHERE id = X'"+gatewayId+"'";
            stmt.executeUpdate(sql);
            conn.close();
        } catch (ClassNotFoundException | SQLException | JSONException e) {
            e.printStackTrace();
        }
    }
    private void handleValidation(String json){
        System.out.println("handle Validation");
        JSONObject message=new JSONObject(json);
        try {
            String cipher=message.getString("cipherCode");
            String modules=message.getJSONArray("modules").toString();
            String sql;
            long connectionId=Long.parseLong(message.getString("connId"),16);
            System.out.println("ConnectoinId: "+Long.toString(connectionId,16));
            byte connectionIds[]=new byte[]{
                    (byte)(connectionId&(0x000000FF)),
                    (byte)((connectionId&(0x0000FF00))>>8),
                    (byte)((connectionId&(0x00FF0000))>>16),
                    (byte)((connectionId&(0xFF000000))>>24),
            };
            byte[] deCipherCode=CipherHandler.getInstance().decrypt(DatatypeConverter.parseHexBinary(cipher));
            for(int i=0;i<32;i++){
                deCipherCode[i]=(byte)(deCipherCode[i]^connectionIds[i%4]);
            }
            String connectionIdString=Long.toString(connectionId,16);
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/besafe","robin","Sonal_09");
            Statement stmt = conn.createStatement();
            sql = "DELETE FROM GatewayRequestData WHERE connectionId=X'"+connectionIdString+"';";
            stmt.executeUpdate(sql);
            String gatewayId=DatatypeConverter.printHexBinary(deCipherCode);
            sql = "INSERT INTO GatewayRequestData "+"(gatewayId,connectionId,requestedOn,modules) VALUES (X'"+gatewayId+"',X'"+connectionIdString+"',NOW(),'"+modules+"');";
            stmt.executeUpdate(sql);
            conn.close();
        } catch (ClassNotFoundException | SQLException | JSONException e) {
            e.printStackTrace();
        }
    }
    private void handleLocation(String json){
        System.out.println("handle location");
        JSONObject message=new JSONObject(json);
        try {
            String cipher=message.getString("cipherCode");
            String location=message.getJSONArray("location").toString();
            String sql;
            Statement stmt = null;
            byte[] deCipherCode=CipherHandler.getInstance().decrypt(DatatypeConverter.parseHexBinary(cipher));
            String gatewayId=DatatypeConverter.printHexBinary(deCipherCode);
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/besafe","robin","Sonal_09");
            stmt=conn.createStatement();
            sql="INSERT INTO Locations "+"(timestamp,location,gatewayId) VALUES (NOW(),'"+location+"',X'"+gatewayId+"')";
            stmt.executeUpdate(sql);
            stmt=conn.createStatement();
            sql="UPDATE Gateways SET location = '"+location+"' WHERE id = X'"+gatewayId+"'";
            stmt.executeUpdate(sql);
            conn.close();
        } catch (ClassNotFoundException | SQLException | JSONException e) {
            e.printStackTrace();
        }
    }
}
