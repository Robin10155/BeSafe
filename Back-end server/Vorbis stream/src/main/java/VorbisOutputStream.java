import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by robin on 6/5/16.
 */
public class VorbisOutputStream extends OutputStream{
    private OutputStream stream;
    private int decoderID;
    public VorbisOutputStream(OutputStream stream){
        this.stream=stream;
        decoderID=init();
        System.out.println("ID is "+decoderID);
    }
    private native int init();
    private native int vorbis_init(int id,byte[] arr,int off, int len);
    public void write(int b) throws IOException {
        throw new IOException();
    }
    private boolean initialized=false;

    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if(!initialized){
            int code;
            switch(code=vorbis_init(decoderID,b,off,len)){
                case 3:
                    initialized=true;
                    break;
                case -1:
                    System.out.println("ERROR");

            }
            if(vorbis_init(decoderID,b,off,len)==3){
                initialized=true;
                System.out.println("Initialized");
            }
        }else{
            byte[] buffer=decode(decoderID,b,off,len);
            if(buffer!=null){
                stream.write(buffer,0,buffer.length);
            }
        }
    }
    private native void uinit(int id);

    @Override
    public void flush() throws IOException {
        stream.flush();
    }

    private native byte[] decode(int id,byte[] arr,int off, int len);
    static{
        System.loadLibrary("vorbisdecoder");
    }

    @Override
    public void close() throws IOException {
        stream.close();
        this.uinit(decoderID);
    }
}
