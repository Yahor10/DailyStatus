package by.android.dailystatus.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public final class CryptUtils {

	private CryptUtils() {

	}

	/**
	 * encrypt user pass phrase
	 * 
	 * @param message
	 * @throws Exception
	 * @return encrypt bytes
	 */

	public static byte[] encryptPassSection(String message) throws Exception {
		String salt = "quick brown fox ";
		SecretKeySpec key = new SecretKeySpec(salt.getBytes(), "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return cipher.doFinal(message.getBytes());
	}

	/**
	 * decrypt user pass phrase
	 * 
	 * @param bytes
	 * @throws Exception
	 * @return decrypt bytes
	 */
	public static byte[] decryptPassSection(byte[] bytes) throws Exception {
		String salt = "quick brown fox ";
		SecretKeySpec key = new SecretKeySpec(salt.getBytes(), "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, key);
		return cipher.doFinal(bytes);
	}
}
