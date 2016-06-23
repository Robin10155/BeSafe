import com.pi4j.io.gpio.*;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

/**
 * Created by robin on 12/5/16.
 */
public class PanicButtonValidation extends ModuleValidation{
    private GpioPinDigitalInput buttonDigitalInput;
    private GpioPinDigitalOutput validateDigitalOutput;
    public PanicButtonValidation(Pin buttonPin, Pin validatePin) {
        final GpioController gpio = GpioFactory.getInstance();
        validateDigitalOutput = gpio.provisionDigitalOutputPin(validatePin, "BJT Validate LED");
        validateDigitalOutput.low();
        buttonDigitalInput = gpio.provisionDigitalInputPin(buttonPin, "Panic button");
        DeboucingButton deboucingButton = new DeboucingButton(40);
        new Thread(deboucingButton).start();
        setMaxTrailsAllowed(1);
        setValidationSkipTime(5000);
        setModuleName("Panic Button");
    }

    @Override
    public void run() {
        ValidationResult result;
        int initialState=buttonDigitalInput.getState().getValue();
        if(initialState==0){
            setValidationResult(ValidationResult.FAILED);
            result=ValidationResult.FAILED;
            validationComplete();
            onValidationComplete(result);
            return;
        }
        validateDigitalOutput.high();
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(initialState==1&&buttonDigitalInput.getState().getValue()==0)
            result=ValidationResult.PASSED;
        else
            result=ValidationResult.FAILED;
        validateDigitalOutput.low();
        setValidationResult(result);
        validationComplete();
        onValidationComplete(result);

    }
    private void onRequestEmergency(){
        for(RequestEmergencyListener requestEmergencyListener:requestEmergencyListeners){
            requestEmergencyListener.onRequestEmergency(this);
        }
    }

    private List<RequestEmergencyListener> requestEmergencyListeners=new ArrayList<RequestEmergencyListener>();

    public void addRequestEmergencyListener(RequestEmergencyListener requestEmergencyListener){
        requestEmergencyListeners.add(requestEmergencyListener);
    }
    public void removeRequestEmergencyListener(RequestEmergencyListener requestEmergencyListener){
        requestEmergencyListeners.remove(requestEmergencyListener);
    }
    public boolean onButtonPressed(){
        if(!isValidating()) {
            onRequestEmergency();
            return false;
        }else{
            return true;
        }
    }
    public interface RequestEmergencyListener extends EventListener{
        void onRequestEmergency(PanicButtonValidation button);
    }
    private class DeboucingButton implements Runnable{
        private final int debouncingPeriod;
        public DeboucingButton(int debouncingPeriod) {
            this.debouncingPeriod=debouncingPeriod;
        }
        private int prevState=-1;
        private int currState;
        private boolean actionRequired=true;
        @Override
        public void run() {
            while (true){
                currState= PanicButtonValidation.this.buttonDigitalInput.getState().getValue();
                if(currState==0&&actionRequired){
                    actionRequired=onButtonPressed();
                }
                if(currState==1){
                    actionRequired=true;
                }
                try {
                    Thread.sleep(debouncingPeriod);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                prevState=currState;
            }
        }
    }
}
