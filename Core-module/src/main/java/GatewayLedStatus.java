import com.pi4j.io.gpio.*;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by robin on 26/5/16.
 */
public class GatewayLedStatus {
    private Object lock=new Object();
    public enum Status{
        NORMAL,
        EMERGENCY,
        VALIDATION_FAILED
    }
    private Status status=Status.NORMAL;
    private Pin greenLED=RaspiPin.GPIO_02;
    private Pin redLED=RaspiPin.GPIO_03;
    private GpioPinDigitalOutput greenLEDOutput;
    private GpioPinDigitalOutput redLEDOutput;
    public GatewayLedStatus() {
        final GpioController gpio = GpioFactory.getInstance();
        greenLEDOutput = gpio.provisionDigitalOutputPin(greenLED, "GreenLED", PinState.HIGH);
        redLEDOutput = gpio.provisionDigitalOutputPin(redLED, "RedLED", PinState.LOW);
    }

    public Status getStatus() {
        return status;
    }
    private Timer timer;
    private DelayTask delayTask;
    public void setStatus(Status status) {
        synchronized (lock) {
            this.status = status;
            switch (status) {
                case NORMAL:
                    greenLEDOutput.setState(true);
                    redLEDOutput.setState(false);
                    break;
                case EMERGENCY:
                case VALIDATION_FAILED:
                    greenLEDOutput.setState(false);
                    redLEDOutput.setState(true);
                    if(timer!=null){
                        timer.cancel();
                    }
                    timer=new Timer();
                    timer.schedule(new DelayTask(),5000);
            }
        }
    }
    private class DelayTask extends TimerTask{
        @Override
        public void run() {
            synchronized (lock) {
                if (GatewayLedStatus.this.status == Status.EMERGENCY || status == Status.VALIDATION_FAILED){
                    GatewayLedStatus.this.setStatus(Status.NORMAL);
                }
            }
        }
    }
}
