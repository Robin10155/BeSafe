import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Exchanger;

/**
 * Created by robin on 12/5/16.
 */
public class MicrophoneFFT{
    private static int SAMPLE_RATE=44100;
    private DoubleFFT_1D fft;
    private static final int FFT_LENGTH=441;
    private double[] audioBuffer;
    private double[] fftBuffer;
    private int toneFreq;
    private static int toneband=50;
    private ByteToDoubleAdapter byteToDoubleAdapter;
    private InputStream microphoneInputStream;;
    private Object lock;
    public MicrophoneFFT(int toneFreq, InputStream inputStream, Object lock){
        this.lock=lock;
        this.toneFreq=toneFreq;
        this.fft=new DoubleFFT_1D(FFT_LENGTH);
        this.audioBuffer=new double[FFT_LENGTH];
        this.fftBuffer=new double[FFT_LENGTH*2];
        microphoneInputStream=inputStream;
        BufferedInputStream bufferedInputStream;
        //bufferedInputStream = new BufferedInputStream(microphoneInputStream);
        byteToDoubleAdapter = new ByteToDoubleAdapter(microphoneInputStream);
        int len;
        try {
            byteToDoubleAdapter.read(audioBuffer,0,audioBuffer.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private int freqToIndex(int freq){
        return (freq*FFT_LENGTH/SAMPLE_RATE);
    }

    private boolean last;

    public boolean isLast() {
        return last;
    }
    private static final int FFT_CAPTURED_LIMIT=70;
    private boolean stopExec=false;
    public void stop(){
        stopExec=true;
    }
    public void run() {
        stopExec=false;
        try {
            microphoneInputStream.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int fftCaptured = 0;
        int len;
        double power=0.0;
        int bandCount=0;
        boolean firstTime=true;
        try {
            while((len=byteToDoubleAdapter.read(audioBuffer,0,audioBuffer.length))!=-1){
                if(stopExec)
                    return;
                for(int i=0;i<len;i++){
                    fftBuffer[2*i]=audioBuffer[i];
                    fftBuffer[2*i+1]=0;
                }
                fft.complexForward(fftBuffer);
                bandCount=0;
                power=0;
                for (int i = freqToIndex(toneFreq-toneband); i <= freqToIndex(toneFreq+toneband); i++) {
                    double vlen=Math.sqrt(fftBuffer[2*i]*fftBuffer[2*i]+fftBuffer[2*i+1]*fftBuffer[2*i+1]);
                    bandCount++;
                    power+=vlen;
                }
                //System.out.println("band "+bandCount);
                power/=bandCount;
                //power=(power>1.0)?1.0:-1.0;
                power=(power>0.4)?1.0:-1.0;

                if(fftCaptured == FFT_CAPTURED_LIMIT-1)
                    last=true;
                else
                    last=false;
                onFFTComplete(fftBuffer.clone(),power);
                fftCaptured++;
                if(fftCaptured ==FFT_CAPTURED_LIMIT)
                    break;
                if(firstTime){
                    synchronized (lock){
                        lock.notify();
                    }
                    System.out.println("First fft captured");
                    firstTime=false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void close(){
        try {
            microphoneInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void addFFTCompleteListener(FFTCompleteListener FFTCompleteListener){
        FFTCompleteListeners.add(FFTCompleteListener);
    }
    public void removeFFTCompleteListener(FFTCompleteListener FFTCompleteListener){
        FFTCompleteListeners.remove(FFTCompleteListener);
    }
    private void onFFTComplete(double[] fftPlot,double power){
        for(FFTCompleteListener FFTCompleteListener : FFTCompleteListeners){
            FFTCompleteListener.onFFTComplete(this,fftPlot,power);
        }
    }
    private List<FFTCompleteListener> FFTCompleteListeners =new ArrayList<FFTCompleteListener>();
    public interface FFTCompleteListener {
        void onFFTComplete(MicrophoneFFT microphoneFFT,double[] fftPlot,double power);
    }
}
