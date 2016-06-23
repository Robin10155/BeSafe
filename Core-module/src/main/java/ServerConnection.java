import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.xml.bind.DatatypeConverter;
import java.util.List;

/**
 * Created by robin on 14/5/16.
 */
public class ServerConnection {
    public static final String DEFAULT_MQTT_HOST="192.168.0.167";
    public static final int DEFAULT_MQTT_PORT=1883;
    private MqttClient client;
    private MemoryPersistence persistence;
    private TinyGPS.Location location;
    private ModuleValidation [] moduleList;
    private byte[] cipherCode;
    public ServerConnection(String host, int port, TinyGPS.Location location,ModuleValidation [] moduleList,byte[] cipherCode) {
        this.cipherCode=cipherCode;
        this.location=location;
        this.moduleList=moduleList;
        persistence = new MemoryPersistence();
        try {
            client = new MqttClient("tcp://"+host+":"+port,"Gateway",persistence);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        connect();
    }
    private void connect(){
        try {

            MqttConnectOptions connectOptions=new MqttConnectOptions();
            connectOptions.setCleanSession(true);
            connectOptions.setWill("/gateway/will",getWillMessage(location,moduleList,cipherCode).getBytes(),2,true);
            client.setCallback(new ServerMqttCallback());
            client.connect(connectOptions);
            client.publish("/gateway/connect",getWillMessage(location,moduleList,cipherCode).getBytes(),2,true);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public ServerConnection(TinyGPS.Location location,ModuleValidation [] moduleList,byte[] cipherCode) {
        this(DEFAULT_MQTT_HOST,DEFAULT_MQTT_PORT,location,moduleList,cipherCode);
    }

    public String getWillMessage(TinyGPS.Location location, ModuleValidation[] moduleList,byte[] cipherCode){
        JSONObject willObject=new JSONObject();
        willObject.put("time",System.currentTimeMillis());
        willObject.put("cipherCode", DatatypeConverter.printHexBinary(cipherCode));
        if(location.isValid())
            willObject.put("location",location.toString());
        else
            willObject.put("location","Unknown");
        JSONObject modulesObject;
        JSONArray modulesArray=new JSONArray();
        willObject.put("modules",modulesArray);
        for (ModuleValidation moduleValidation:moduleList) {
            modulesObject=new JSONObject();
            modulesArray.put(modulesObject);
            modulesObject.put("name",moduleValidation.getModuleName());
            modulesObject.put("result",moduleValidation.getLastValidResult());
        }
        return willObject.toString();
    }

    public void sendValidationMessage(long connID, byte[] cipherCode, ModuleValidation[] moduleValidationList) {
        JSONObject validationObject=new JSONObject();
        validationObject.put("connId",Long.toHexString(connID));
        validationObject.put("cipherCode", DatatypeConverter.printHexBinary(cipherCode));
        validationObject.put("time",System.currentTimeMillis());
        JSONObject modulesObject;
        JSONArray modulesArray=new JSONArray();
        validationObject.put("modules",modulesArray);
        for (ModuleValidation moduleValidation:moduleValidationList) {
            modulesObject=new JSONObject();
            modulesArray.put(modulesObject);
            modulesObject.put("name",moduleValidation.getModuleName());
            modulesObject.put("result",moduleValidation.getLastValidResult());
        }
        try {
            client.publish("/gateway/validation",validationObject.toString().getBytes(),2,true);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    public void sendEmergencyRequestMessage(TinyGPS.Location location,ModuleValidation[] moduleValidationList,byte[] cipherCode){
        JSONObject emergencyRequestObject=new JSONObject();
        emergencyRequestObject.put("cipherCode", DatatypeConverter.printHexBinary(cipherCode));
        emergencyRequestObject.put("time",System.currentTimeMillis());
        emergencyRequestObject.put("location",location.toString());
        JSONObject modulesObject;
        JSONArray modulesArray=new JSONArray();
        emergencyRequestObject.put("modules",modulesArray);
        for (ModuleValidation moduleValidation:moduleValidationList) {
            modulesObject=new JSONObject();
            modulesArray.put(modulesObject);
            modulesObject.put("name",moduleValidation.getModuleName());
            modulesObject.put("result",moduleValidation.getLastValidResult());
        }
        try {
            client.publish("/gateway/panicEmergency",emergencyRequestObject.toString().getBytes(),2,true);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void sendLocationMessage(TinyGPS.Location location,byte[] cipherCode) {
        JSONObject locationObject=new JSONObject();
        locationObject.put("location",location.toString());
        locationObject.put("cipherCode", DatatypeConverter.printHexBinary(cipherCode));
        try{
            client.publish("/gateway/location",locationObject.toString().getBytes(),2,true);
        }catch (MqttException e){
            e.printStackTrace();
        }
    }

    private class ServerMqttCallback implements MqttCallback {

        @Override
        public void connectionLost(Throwable cause) {
            System.out.println(cause);
            ServerConnection.this.connect();
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {

        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

        }
    }
}
