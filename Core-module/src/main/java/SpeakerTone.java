import javax.sound.sampled.*;
import java.io.*;
import java.util.concurrent.Exchanger;

/**
 * Created by robin on 10/5/16.
 */
public class SpeakerTone implements Runnable{
    private static final int SAMPLE_RATE=44100;
    private int frequency;
    private long code;
    private int codeLength;

    public double[] getSequence() {
        return sequence;
    }

    private double codeDurationMillis;
    private SourceDataLine speakers;
    private double[] sequence;
    private Object lock;
    private byte[] audioData;
    public SpeakerTone(int frequency, long code, int codeLength, int codeDurationMillis, Object lock){
        this.lock=lock;
        AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
        DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
        try {
            speakers = (SourceDataLine) AudioSystem.getLine(dataLineInfo);

            Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
            for (Mixer.Info infos: mixerInfos){
                Mixer m = AudioSystem.getMixer(infos);
                Line.Info[] lineInfos;// = m.getSourceLineInfo();

                lineInfos = m.getSourceLineInfo();
                for (Line.Info lineInfo:lineInfos){
                    if(m.getMixerInfo().getName().equals("Device [plughw:1,0]") &&m.getLine(lineInfo) instanceof SourceDataLine)
                    {
                        System.out.println(m.getLine(lineInfo));
//                        try {
//                            speakers = (SourceDataLine) m.getLine(lineInfo);
//                        } catch (LineUnavailableException e) {
//                            e.printStackTrace();
//                        }
                    }
                    //System.out.println(m.getMixerInfo().getDescription()+" "+m.getLineInfo()+" "+lineInfo.getLineClass()+" "+ m.getMixerInfo().getName());
                }
            }
            speakers.open(format);
            speakers.start();
            System.out.println("Speaker buffer size"+speakers.getBufferSize());
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }

        this.frequency=frequency;
        this.code=code;
        this.codeLength=codeLength;
        this.codeDurationMillis=codeDurationMillis;
        this.sequence=new double[codeDurationMillis*codeLength/10];
        int codeIndex=0;
        int timeCount=0;
        for(int i=0;i<sequence.length;i++){
            sequence[i]=((code&(1<<codeIndex))!=0?1.0:-1.0);
            timeCount++;
            if(timeCount==codeDurationMillis/10){
                timeCount=0;
                codeIndex++;
            }
        }
        this.audioData=new byte[76816];
        int audioDataIndex=0;
        long tempCode=code;
        codeIndex=0;
        int codeVal= (int) (tempCode&(0x1<<codeIndex));
        int nextCodeVal = (int)(tempCode&(0x1<<(codeIndex+1)));
        int prevCode=(codeVal==0)?1:0;
        byte[] data=new byte[1024*2];
        double phase=0;
        int i;
        short val;
        double totalCount = 0;
        double dampingFactor=0;
        BufferedOutputStream bufferedOutputStream = null;
        try {
            bufferedOutputStream=new BufferedOutputStream(new FileOutputStream("/home/pi/data.raw"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        loop:while(true){
            for(i=0;i<data.length/2;phase+=(2*Math.PI*frequency)/SAMPLE_RATE,i++){
                if(phase>2*Math.PI)
                    phase-=2*Math.PI;
                if(totalCount<CHANGE_TIME){
                    dampingFactor=(totalCount/CHANGE_TIME);
                }else if(totalCount>(codeDurationMillis-CHANGE_TIME)){
                    dampingFactor=(codeDurationMillis-totalCount)/CHANGE_TIME;
                }else
                {
                    dampingFactor=1;
                }
                val= (short) (Math.sin(phase)*32768.f*dampingFactor);
                if(codeVal!=0) {
                    audioData[audioDataIndex++]=(byte) (val & 0xFF);
                    audioData[audioDataIndex++]=(byte) ((val >> 8) & 0xFF);
                    data[2 * i] = (byte) (val & 0xFF);
                    data[2 * i + 1] = (byte) ((val >> 8) & 0xFF);
                }else{
                    audioData[audioDataIndex++]=0;
                    audioData[audioDataIndex++]=0;
                    data[2 * i] = 0;
                    data[2 * i + 1] = 0;
                }
                //audioDataIndex+=2;
                totalCount+=(1000.0/SAMPLE_RATE);
                if(totalCount>codeDurationMillis){
                    codeIndex++;
                    if(codeIndex==codeLength) {
                        codeIndex = 0;
                        break loop;
                    }
                    codeVal= (int) (tempCode&(1<<codeIndex));
                    totalCount=0.0;
                }
            }
        }
        try {
            bufferedOutputStream.write(audioData, 0, audioDataIndex);
            bufferedOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        System.out.println("audio count"+audioDataIndex);
    }
    private static int CHANGE_TIME=3;
    public void run() {
        speakers.start();
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        play(audioData,0,audioData.length);
        speakers.drain();
    }
    public void close(){
        speakers.close();
    }
    private void play(byte[] data,int off,int len){
        speakers.write(data,off,len);
    }
}
