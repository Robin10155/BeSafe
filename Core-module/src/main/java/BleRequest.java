/**
 * Created by robin on 13/5/16.
 */
import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.io.spi.SpiFactory;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.util.*;

public class BleRequest{

    private ConnIdReaderTimer timer;
    // SPI device
    public static SpiDevice spi = null;
    public BleRequest() {
        try {
            spi = SpiFactory.getInstance(SpiChannel.CS1,
                    50000,
                    SpiDevice.DEFAULT_SPI_MODE); // default spi mode 0
        } catch (IOException e) {
            e.printStackTrace();
        }
        timer=new ConnIdReaderTimer();
        connectionIdFoundListeners = new ArrayList<ConnectionIdFoundListener>();
    }
    public List<ConnectionIdFoundListener> connectionIdFoundListeners;
    private void onConnIdFound(long connId){
        Thread thread = new Thread() {
            @Override
            public void run() {
                for (ConnectionIdFoundListener connectionIdFoundListener:connectionIdFoundListeners)
                    connectionIdFoundListener.onConnIDFound(BleRequest.this,connId);
            }
        };
        thread.start();
    }
    public interface ConnectionIdFoundListener extends EventListener{
        void onConnIDFound(BleRequest bleRequest,long connID);
    }
    public void addConnectionIdFoundListener(ConnectionIdFoundListener connectionIdFoundListener){
        connectionIdFoundListeners.add(connectionIdFoundListener);
    }
    public void removeConnectionIdFoundListener(ConnectionIdFoundListener connectionIdFoundListener){
        connectionIdFoundListeners.remove(connectionIdFoundListener);
    }
    private class ConnIdReaderTimer extends TimerTask{
        private byte[] dataBuffer=new byte[6];
        private boolean connIdFound;
        private long connId;
        private Timer timer;
        public ConnIdReaderTimer() {
            timer=new Timer();
            timer.schedule(this,100,100);
        }
        private boolean canceled=false;
        @Override
        public void run() {
            byte[] dataRead = null;
            do{
                try {
                    dataRead=spi.write(dataBuffer,0,6);
                    if((dataRead[0]==(byte)0xF0)&&(dataRead[5]==(byte)0x0F)){
                        connId=((dataRead[1]<<24)&0xFF000000L)|
                                ((dataRead[2]<<16)&0x00FF0000L)|
                                ((dataRead[3]<<8)&0x0000FF00L)|
                                ((dataRead[4])&0x000000FFL);
                        onConnIdFound(connId);
                        connIdFound=true;
                    }else{
                        connIdFound=false;
                    }
//                    if(dataRead[0]==(byte)0x00&&dataRead[5]==(byte)0x00)
//                        System.out.println("SPI not working");
                } catch (IOException e) {
                    e.printStackTrace();
                    connIdFound=false;
                }
                if(connIdFound) {
                    timer.cancel();
                    canceled=true;
                }
            }while (connIdFound);
            if(canceled)
                new ConnIdReaderTimer();
        }
    }
}