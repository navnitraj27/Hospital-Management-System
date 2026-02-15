package utils;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.security.spec.*;

public class EncryptionUtil {
    private static final String CIPHER = "AES/CBC/PKCS5Padding";
    private static final String KDF = "PBKDF2WithHmacSHA256";
    private static final int ITER = 65536;
    private static final int KEYLEN = 256;

    public static byte[] encrypt(String password, byte[] plain) throws Exception {
        if (password == null || password.isEmpty())
            throw new IllegalArgumentException("Empty password");
        SecureRandom rnd = new SecureRandom();
        byte[] salt = new byte[16];
        rnd.nextBytes(salt);
        byte[] iv = new byte[16];
        rnd.nextBytes(iv);
        SecretKey key = deriveKey(password, salt);
        Cipher c = Cipher.getInstance(CIPHER);
        c.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
        byte[] cipher = c.doFinal(plain);
        byte[] out = new byte[16 + 16 + cipher.length];
        System.arraycopy(salt, 0, out, 0, 16);
        System.arraycopy(iv, 0, out, 16, 16);
        System.arraycopy(cipher, 0, out, 32, cipher.length);
        return out;
    }

    public static byte[] decrypt(String password, byte[] input) throws Exception {
        if (input == null || input.length < 32)
            return new byte[0];
        byte[] salt = new byte[16];
        byte[] iv = new byte[16];
        System.arraycopy(input, 0, salt, 0, 16);
        System.arraycopy(input, 16, iv, 0, 16);
        byte[] cipher = new byte[input.length - 32];
        System.arraycopy(input, 32, cipher, 0, cipher.length);
        SecretKey key = deriveKey(password, salt);
        Cipher c = Cipher.getInstance(CIPHER);
        c.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        return c.doFinal(cipher);
    }

    private static SecretKey deriveKey(String password, byte[] salt) throws Exception {
        SecretKeyFactory f = SecretKeyFactory.getInstance(KDF);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITER, KEYLEN);
        byte[] raw = f.generateSecret(spec).getEncoded();
        return new SecretKeySpec(raw, "AES");
    }
}