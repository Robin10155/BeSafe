import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

/**
 * Created by robin on 5/5/16.
 */
public class VorbisInputStream extends InputStream{
    public static final int VORBIS_CHANNELS=2;
    public static final int VORBIS_RATE=44100;
    public static final float VORBIS_QUALITY=0.3f;
    public ByteRingBuffer vorbisBuffer;
    private InputStream stream;
    static int READ = 1024;
    static byte[] readBuffer = new byte[READ*4+44];
    public VorbisInputStream(InputStream stream) {
        vorbisBuffer=new ByteRingBuffer(44100*4);
        byte[] array=init();
        vorbisBuffer.write(array,0,array.length);
        System.out.println("Array len "+array.length);
        this.stream=stream;
    }
    private native byte[] init();
    private byte internalBuffer[]=new byte[1];
    private byte buffer[]=new byte[1024];
    @Override
    public int read() throws IOException {
        System.out.println("read byte");
        int len=stream.read(buffer,0,1024);
        encode(buffer,len);
        if(vorbisBuffer.read(internalBuffer,0,1)!=-1){
            return internalBuffer[0];
        }else
            return -1;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        System.out.println("read byte array");
        int lenRead=0;
        while(vorbisBuffer.getUsed()<len&&lenRead!=-1) {
            lenRead = stream.read(buffer, 0, 1024);
            System.out.println("Stream read " + lenRead);
            if(lenRead!=-1) {
                byte[] arr = encode(buffer, lenRead);
                vorbisBuffer.write(arr, 0, arr.length);
                System.out.println("Length=" + arr.length);
            }else
                break;
        }
        int lenVorbis=vorbisBuffer.read(b,off,len);
        System.out.println("Len vorbis "+lenVorbis);
        return (lenVorbis==0)?-1:lenVorbis;
    }

    @Override
    public int available() throws IOException {
        System.out.print("available "+vorbisBuffer.getUsed());
        return vorbisBuffer.getUsed();
    }

    @Override
    public void close() throws IOException {
    }


    private native byte[] encode(byte[] arr, int len);
    static {
        System.loadLibrary("vorbisrpi");
    }
}
