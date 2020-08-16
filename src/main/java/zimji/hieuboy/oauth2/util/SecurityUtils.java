package zimji.hieuboy.oauth2.util;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import java.security.spec.KeySpec;

/**
 * @author HieuDT28 - (Hiếu Boy)
 * created 16/08/2020 - 14:08
 * https://stackoverflow.com/questions/10303767/encrypt-and-decrypt-in-java
 */

public class SecurityUtils {

    private static SecurityUtils instance = new SecurityUtils();
    private static final String UNICODE_FORMAT = "UTF8";
    public static final String DESEDE_ENCRYPTION_SCHEME = "DESede";
    public KeySpec keySpec;
    public SecretKeyFactory secretKeyFactory;
    public Cipher cipher;
    public byte[] arrayBytes;
    public String myEncryptionKey;
    public String myEncryptionScheme;
    public SecretKey key;

    public static SecurityUtils getInstance() {
        return instance;
    }

    public SecurityUtils() {
        try {
            myEncryptionKey = "ThisIsSpartaThisIsSparta";
            myEncryptionScheme = DESEDE_ENCRYPTION_SCHEME;
            arrayBytes = myEncryptionKey.getBytes(UNICODE_FORMAT);
            keySpec = new DESedeKeySpec(arrayBytes);
            secretKeyFactory = SecretKeyFactory.getInstance(myEncryptionScheme);
            cipher = Cipher.getInstance(myEncryptionScheme);
            key = secretKeyFactory.generateSecret(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String encrypt(String unencryptedString) {
        String encryptedString = null;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] plainText = unencryptedString.getBytes(UNICODE_FORMAT);
            byte[] encryptedText = cipher.doFinal(plainText);
            encryptedString = new String(Base64.encodeBase64(encryptedText));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedString;
    }

    public String decrypt(String encryptedString) {
        String decryptedText = null;
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] encryptedText = Base64.decodeBase64(encryptedString);
            byte[] plainText = cipher.doFinal(encryptedText);
            decryptedText = new String(plainText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decryptedText;
    }

    public static String getEncryptPassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }

    public static Boolean checkEncryptPassword(String password, String encodedPassword) {
        return new BCryptPasswordEncoder().matches(password, encodedPassword);
    }

    public static void main(String[] args) throws Exception {
        SecurityUtils securityUtils = new SecurityUtils();

        String target = "HieuDT28";
        String encrypted = securityUtils.encrypt(target);
        String decrypted = securityUtils.decrypt(encrypted);

        System.out.println("String To Encrypt: " + target);
        System.out.println("Encrypted String:" + encrypted);
        System.out.println("Decrypted String:" + decrypted);

    }

}
