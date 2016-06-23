import javax.sound.sampled.*;
import javax.swing.plaf.synth.SynthTextAreaUI;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by robin on 10/5/16.
 */
public class MicrophoneInputStream extends InputStream{
    public static final int SAMPLES_RATE = 44100;
    private TargetDataLine microphone;
    public MicrophoneInputStream(){
        AudioFormat format = new AudioFormat(SAMPLES_RATE, 16, 1, true, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        try {

            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Line not supported");
                System.exit(0);
            }
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
            for (Mixer.Info infos: mixerInfos){
                Mixer m = AudioSystem.getMixer(infos);
                Line.Info[] lineInfos;// = m.getSourceLineInfo();

                lineInfos = m.getTargetLineInfo();
                for (Line.Info lineInfo:lineInfos){
                    if(m.getMixerInfo().getName().equals("Device [plughw:1,0]"))
                    {
                        try {
                            microphone = (TargetDataLine) m.getLine(lineInfo);
                        } catch (LineUnavailableException e) {
                            e.printStackTrace();
                        }
                    }
//                    System.out.println(m.getLineInfo()+" "+lineInfo.getLineClass()+" "+ m.getMixerInfo().getName());

                }
            }
            if(microphone==null)
                return;
            microphone.open(format);
            System.out.println("Microphone buffer size"+microphone.getBufferSize());
            microphone.start();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }
    public int read() throws IOException {
//        byte[] one=new byte[1];
//        microphone.read(one,0,1);
//        return one[0];
        throw new IOException("Not supported");
    }

    @Override
    public synchronized void reset() throws IOException {
        microphone.drain();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return microphone.read(b,off,len);
    }

    @Override
    public int available() throws IOException {
        return microphone.available();
    }

    @Override
    public void close() throws IOException {
        microphone.close();
    }
}
