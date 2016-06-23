import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by robin on 22/5/16.
 */
public class StreamData implements Runnable{
    private String fileName;
    private GatewayLedStatus gatewayLedStatus;
    private boolean validating;
    public StreamData(String fileName, GatewayLedStatus gatewayLedStatus, MicrophoneValidation microphoneValidation) {
        this.fileName = fileName;
        this.gatewayLedStatus=gatewayLedStatus;
        microphoneValidation.addValidationStartListener(microphoneValidation1 -> validating=true);
        microphoneValidation.addValidationCompleteListener((moduleValidation, result) -> {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    validating = false;
                }
            },2000);
        });
    }
    public void start(){
        new Thread(this).start();
    }
    @Override
    public void run() {
        Socket clientSocket = null;
        byte[] dataArr=new byte[1024];
        BufferedInputStream bufferedInputStream;
        int len;
        try {
            FileInputStream fileInputStream=new FileInputStream(fileName);
            VorbisInputStream vorbisInputStream=new VorbisInputStream(fileInputStream);
            bufferedInputStream = new BufferedInputStream(vorbisInputStream);
            System.out.println("ready to write");
            clientSocket = new Socket("192.168.0.167", 3490);
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            while((len=bufferedInputStream.read(dataArr,0,1024))!=-1){
                if(!validating)
                    gatewayLedStatus.setStatus(GatewayLedStatus.Status.EMERGENCY);
                outToServer.write(dataArr,0,len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if(clientSocket!=null)
                clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
