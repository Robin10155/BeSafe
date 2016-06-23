import com.google.common.io.LittleEndianDataInputStream;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by robin on 12/5/16.
 */
public class ByteToDoubleAdapter {
    private InputStream stream;
    public ByteToDoubleAdapter(InputStream stream){
        this.stream=stream;
    }

    public int read(double[] data,int off,int len) throws IOException {
        byte[] audioBuffer=new byte[len*2];
        stream.read(audioBuffer,0,len*2);
        int i;
        for(i=0;i<len;i++){
            short val= (short) ((audioBuffer[2*i]&0xFF)|((audioBuffer[2*i+1]<<8)&0xFF00));
            data[i]=val/32768.0;
        }
        return i;
    }
}
