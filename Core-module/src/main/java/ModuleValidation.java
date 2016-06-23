import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.Objects;

public abstract class ModuleValidation implements Runnable{
    private Object lock=new Object();
    private String moduleName="Undefined";
    public enum ValidationResult{
        PASSED,
        FAILED,
        INVALID,
        VALIDATING
    }
    private long lastValidationTime=-1;
    private ValidationResult lastResult =ValidationResult.INVALID;
    private ValidationResult lastValidResult = ValidationResult.INVALID;
    private long validationSkipTime=5000;
    private long maxTrailsAllowed=1;
    private int trialNumber=0;
    private boolean validating=false;
    public ValidationResult validate(boolean force){
        if(validationSkipTime==-1)
            return lastResult;
        if(lastValidationTime+validationSkipTime>System.currentTimeMillis()&&!force&& lastResult !=ValidationResult.INVALID) {
            return lastResult;
        }else{
            trialNumber=0;
            revalidate();
            return ValidationResult.VALIDATING;
        }
    }
    public ValidationResult validate(){
        return validate(false);
    }

    protected long getValidationSkipTime() {
        return validationSkipTime;
    }

    protected long getMaxTrailsAllowed() {
        return maxTrailsAllowed;
    }

    protected void setMaxTrailsAllowed(long maxTrailsAllowed) {
        this.maxTrailsAllowed = maxTrailsAllowed;
    }

    protected void setValidationSkipTime(long validationSkipTime) {
        this.validationSkipTime = validationSkipTime;
    }

    protected void setValidationResult(ValidationResult result){
        synchronized (lock) {
            lastValidationTime = System.currentTimeMillis();
            lastResult = result;
            if (lastResult == ValidationResult.PASSED || lastResult == ValidationResult.FAILED)
                lastValidResult = lastResult;
        }
    }

    public ValidationResult getLastValidResult() {
        return lastValidResult;
    }

    public boolean isValidating() {
        return validating;
    }

    protected boolean revalidate(){
        trialNumber++;
        if(trialNumber>maxTrailsAllowed)
            return false;

        if (!this.validating) {
            this.validating = true;
            new Thread(this).start();
            return true;
        }
        return false;
    }
    protected void validationComplete(){
        this.validating=false;
    }
    public ValidationResult getValidationResult() {
        synchronized (lock) {
            if (this.validating)
                return ValidationResult.VALIDATING;
            if(validationSkipTime==-1)
                return lastResult;
            if (lastValidationTime + validationSkipTime > System.currentTimeMillis()) {
                return lastResult;
            } else {
                return ValidationResult.INVALID;
            }
        }
    }
    public abstract void run();
    private List<ValidationCompleteListener> validationCompleteListeners =new ArrayList<ValidationCompleteListener>();
    protected void onValidationComplete(ValidationResult result){
        for(ValidationCompleteListener validationCompleteListener:validationCompleteListeners)
            validationCompleteListener.onValidationComplete(this,result);
    }
    public interface ValidationCompleteListener extends EventListener {
        void onValidationComplete(ModuleValidation moduleValidation, ValidationResult result);
    }
    public void addValidationCompleteListener(ValidationCompleteListener modeuleValidationCompleteListener){
        validationCompleteListeners.add(modeuleValidationCompleteListener);
    }
    public void removeValidationCompleteListener(ValidationCompleteListener modeuleValidationCompleteListener){
        validationCompleteListeners.remove(modeuleValidationCompleteListener);
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }
}
