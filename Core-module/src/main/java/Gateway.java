import java.io.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Gateway {
	private byte[] gatewayID={
			68,82,85,8,
			98,69,39,6,
			53,41,113,125,
			46,0,36,85,
			40,33,99,73,
			89,122,82,57,
			31,15,101,26,
			29,44,54,30
	};
	private Gateway(){
		
	}
	public static Gateway getGateway(InputStream inputStream){
		Gateway gateway=new Gateway();
		StringBuilder stringBuilder;
		FileReader fileReader;
		String str;
		JSONArray idArray;
		BufferedReader bufferedReader;
		stringBuilder=new StringBuilder();
		JSONObject jsonObject;
		gateway=new Gateway();
		try {
			bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
			while((str=bufferedReader.readLine())!=null)
				stringBuilder.append(str);
			jsonObject=new JSONObject(stringBuilder.toString());
			idArray=jsonObject.getJSONArray("id");
			assert idArray.length()!=gateway.gatewayID.length;
			for(int i=0;i<idArray.length();i++){
				gateway.gatewayID[i]=(byte)idArray.getInt(i);
			}
			bufferedReader.close();
			return gateway;
		} catch (IOException | JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	public byte[] getGatewayID() {
		return gatewayID;
	}
}
