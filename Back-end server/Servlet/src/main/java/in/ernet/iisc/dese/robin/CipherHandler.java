package in.ernet.iisc.dese.robin;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class CipherHandler {
	public static final String PRIVATE_KEY_LOC="/home/robin/private.der";
	public static final String PUBLIC_KEY_LOC="/home/robin/public.der";
	public static final String ALGORITHM = "RSA";
	
	private static CipherHandler handle=null;
	private PrivateKey privateKey;
	private PublicKey publicKey;
	private CipherHandler(){
		privateKey=getPrivateKey(PRIVATE_KEY_LOC);
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
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cipherText;
	}
	public byte[] decrypt(byte [] data){
		byte [] decipherText = null;
		try {
			final Cipher cipher  =Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, this.privateKey);
			decipherText = cipher.doFinal(data);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return decipherText;
		
	}
	public PublicKey getPublicKey(String fileName){
		try {
			File f = new File(fileName);
			FileInputStream fis = new FileInputStream(fileName);
			DataInputStream dis = new DataInputStream(fis);
			byte[] keybytes = new byte[(int)f.length()];
			dis.readFully(keybytes);
			dis.close();
			X509EncodedKeySpec spec=new X509EncodedKeySpec(keybytes);
			KeyFactory kf=KeyFactory.getInstance(ALGORITHM);
			return kf.generatePublic(spec);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public PrivateKey getPrivateKey(String fileName){
		try {
			File f = new File(fileName);
			FileInputStream fis = new FileInputStream(fileName);
			DataInputStream dis = new DataInputStream(fis);
			byte[] keybytes = new byte[(int)f.length()];
			dis.readFully(keybytes);
			dis.close();
			PKCS8EncodedKeySpec spec=new PKCS8EncodedKeySpec(keybytes);
			KeyFactory kf=KeyFactory.getInstance(ALGORITHM);
			return kf.generatePrivate(spec);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
