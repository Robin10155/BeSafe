import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by robin on 4/5/16.
 */
public class GPSUpdateRequest implements Runnable {
    private final TinyGPS.Location location;
    private final byte[] gatewayid;
    public GPSUpdateRequest(TinyGPS.Location location, byte[] gatewayId) {
        this.location=location;
        this.gatewayid=gatewayId;
    }

    @Override
    public void run() {
        URL url;
        HttpURLConnection httpURLConnection = null;
        try {
            String urlString = "http://10.32.33.180:8080/location";
            String charset = "UTF-8";  // Or in Java 7 and later, use the constant: java.nio.charset.StandardCharsets.UTF_8.name()
            String param1 = location.toString();
            String param2 = "value2";
// ...

            String query = String.format("location=%s&cipher=%s",
                    URLEncoder.encode(location.toString(), charset),
                    URLEncoder.encode(DatatypeConverter.printHexBinary(CipherHandler.getInstance().encrypt(gatewayid)), charset));
            url = new URL(urlString + "?" + query);

            httpURLConnection = (HttpURLConnection) url.openConnection();
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                httpURLConnection.disconnect();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
