import java.io.*;

/**
 * Created by robin on 15/5/16.
 */
public class SplitInputStream extends InputStream{
    private InputStream inputStream;
    private OutputStream outputStream;
    private ByteRingBuffer byteRingBuffer;
    private final Object lock=new Object();
    public SplitInputStream(InputStream inputStream, OutputStream outputStream) {
        this.inputStream=inputStream;
        this.outputStream=outputStream;
        this.byteRingBuffer=new ByteRingBuffer(44100*10);
        new Thread(new InternalInputStreamThread()).start();
    }

    @Override
    public int read() throws IOException {
        return 0;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        //System.out.println("Reading data "+len);
        synchronized (lock){
            while(byteRingBuffer.getUsed()<len){
                //System.out.println("trying to read data");
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return byteRingBuffer.read(b,0,len);
    }

    @Override
    public void close() throws IOException {
        super.close();
    }

    @Override
    public int available() throws IOException {
        return byteRingBuffer.getFree();
    }

    @Override
    public synchronized void reset() throws IOException {
        int len;
        synchronized (lock) {
            byteRingBuffer.clear();
        }
    }
    private byte[] dataBuffer=new byte[1000];
    private class InternalInputStreamThread implements Runnable{
        @Override
        public void run() {
            byte[] buffer=new byte[44100];
            int len;
            try {
                while((len=inputStream.read(dataBuffer,0,dataBuffer.length))!=-1){
                    synchronized (lock) {
                        outputStream.write(dataBuffer,0,len);
                        byteRingBuffer.write(dataBuffer,0,len);
                        //System.out.println("AddingData");
                        if(byteRingBuffer.getFree()<buffer.length){
                            //System.out.println("Removing data");
                            byteRingBuffer.read(buffer,0,buffer.length);
                        }
                        lock.notify();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
