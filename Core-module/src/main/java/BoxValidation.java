import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.io.spi.SpiFactory;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;

/**
 * Created by robin on 25/5/16.
 */
public class BoxValidation extends ModuleValidation{

    public BoxValidation(){
        setMaxTrailsAllowed(1);
        setValidationSkipTime(-1);
        setModuleName("Box");

    }
    public void start(){
        new Thread(new BoxValidationThread()).start();
    }

    @Override
    public void run() {

    }
    private class  BoxValidationThread implements Runnable {
        private byte[] dataRead;
        byte[] dataBuffer=new byte[4];
        private int v[]=new int[2];
        byte[] decryptedData=new byte[4];
        private int k[]={0xD2A7, 0xFA9D, 0xA887, 0x293F};
        private SpiDevice spi;
        public BoxValidationThread(){
            try {
                spi = SpiFactory.getInstance(SpiChannel.CS0,
                        1000,
                        SpiDevice.DEFAULT_SPI_MODE); // default spi mode 0
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void run() {
            ValidationResult validationResult;
            while (true){
                validationResult=ValidationResult.PASSED;
                for (int i = 0; i < dataBuffer.length; i++) {
                    dataBuffer[i]= (byte)(Math.random()*256);
                }
                try {
                    dataRead=spi.write(dataBuffer,0,dataBuffer.length);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    dataRead=spi.write(dataBuffer,0,dataBuffer.length);
                    v[0]= ((dataBuffer[0]&0xFF)|((((short)dataBuffer[1])<<8)&0xFF00)) &0xFFFF;
                    v[1]= ((dataBuffer[2]&0xFF)|((((short)dataBuffer[3])<<8)&0xFF00)) &0xFFFF;
                    encrypt(v,k);
                    decryptedData[0]= (byte) (v[0]&0xFF);
                    decryptedData[1]= (byte) ((v[0]>>8)&0xFF);
                    decryptedData[2]= (byte) (v[1]&0xFF);
                    decryptedData[3]= (byte) ((v[1]>>8)&0xFF);
                    for(int i=0;i<decryptedData.length;i++)
                    {
                        if(decryptedData[i]!=dataRead[i]){
                            validationResult=ValidationResult.FAILED;
                            break;
                        }
                    }
                    System.out.println("result "+validationResult);
                    System.out.println("Data r "+DatatypeConverter.printHexBinary(dataRead));
                    System.out.println("Data r "+DatatypeConverter.printHexBinary(decryptedData));
                    BoxValidation.this.setValidationResult(validationResult);
                    validationComplete();
                    onValidationComplete(validationResult);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(1000*5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        private void encrypt(int[] v,int[] k){
            int v0=v[0], v1=v[1], i;
            int sum=0;
            int delta= (short) 0x9e37;
            int k0=k[0], k1=k[1], k2=k[2], k3=k[3];
            for (i=0; i<16; i++) {
                sum= ((sum+delta)&0xFFFF);
                v0 = (v0+(((v1<<4) + k0) ^ (v1 + sum) ^ ((v1>>5) + k1)))&0xFFFF;
                v1 = (v1+(((v0<<4) + k2) ^ (v0 + sum) ^ ((v0>>5) + k3)))&0xFFFF;
            }
            v[0]= (short) (v0&0xFFFF); v[1]= (short) (v1&0xFFFF);
        }
    }
}
