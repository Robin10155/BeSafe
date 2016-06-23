import org.apache.commons.io.IOUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class CipherHandler {
	public static final String PUBLIC_KEY_LOC="/public.der";
	public static final String ALGORITHM = "RSA";
	
	private static CipherHandler handle=null;
	private PrivateKey privateKey;
	private PublicKey publicKey;
	private CipherHandler(){
		publicKey=getPublicKey(PUBLIC_KEY_LOC);
	}
	public static CipherHandler getInstance(){
		if(handle==null)
			handle=new CipherHandler();
		return handle;
	}
	
	public byte[] encrypt(byte[] data){
		byte [] cipherText = null;
		try {
			final Cipher cipher  =Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, this.publicKey);
			cipherText = cipher.doFinal(data);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
		return cipherText;
	}
	public PublicKey getPublicKey(String fileName){
		try {

			InputStream inputStream=getClass().getResourceAsStream(fileName);
            byte[] keybytes = IOUtils.toByteArray(inputStream);
            X509EncodedKeySpec spec=new X509EncodedKeySpec(keybytes);
			KeyFactory kf=KeyFactory.getInstance(ALGORITHM);
			return kf.generatePublic(spec);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
