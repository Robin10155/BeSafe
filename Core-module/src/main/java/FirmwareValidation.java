import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by robin on 27/5/16.
 */
public class FirmwareValidation extends ModuleValidation{
    public FirmwareValidation() {
        setMaxTrailsAllowed(1);
        setValidationSkipTime(-1);
        setModuleName("Firmware");
        MessageDigest md = null;
        String md5Checksum=null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try(InputStream is= new FileInputStream("/home/pi/MicValidation/out/artifacts/SystemValidation_jar/SystemValidation.jar");
            DigestInputStream dis=new DigestInputStream(is,md)){
            int len;
            byte[] buffer=new byte[1024];
            while((len=dis.read(buffer,0,buffer.length))!=-1){

            }
            byte[] digest=md.digest();
            md5Checksum= DatatypeConverter.printHexBinary(digest);
            System.out.println(DatatypeConverter.printHexBinary(digest));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try(InputStream is=new FileInputStream("/home/pi/md5Checksum.txt");
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(is))){
            String fileMd5=bufferedReader.readLine();
            if(fileMd5.equals(md5Checksum))
                setValidationResult(ValidationResult.PASSED);
            else
                setValidationResult(ValidationResult.FAILED);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

    }
}
