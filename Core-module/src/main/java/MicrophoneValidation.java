import javafx.beans.binding.ListExpression;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.concurrent.Exchanger;

/**
 * Created by robin on 13/5/16.
 */
public class MicrophoneValidation extends ModuleValidation{

    private static final double CORRELATION_THRESHOLD=1.0;
    private SpeakerTone speakerTone;
    private MicrophoneFFT microphoneFFT;
    private Object lock=new Object();
    public MicrophoneValidation(InputStream inputStream) {
        speakerTone=new SpeakerTone(15000,0b10100101,8,50,lock);
        microphoneFFT=new MicrophoneFFT(15000,inputStream,lock);
        microphoneFFT.addFFTCompleteListener((microphoneFFT1, fftPlot, power) -> updateCorrelation(fftPlot,power));
        setMaxTrailsAllowed(3);
        setValidationSkipTime(5000);
        setModuleName("Microphone");
    }

    @Override
    public void run() {
        onValidationStart();
        for(int j=0;j<timeSeries.length;j++){
            timeSeries[j]=-1;
        }
        prevCorrelationValue=-1;
        validationPassed=false;
        Thread speakerThread = new Thread(speakerTone);
        speakerThread.start();
        microphoneFFT.run();
    }
    private double prevCorrelationValue=-1;
    private double currCorrelationValue=-1;
    private boolean validationPassed=false;
    private double[] timeSeries=new double[100];
    private void updateCorrelation(double[] fftBuffer,double power){

        ValidationResult result;
        for(int j=0;j<timeSeries.length-1;j++){
            timeSeries[j]=timeSeries[j+1];
        }
        timeSeries[timeSeries.length-1]=power;
        //System.out.println("Power "+power);
        currCorrelationValue=correlation(speakerTone.getSequence(),timeSeries)/20;
        if((prevCorrelationValue>=currCorrelationValue)&&(prevCorrelationValue>CORRELATION_THRESHOLD)){
            result=ValidationResult.PASSED;
            synchronized (this) {
                setValidationResult(result);
                System.out.println(getModuleName() + " " + getValidationResult());
                microphoneFFT.stop();
                validationPassed = true;
                validationComplete();
                onValidationComplete(result);
            }
            return;
        }
        if(microphoneFFT.isLast()&&!validationPassed) {
            validationComplete();
            if (!revalidate()) {
                result=ValidationResult.FAILED;
                microphoneFFT.stop();
                setValidationResult(result);
                onValidationComplete(result);

            }
        }
        prevCorrelationValue=currCorrelationValue;
    }


    private double correlation(double[] data1,double[] data2){
        double rel=0.0;
        for(int i=0;i<Math.min(data1.length,data2.length);i++){
            rel+=data1[data1.length-i-1]*data2[data2.length-i-1];
        }
        return rel;
    }

    public List<ValidationStartListener> validationStartListeners=new ArrayList<ValidationStartListener>();
    private void onValidationStart(){
        for(ValidationStartListener validationStartListener:validationStartListeners){
            validationStartListener.onValidationStart(this);
        }
    }
    public interface ValidationStartListener extends EventListener{
        void onValidationStart(MicrophoneValidation microphoneValidation);
    }
    public void addValidationStartListener(ValidationStartListener validationStartListener){
        validationStartListeners.add(validationStartListener);
    }
    public void removeValidationStartListener(ValidationStartListener validationStartListener){
        validationStartListeners.remove(validationStartListener);
    }

}
