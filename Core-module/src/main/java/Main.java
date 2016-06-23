import com.pi4j.io.gpio.RaspiPin;
import com.sun.org.apache.xpath.internal.SourceTree;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by robin on 10/5/16.
 */
public class Main {
    public static final int SAMPLES_RATE = 44100;
    private String filename="/home/robin/fft.txt";
    private String datafile="/home/robin/mic.raw";
    private static SpeakerTone speakerTone;
    private static MicrophoneFFT microphoneFFT;
    private MyFrame frame;
    private Queue<Long> connIdQueue=new LinkedList<Long>();

    public Main() {

    }

    public static void main(String[] args){
        new Main().run();
    }
    private double[] timeSeries=new double[100];
    private double[] correlationSeries=new double[100];
    private long fftCapured=0;
    private boolean passed=false;
    ModuleValidation moduleValidationList[]=new ModuleValidation[4];

    public static final int BUTTON_VALIDATION_SKIP_TIME=1000;
    private TinyGPS tinyGPS;
    private synchronized void onValidationComplete(ModuleValidation validation, ModuleValidation.ValidationResult result){
        boolean processQueue=true;
        ModuleValidation.ValidationResult validationResult;
        /*for(ModuleValidation moduleValidation:moduleValidationList){
            validationResult=moduleValidation.getValidationResult();
            if(validationResult == ModuleValidation.ValidationResult.INVALID)
            {
                System.out.println("Starting revalidation sequence for "+validation.getModuleName());
                processQueue=false;
                moduleValidation.validate();
            }
            if(validationResult == ModuleValidation.ValidationResult.VALIDATING)
                processQueue=false;
        }*/
        for (int i = 0; i < moduleValidationList.length; i++) {
            System.out.println(moduleValidationList[i]+" "+validation+" "+moduleValidationList[i].getModuleName());
            validationResult=moduleValidationList[i].getValidationResult();
            if(validationResult == ModuleValidation.ValidationResult.INVALID)
            {
                System.out.println("Starting revalidation sequence for "+validation.getModuleName());
                processQueue=false;
                moduleValidationList[i].validate();
            }
            if(validationResult == ModuleValidation.ValidationResult.VALIDATING)
                processQueue=false;
        }
        if(result== ModuleValidation.ValidationResult.FAILED)
            gatewayLedStatus.setStatus(GatewayLedStatus.Status.VALIDATION_FAILED);
        else{
            boolean valid=true;
            for(ModuleValidation moduleValidation:moduleValidationList)
            {
                if(moduleValidation.getLastValidResult()== ModuleValidation.ValidationResult.FAILED)
                    valid=false;
            }
            if(valid)
                gatewayLedStatus.setStatus(GatewayLedStatus.Status.NORMAL);
        }
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(3000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                System.out.println(validation.getModuleName()+" "+result);
//                validation.validate(true);
//            }
//        }).start();
        if(processQueue)
            processConnectionQueue(moduleValidationList);
    }
    private synchronized void processConnectionQueue(ModuleValidation[] moduleValidationList){
        for (ModuleValidation moduleValidation: moduleValidationList) {
            System.out.println(moduleValidation.getModuleName()+" state "+moduleValidation.getLastValidResult());
        }
        while(connIdQueue.size()!=0){
            Long connID=connIdQueue.remove();
            System.out.println("Processing queue");
            System.out.println("Valid ID "+Long.toHexString(connID));
            byte connIDs[]=new byte[]{
                    (byte)(connID&(0x000000FF)),
                    (byte)((connID&(0x0000FF00))>>8),
                    (byte)((connID&(0x00FF0000))>>16),
                    (byte)((connID&(0xFF000000))>>24),
            };
            byte[] code=new byte[32];
            for(int i=0;i<32;i++){
                code[i]=(byte)(gateway.getGatewayID()[i]^connIDs[i%4]);
            }
            byte[] cipherCode=CipherHandler.getInstance().encrypt(code);
            connection.sendValidationMessage(connID,cipherCode, moduleValidationList);
        }
    }
    private ServerConnection connection;
    private MicrophoneValidation microphoneValidation;
    private PanicButtonValidation panicButtonValidation;
    private BoxValidation boxValidation;
    private FirmwareValidation firmwareValidation;
    private boolean dateUpdated=false;
    private static final String AUDIO_PIPE_FILENAME = "/home/pi/audioPipe";
    private Gateway gateway;
    private byte[] cipherCode;
    private GatewayLedStatus gatewayLedStatus;
    private void run() {
        firmwareValidation=new FirmwareValidation();
        gatewayLedStatus=new GatewayLedStatus();
        boxValidation=new BoxValidation();
        boxValidation.addValidationCompleteListener((moduleValidation, result) -> {
//            if(result== ModuleValidation.ValidationResult.FAILED)
//                gatewayLedStatus.setStatus(GatewayLedStatus.Status.VALIDATION_FAILED);
        });
        boxValidation.start();

        gateway=Gateway.getGateway(getClass().getResourceAsStream("/gateway.json"));
        cipherCode=CipherHandler.getInstance().encrypt(gateway.getGatewayID());
        SplitInputStream splitInputStream = null;
        try {
            BufferedOutputStream bufferedOutputStream=new BufferedOutputStream(new FileOutputStream("/home/pi/audioPipe.fifo"));
            splitInputStream=new SplitInputStream(new MicrophoneInputStream(),bufferedOutputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        panicButtonValidation = new PanicButtonValidation(RaspiPin.GPIO_06,RaspiPin.GPIO_00);

        microphoneValidation = new MicrophoneValidation(splitInputStream);
        StreamData streamData=new StreamData("/home/pi/outputPipe.fifo",gatewayLedStatus,microphoneValidation);
        streamData.start();
        moduleValidationList[0]=(panicButtonValidation);
        moduleValidationList[1]=(microphoneValidation);
        moduleValidationList[2]=(boxValidation);
        moduleValidationList[3]=firmwareValidation;
        ModuleValidation.ValidationCompleteListener validationCompleteListener= this::onValidationComplete;
        microphoneValidation.addValidationCompleteListener(validationCompleteListener);
        panicButtonValidation.addValidationCompleteListener(validationCompleteListener);

        tinyGPS=new TinyGPS();
        for(ModuleValidation moduleValidation:moduleValidationList){
            moduleValidation.validate();
        }
        connection = new ServerConnection(tinyGPS.getLocation(),moduleValidationList,cipherCode);
        panicButtonValidation.addRequestEmergencyListener(button -> {
            System.out.println("Emergency requested");
            connection.sendEmergencyRequestMessage(tinyGPS.getLastKnownGoodLocation(),moduleValidationList,cipherCode);
            gatewayLedStatus.setStatus(GatewayLedStatus.Status.EMERGENCY);
        });

        BleRequest bleRequest=new BleRequest();
        bleRequest.addConnectionIdFoundListener((bleRequest1, connID) -> {
            System.out.println("Connection ID found "+Long.toHexString(connID));
            boolean validationRequired=true;
            connIdQueue.add(connID);
            int i=0;
            ModuleValidation.ValidationResult result;
            for(ModuleValidation moduleValidation:moduleValidationList){
                result=moduleValidation.validate();
                if(result == ModuleValidation.ValidationResult.VALIDATING||result == ModuleValidation.ValidationResult.INVALID)
                    validationRequired=false;
                i++;
            }
            if(validationRequired)
                processConnectionQueue(moduleValidationList);
        });

        tinyGPS.addDateUpdatedListener((gps, date) -> {
            if(!dateUpdated) {
                SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz y");
                formatter.setTimeZone(TimeZone.getTimeZone("IST"));
                String cmd = "date -s \"" + formatter.format(date) + "\"";
                try {
                    Runtime r=Runtime.getRuntime();
                    Process p = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", cmd});
                    p.waitFor();
                    InputStream er=p.getErrorStream();
                    BufferedReader br=new BufferedReader(new InputStreamReader(er));
                    String line;
                    while((line=br.readLine())!=null)
                        System.out.println(line);
                    br=new BufferedReader(new InputStreamReader(p.getInputStream()));
                    while((line=br.readLine())!=null)
                        System.out.println(line);
                } catch (IOException | InterruptedException e1) {
                    e1.printStackTrace();
                }
                dateUpdated=true;
            }
        });
        tinyGPS.addLocationUpdatedListener((gps,location) -> {
            connection.sendLocationMessage(location,cipherCode);
        });

    }

}
